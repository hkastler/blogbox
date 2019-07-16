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

    String body;
    StringBuilder bodyBuilder = new StringBuilder();
    StringBuilder text = new StringBuilder();
    StringBuilder html = new StringBuilder();

    private static final Logger LOG = Logger.getLogger(BlogMessageBody.class.getName());

    public BlogMessageBody(Message msg) {
        this.body = buildBody(msg);
    }

    public String buildBody(Message msg) {
        try {
            MimeMultipart mp;
            Object content = msg.getContent();
            Boolean isMimeMultipart = content instanceof MimeMultipart;

            if (isMimeMultipart) {
                mp = (MimeMultipart) content;

                for (int i = 0; i < mp.getCount(); i++) {
                    buildPart(mp.getBodyPart(i));
                }
                if (this.html.length() > 0) {
                    this.bodyBuilder.append(html);

                } else {
                    this.bodyBuilder.append(text);
                }

            } else {
                this.bodyBuilder.append((String) msg.getContent());
            }
        } catch (IOException | MessagingException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return processHtml(bodyBuilder.toString());
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
                this.text.append(ctext);
            } else if (p.isMimeType(MediaType.TEXT_HTML)) {
                String chtml = (String) p.getContent();
                this.html.append(chtml);
            } else if (o instanceof BASE64DecoderStream) {
                String contentType = p.getDataHandler().getContentType();
                if (contentType.contains("image")) {
                    handleImage(p);
                } else if (contentType.contains("pdf")) {
                    handlePdf(p);
                } else {
                    LOG.log(Level.INFO, "unhandled Base64 content type {0} found in content", p.getDataHandler().getContentType());
                }
            }

        } catch (MessagingException | IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    String getBase64String(Part p) {
        String b64 = "";
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            p.getDataHandler().writeTo(baos);
            byte[] attBytes = baos.toByteArray();
            b64 = Base64.getEncoder().encodeToString(attBytes);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return b64;
    }

    void handleImage(Part p) throws MessagingException {

        String imageString = getBase64String(p);
        // get the contentType ensure no attachment name
        String[] aryContentType = p.getDataHandler().getContentType().split(";");

        String contentType = aryContentType[0];

        String template = "<div class=\"blgmsgimg\"><img src=\"data:{0};base64, {1} \" /></div>";
        String imgTag = MessageFormat.format(template, contentType, imageString);

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

        String compiledCidFinder = MessageFormat.format("cid:{0}", imgId);

        if (this.html.length() > 0) {
            if (p.getDisposition().equals("inline")) {
                String currentHtml = this.html.toString();
                if (!currentHtml.contains(compiledCidFinder)) {
                    currentHtml = currentHtml.concat(imgTag);
                } else {
                    currentHtml = replaceCidImgTag(currentHtml, compiledCidFinder, imgTag);
                }
                this.html.setLength(0);
                this.html.append(currentHtml);
            } else {
                this.html.append(imgTag);
            }
        } else {
            this.text.append(imgTag);
        }
    }

    String replaceCidImgTag(String html, String imgSelectorValue, String replacement) {
        Document doc = Jsoup.parse(html);
        String imgSelector = MessageFormat.format("img[src*=\"{0}\"]", imgSelectorValue);
        Optional<Element> oImg = Optional.ofNullable(doc.selectFirst(imgSelector));
        if (oImg.isPresent()) {
            Element img = oImg.get();
            img.before(replacement);
            img.remove();
        }
        return doc.toString();
    }

    void handlePdf(Part p) throws MessagingException {
        
        String pdfString = getBase64String(p);
        String[] aryContentType = p.getDataHandler().getContentType().split(";");
        String contentType = aryContentType[0];
        String template = "<div class=\"blgmsgpdf\"><a href=\"data:{0};base64, {1} \">{2}</a></div>";
        String pdfTag = MessageFormat.format(template, contentType, pdfString, p.getFileName());
        if (this.html.length() > 0) {
            this.html.append(pdfTag);
        } else {
            this.text.append(pdfTag);
        }

    }

    public String[] contentTypes(String getContentType) {
        String nStr = getContentType;
        String[] aryStr = nStr.split(";");
        Arrays.parallelSetAll(aryStr, i -> aryStr[i].trim());
        return aryStr;
    }

    public String getBody() {
        return body;
    }

    private String processHtml(String html) {
        Document doc = Jsoup.parse(html);
        Element htmlBody = doc.body();

        Optional<Element> sig = Optional.ofNullable(doc.select("div:contains(Sent from Yahoo Mail)").first());
        if (sig.isPresent()) {
            sig.get().remove();
        }

        Whitelist wl = new RelaxedPlusDataBase64();
        
        String safe = Jsoup.clean(htmlBody.html(), wl.preserveRelativeLinks(true));
        System.out.println(safe);
        return StringUtil.normaliseWhitespace(safe);
    }

}
