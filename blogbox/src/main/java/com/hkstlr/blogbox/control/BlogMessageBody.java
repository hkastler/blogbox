package com.hkstlr.blogbox.control;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.core.MediaType;

import com.sun.mail.util.BASE64DecoderStream;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public final class BlogMessageBody {

    String messageId;
    String body;
    StringBuilder bodyBuilder = new StringBuilder();
    StringBuilder text = new StringBuilder();
    StringBuilder html = new StringBuilder();

    private Logger LOG = Logger.getLogger(BlogMessageBody.class.getName());

    public BlogMessageBody(String messageId, Message msg) {
        this.messageId = messageId;
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
                    LOG.log(Level.INFO, "unhandled Base64 content type {0} found in content",
                            p.getDataHandler().getContentType());
                }
            }

        } catch (MessagingException | IOException e) {
            LOG.log(Level.SEVERE, null, e);
        }

    }

    void handleImage(Part p) throws MessagingException, IOException {

        PartFileWriter pfw = new PartFileWriter(this.messageId, p);
        pfw.createFile();
        String template = "<div class=\"blgmsgimg\"><img src=\"/assets/imgs/{0}\" /></div>";
        String imgTag = MessageFormat.format(template, pfw.getFileName());

        String[] aryContentType = contentTypes(p.getDataHandler().getContentType());
        String partId = StringPool.STRING;
        if (aryContentType.length > 1) {
            partId = aryContentType[1];
        }

        String imgId = partId;
        if (partId.contains(StringPool.EQUALS)) {
            imgId = partId.split(StringPool.EQUALS)[1];
        }

        Optional<String[]> cidHeader = Optional.ofNullable(p.getHeader("Content-Id"));
        if (cidHeader.isPresent()) {
            String contentId = Optional.ofNullable(cidHeader.get()[0]).orElse(StringPool.STRING);
            if (!contentId.isBlank()) {
                String ncid = contentId.replaceAll(StringPool.LESS_THAN, StringPool.STRING);
                ncid = ncid.replaceAll(StringPool.GREATER_THAN, StringPool.STRING);
                imgId = ncid;
            }
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

        PartFileWriter pfw = new PartFileWriter(this.messageId, p);
        pfw.fileLocation = "/etc/opt/blogbox/assets/pdfs/";
        pfw.createFile();

        String template = "<div class=\"blgmsgpdf\"><a href=\"/assets/pdfs/{0}\">{1}</a></div>";
        String pdfTag = MessageFormat.format(template, pfw.getFileName(), p.getFileName());
        if (this.html.length() > 0) {
            this.html.append(pdfTag);
        } else {
            this.text.append(pdfTag);
        }

    }

    public String getPartContentType(String[] contentTypes) throws MessagingException {
        return contentTypes[0];
    }

    public String[] contentTypes(String contentTypes) {
        String nStr = contentTypes;
        String[] aryStr = nStr.split(";");
        Arrays.parallelSetAll(aryStr, i -> aryStr[i].trim());
        return aryStr;
    }

    public String getBody() {
        return body;
    }

    private String processHtml(String html) {
        String lHtml = newLinesToBrs(html);
        Document doc = Jsoup.parse(lHtml);
        Element htmlBody = doc.body();

        Optional<Element> sig = Optional.ofNullable(doc.select("div:contains(Sent from Yahoo Mail)").first());
        if (sig.isPresent()) {
            sig.get().remove();
        }

        Whitelist wl = new RelaxedPlusDataBase64();

        String safe = Jsoup.clean(htmlBody.html(), wl.preserveRelativeLinks(true));

        return StringUtil.normaliseWhitespace(safe);
    }

    private String newLinesToBrs(String html) {
        return html.replaceAll("(\r\n|\n)", "<br/>");
    }

}
