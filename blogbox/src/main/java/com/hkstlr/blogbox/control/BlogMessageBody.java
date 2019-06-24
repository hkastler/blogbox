package com.hkstlr.blogbox.control;

import com.sun.mail.util.BASE64DecoderStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public final class BlogMessageBody {

    Message msg;
    StringBuilder body = new StringBuilder();
    StringBuilder text = new StringBuilder();
    StringBuilder html = new StringBuilder();
    StringBuilder imgs = new StringBuilder();

    private static final Logger LOG = Logger.getLogger(BlogMessageBody.class.getName());

    public BlogMessageBody() {
        super();
    }

    public BlogMessageBody(Message msg) {
        this.msg = msg;
        setBody();
    }

    public String[] contentTypes(String getContentType) {
        String nStr = getContentType;
        String[] aryStr = nStr.split(";");
        Arrays.parallelSetAll(aryStr, (i) -> aryStr[i].trim());
        return aryStr;
    }

    public void setBody() {
        try {
            MimeMultipart mp;
            Object content = msg.getContent();
            Boolean isMimeMultipart = content instanceof MimeMultipart;

            if (isMimeMultipart) {
                mp = (MimeMultipart) content;

                for (int i = 0; i < mp.getCount(); i++) {
                    buildPart(mp.getBodyPart(i));
                }
                if (html.length() > 0) {
                    body.append(html);

                } else {
                    body.append(text);
                }

            } else {
                body.append((String) msg.getContent());
            }
        } catch (IOException | MessagingException ex) {
            Logger.getLogger(BlogMessageBody.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public String getBody() {
        return processHtml(body.toString());
    }

    void buildPart(Part p) {
        try {
            Object o = p.getContent();
            if (o instanceof Multipart) {
                Multipart mp = (Multipart) o;
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    buildPart(mp.getBodyPart(i));
                }
            }
            if (p.isMimeType(MediaType.TEXT_PLAIN)) {
                String ctext = (String) p.getContent();
                text.append(ctext);
            } else if (p.isMimeType(MediaType.TEXT_HTML)) {
                String chtml = (String) p.getContent();
                html.append(chtml);
            } else if (o instanceof BASE64DecoderStream) {
                DataHandler dh = p.getDataHandler();
                if (dh.getContentType().contains("image/")) {
                    String imageString;
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        dh.writeTo(baos);
                        byte[] attBytes = baos.toByteArray();
                        imageString = Base64.getEncoder().encodeToString(attBytes);
                        baos.close();
                    }
                    if (imageString.isEmpty()) {

                    }

                    // get the contentType ensure no attachment name
                    String[] aryContentType = dh.getContentType().split(";");

                    String contentType = aryContentType[0];

                    String template = "<div class=\"blgmsgimg\"><img src=\"data:{0};base64, {1} \" /></div>";
                    String imgTag = MessageFormat.format(template, new Object[]{contentType, imageString});
                    String cidPh = "<img src=\"cid:{0}\" id=\"{0}\">";

                    String partId = "";
                    if (aryContentType.length > 1) {
                        partId = aryContentType[1];
                    }

                    String imgId = partId;
                    if (partId.contains("=")) {
                        imgId = partId.split("=")[1];
                    }
                    Optional<String[]> cidHeader = Optional.ofNullable(p.getHeader("Content-Id"));

                    if (cidHeader.isPresent()) {

                        String contentId = Optional.ofNullable(cidHeader.get()[0]).orElse("");
                        String ncid = contentId.replaceAll("<", "");
                        ncid = ncid.replaceAll(">", "");
                        imgId = ncid;
                    }
                    String placeholder = MessageFormat.format(cidPh, new Object[]{imgId});

                    if (html.length() > 0) {
                        if (p.getDisposition().equals("inline")) {
                            String compile = html.toString();
                            if (!compile.contains(placeholder)) {
                                compile = compile.concat(imgTag);
                            } else {
                                compile = compile.replaceAll(placeholder, imgTag);                           }
                            html.setLength(0);
                            html.append(compile);
                        } else {
                            html.append(imgTag);
                            
                        }
                    } else {
                        text.append(imgTag);                        
                    }

                }

            }

        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "", e);
        } catch (IOException ioex) {
            System.out.println("Cannot get content" + ioex.getMessage());
        }
    }
    
    private String processHtml(String html) {
        Document doc = Jsoup.parse(html);
        Element htmlBody = doc.body();

        Optional<Element> sig = Optional.ofNullable(doc.select("div:contains(Sent from Yahoo Mail)").first());
        if (sig.isPresent()) {
            sig.get().remove();
        }

        Whitelist wl = Whitelist.relaxed();
        wl.addProtocols("img", "src", "http", "https", "data", "cid");
        wl.addAttributes("div", "style");
        wl.addAttributes("div", "class");

        wl.addTags("font");
        wl.addAttributes("font", "size");
        wl.addAttributes("font", "color");

        String safe = Jsoup.clean(htmlBody.html(), wl);
        return StringUtil.normaliseWhitespace(safe);
    }

}
