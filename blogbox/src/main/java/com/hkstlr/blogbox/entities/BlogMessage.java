package com.hkstlr.blogbox.entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;

import com.hkstlr.blogbox.control.DateFormatter;
import com.hkstlr.blogbox.control.StringChanger;
import com.sun.mail.util.BASE64DecoderStream;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

public class BlogMessage {

    private String messageId;
    private int messageNumber;
    private Date createDate;
    private String subject;
    private String body;
    private String href;

    private BlogMessage(BlogMessageBuilder builder) {
        super();
        this.messageId = builder.getMessageId();
        this.messageNumber = builder.getMessageNumber();
        this.createDate = builder.getCreateDate();
        this.subject = builder.getSubject();
        this.body = builder.getBody();
        this.href = builder.getHref();
    }

    public String getMessageId() {
        return messageId;
    }

    public int getMessageNumber() {
        return messageNumber;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getHref() {
        return href;
    }

    public static final class BlogMessageBuilder {
        private String messageId;
        private int messageNumber;
        private Date createDate;
        private String subject;
        private String body;
        private String href;
        private static final String DEFAULT_SUBJECTREGEX = "[Bb]log";
        public static final Integer DEFAULT_HREFWORDMAX = 10;

        public static final String TITLE_SEPARATOR = "-";

        public BlogMessageBuilder() {
            super();
        }

        public BlogMessageBuilder(Message msg) throws MessagingException, IOException {
            super();
            setBlogMessage(msg, DEFAULT_SUBJECTREGEX, DEFAULT_HREFWORDMAX);
        }

        public BlogMessageBuilder(Message msg, Integer hrefWordMax) throws MessagingException, IOException {
            super();
            setBlogMessage(msg, DEFAULT_SUBJECTREGEX, hrefWordMax);
        }

        // The only method to initiate BlogMessage class
        public BlogMessage build() {
            return new BlogMessage(this);
        }

        public void setBlogMessage(Message msg, String subjectRegex, Integer hrefWordMax)
                throws MessagingException, IOException {
            this.messageId = Optional.ofNullable(msg.getHeader("Message-ID")[0])
                    .orElse(Double.toHexString(Math.random()));
            this.messageNumber = msg.getMessageNumber();
            this.createDate = msg.getReceivedDate();
            this.subject = createSubject(msg.getSubject(), subjectRegex);
            this.body = processMultipart(msg);
            this.href = createHref(hrefWordMax);
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        /**
         * @return the messageNumber
         */
        public int getMessageNumber() {
            return messageNumber;
        }

        /**
         * @param messageNumber the messageNumber to set
         */
        public void setMessageNumber(int messageNumber) {
            this.messageNumber = messageNumber;
        }

        public Date getCreateDate() {
            return createDate;
        }

        public void setCreateDate(Date createDate) {
            this.createDate = createDate;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        private String processMultipart(Message msg) throws IOException, MessagingException {

            if ("java.lang.String".equals(msg.getContent().getClass().getCanonicalName())) {
                return msg.getContent().toString();
            }

            Multipart multipart = (Multipart) msg.getContent();
            StringBuilder content = new StringBuilder();
            BodyPart part;
            Optional<BodyPart> textPart = Optional.empty();
            Optional<BodyPart> htmlPart = Optional.empty();
            Optional<List<String>> imgs = Optional.empty();

            for (int i = 0; i < multipart.getCount(); i++) {

                part = multipart.getBodyPart(i);

                if (part.getContentType().contains(MediaType.TEXT_PLAIN)) {
                    textPart = Optional.of(part);

                } else if (part.getContentType().contains(MediaType.TEXT_HTML)) {
                    htmlPart = Optional.of(part);

                } else if (part.getContentType().contains("multipart/alternative")) {

                    DataHandler mh = part.getDataHandler();
                    MimeMultipart mm = (MimeMultipart) mh.getContent();

                    for (int m = 0; m < mm.getCount(); m++) {
                        BodyPart p = mm.getBodyPart(m);
                        if (p.getContentType().contains(MediaType.TEXT_HTML)) {
                            htmlPart = Optional.of(p);
                        }
                    }

                }

                if (part.getContent() instanceof BASE64DecoderStream) {

                    DataHandler dh = part.getDataHandler();
                    if (dh.getContentType().contains("image/")) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        dh.writeTo(baos);

                        byte[] attBytes = baos.toByteArray();
                        String imageString = Base64.getEncoder().encodeToString(attBytes);
                        baos.close();

                        // get the contentType ensure no attachment name
                        String contentType = dh.getContentType().split(";")[0];

                        String template = "<div class=\"blgmsgimg\"><img src=\"data:{0};base64, {1} \" /></div>";
                        String imgTag = MessageFormat.format(template, new Object[] { contentType, imageString });
                        if (imgs.isPresent()) {
                            imgs.get().add(imgTag);
                        } else {
                            List<String> is = new ArrayList<>();
                            is.add(imgTag);
                            imgs = Optional.of(is);
                        }
                    }

                }
            }

            if (htmlPart.isPresent()) {
                content.append(processHtml((String) htmlPart.get().getContent()));
            } else if (textPart.isPresent()) {
                content.append((String) textPart.get().getContent());
            }
            if (imgs.isPresent()) {
                for (String img : imgs.get()) {
                    content.append(img);
                }

            }
            return content.toString();
        }

        private String processHtml(String html) {
            Document doc = Jsoup.parse(html);
            Element htmlBody = doc.body();

            Optional<Element> sig = Optional.ofNullable(doc.select("div:contains(Sent from Yahoo Mail)").first());
            if (sig.isPresent()) {
                sig.get().remove();
            }

            Whitelist wl = Whitelist.relaxed();
            wl.addAttributes("div", "style");
            wl.addTags("font");
            wl.addAttributes("font", "size");
            wl.addAttributes("font", "color");

            String safe = Jsoup.clean(htmlBody.html(), wl);
            return StringUtil.normaliseWhitespace(safe);
        }

        private String createSubject(String msgSubject, String rfRegex) {

            String lsub = msgSubject;
            lsub = lsub.replaceFirst(rfRegex, "");
            lsub = lsub.trim();
            if (lsub.length() == 0) {
                lsub = msgSubject;
            }
            return lsub;

        }

        /**
         * Create href for blog, based on title or text
         */
        private String createHref(@NotNull Integer numberOfWordsInUrl) {

            // Use title (minus non-alphanumeric characters)
            StringBuilder base = new StringBuilder();
            if (!this.subject.isEmpty()) {
                base.append(StringChanger.replaceNonAlphanumeric(this.subject, ' ').trim());
            }
            // If we still have no base, then try body (minus non-alphanumerics)
            if (base.length() == 0 && !this.body.isEmpty()) {
                base.append(StringChanger.replaceNonAlphanumeric(this.body, ' ').trim());
            }

            if (base.length() > 0) {

                StringTokenizer toker = new StringTokenizer(base.toString());
                StringBuilder tmp = new StringBuilder();
                int count = 0;
                while (toker.hasMoreTokens() && count < numberOfWordsInUrl) {
                    String s = toker.nextToken();
                    s = s.toLowerCase();
                    if (tmp.length() == 0) {
                        tmp.append(s);
                    } else {

                        tmp.append(TITLE_SEPARATOR).append(s);
                    }
                    count++;
                }
                base = tmp;
            } // No title or text, so instead we will use the items date
              // in YYYYMMDD format as the base anchor
            else {

                base.append(new DateFormatter(this.createDate).format8chars());
            }

            return base.toString();
        }

        public void makeHrefUnique() {
            StringBuilder tmpHref = new StringBuilder(this.href);
            tmpHref.append(BlogMessageBuilder.TITLE_SEPARATOR).append(new DateFormatter(createDate).formatjsFormat());
            this.href = tmpHref.toString();
        }
    }

}
