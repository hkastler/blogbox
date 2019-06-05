package com.hkstlr.blogbox.entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.MediaType;

import com.hkstlr.blogbox.control.DateFormatter;
import com.hkstlr.blogbox.control.StringChanger;
import com.sun.mail.util.BASE64DecoderStream;
import java.io.UnsupportedEncodingException;
import javax.mail.internet.MimeUtility;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

@Entity
@Cacheable
@Table(name = "BlogMessage", uniqueConstraints = {
    @UniqueConstraint(columnNames = "href")})
@NamedQuery(name = "BlogMessage.findAll", query = "SELECT b FROM BlogMessage b")
@NamedQuery(name = "BlogMessage.findByMessageId", query = "SELECT b FROM BlogMessage b WHERE b.messageId = :messageId")
@NamedQuery(name = "BlogMessage.findByHref", query = "SELECT b FROM BlogMessage b WHERE b.href = :href")
@NamedQuery(name = "BlogMessage.findByMessageNumber", query = "SELECT b FROM BlogMessage b WHERE b.messageNumber = :messageNumber")
@NamedQuery(name = "BlogMessage.findMessageNumberRange", query = "SELECT b FROM BlogMessage b WHERE b.messageNumber BETWEEN :messageNumberStart AND :messageNumberEnd")
public class BlogMessage {

    private static final String STRING = "";

    @Id
    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.messageId.NotNull}")
    @Size(min = 1, max = 255, message = "{BlogMessage.messageId.NotNull}")
    @Column(name = "messageId", nullable = false, length = 255)
    private String messageId = STRING;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.messageNumber.NotNull}")
    @Column(name = "messageNumber", nullable = false)
    private Integer messageNumber = 0;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.href.NotNull}")
    @Size(min = 1, max = 255, message = "{BlogMessage.href.NotNull}")
    @Column(name = "href", nullable = false, length = 255)
    private String href = STRING;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.subject.NotNull}")
    @Size(min = 1, max = 255, message = "{BlogMessage.subject.NotNull}")
    @Column(name = "subject", nullable = false, length = 255)
    private String subject = STRING;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.body.NotNull}")
    @Size(min = 1, message = "{BlogMessage.body.NotNull}")
    @Column(name = "body", nullable = false, length = 10485760)
    private String body = STRING;

    @Column(name = "createDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createDate;

    private static final Logger LOG = Logger.getLogger(BlogMessage.class.getName());
    private static final String DEFAULT_SUBJECTREGEX = "[Bb]log";
    public static final Integer DEFAULT_HREFWORDMAX = 10;
    public static final String TITLE_SEPARATOR = "-";

    public BlogMessage() {
        super();
    }

    public BlogMessage(Message msg) throws MessagingException, IOException {
        super();
        this.messageId = Optional.ofNullable(msg.getHeader("Message-ID")[0]).orElse(Double.toHexString(Math.random()));
        this.messageNumber = msg.getMessageNumber();
        this.createDate = msg.getReceivedDate();
        this.subject = createSubject(msg.getSubject(), DEFAULT_SUBJECTREGEX);
        this.body = processMultipart(msg);
        this.href = createHref(DEFAULT_HREFWORDMAX);
    }

    public BlogMessage(Message msg, Integer hrefWordMax) throws MessagingException, IOException {
        super();
        this.messageId = Optional.ofNullable(msg.getHeader("Message-ID")[0]).orElse(Double.toHexString(Math.random()));
        this.messageNumber = msg.getMessageNumber();
        this.createDate = msg.getReceivedDate();
        this.subject = createSubject(msg.getSubject(), DEFAULT_SUBJECTREGEX);
        this.body = processMultipart(msg);
        this.href = createHref(hrefWordMax);
    }

    public BlogMessage(Message msg, String subjectRegex, Integer hrefWordMax) throws MessagingException, IOException {
        super();
        this.messageId = Optional.ofNullable(msg.getHeader("Message-ID")[0]).orElse(Double.toHexString(Math.random()));
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

    String getContentTypeFromHeader(Message msg) throws MessagingException {
        return Optional.ofNullable(msg.getHeader("Content-Type")[0].split(";")[0]).orElse("null");

    }

    StringBuilder buildPart(StringBuilder sb, Part p) {
        try {
            Object o = p.getContent();
            if (o instanceof Multipart) {
                Multipart mp = (Multipart) o;
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    buildPart(sb, mp.getBodyPart(i));
                }
            }

            if (p.isMimeType(MediaType.TEXT_HTML)) {
                String html = (String) p.getContent();

                sb.append(processHtml(html));
            }
            
            if (o instanceof BASE64DecoderStream) {
                
                DataHandler dh = p.getDataHandler();
                
                String imageString;
                if (dh.getContentType().contains("image/")) {
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
                    String cidPh = "<img src=\"cid\\:{0}\" id=\"{0}\">";
                    
                    String partId = "";
                    if(aryContentType.length > 1){
                        partId = aryContentType[1];
                    }
                    
                    String imgId = partId;
                    if(partId.contains("=")){
                        imgId = partId.split("=")[1];
                    }
                    
                    String placeholder = MessageFormat.format(cidPh, new Object[]{imgId});
                    int plIdx = sb.toString().indexOf(placeholder);
                    
                    if(plIdx == -1 ){
                        sb.append(imgTag);                       
                    }else{
                       
                        String compile = sb.toString().replaceAll(placeholder, imgTag);
                        sb.setLength(0);
                        sb.append(compile);
                    }
                    
                }

            }

        } catch (MessagingException e) {
            LOG.log(Level.SEVERE, "", e);
        } catch (IOException ioex) {
            System.out.println("Cannot get content" + ioex.getMessage());
        }

        return sb;
    }

    private String processMultipart(Message msg) throws IOException, MessagingException {

        if ("java.lang.String".equals(msg.getContent().getClass().getCanonicalName())) {
            return msg.getContent().toString();
        }
        
        MimeMultipart mp;
        Object content = msg.getContent();
        Boolean isMimeMultipart = content instanceof MimeMultipart;
        StringBuilder sb = new StringBuilder();
        if (isMimeMultipart) {
            mp = (MimeMultipart) content;
            for (int i = 0; i < mp.getCount(); i++) {
                buildPart(sb, mp.getBodyPart(i));
            }
        }
        
         
        return sb.toString();
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
        String lsub = "";
        try {
            lsub = MimeUtility.decodeText(msgSubject);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BlogMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        lsub = lsub.replaceFirst(rfRegex, STRING);
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
        tmpHref.append(BlogMessage.TITLE_SEPARATOR).append(new DateFormatter(createDate).formatjsFormat());
        this.href = tmpHref.toString();

    }

    @Override
    public boolean equals(Object o) {
        boolean response = false;
        if (o instanceof BlogMessage) {
            response = (((BlogMessage) o).href).equals(this.href)
                    && (((BlogMessage) o).messageId).equals(this.messageId);
            ;
        }
        return response;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.messageId, this.href);
    }

}
