package com.hkstlr.blogbox.entities;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;
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

import com.hkstlr.blogbox.control.BlogMessageBody;
import com.hkstlr.blogbox.control.DateFormatter;
import com.hkstlr.blogbox.control.StringChanger;
import com.hkstlr.blogbox.control.StringPool;

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

    

    @Id
    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.messageId.NotNull}")
    @Size(min = 1, max = 255, message = "{BlogMessage.messageId.NotNull}")
    @Column(name = "messageId", nullable = false, length = 255)
    private String messageId = StringPool.STRING;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.messageNumber.NotNull}")
    @Column(name = "messageNumber", nullable = false)
    private Integer messageNumber = 0;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.href.NotNull}")
    @Size(min = 1, max = 255, message = "{BlogMessage.href.NotNull}")
    @Column(name = "href", nullable = false, length = 255)
    private String href = StringPool.STRING;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.subject.NotNull}")
    @Size(min = 1, max = 255, message = "{BlogMessage.subject.NotNull}")
    @Column(name = "subject", nullable = false, length = 255)
    private String subject = StringPool.STRING;

    @Basic(optional = false)
    @NotNull(message = "{BlogMessage.body.NotNull}")
    @Size(min = 1, message = "{BlogMessage.body.NotNull}")
    @Column(name = "body", nullable = false, length = 10485760)
    private String body = StringPool.STRING;

    @Column(name = "createDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date createDate;
    
    
    public static final String TITLE_SEPARATOR = StringPool.DASH;
    

    public BlogMessage() {
        super();
    }

    public BlogMessage(Message msg) throws MessagingException {
        super();
        this.setMessageIdFromMsg(msg);
        this.messageNumber = msg.getMessageNumber();
        this.createDate = msg.getReceivedDate();
        this.subject = defaultCreateSubject(msg.getSubject());
        this.body = new BlogMessageBody(this.messageId,msg).getBody();
        this.href = defaultCreateHref();
    }

    public BlogMessage(Message msg, Integer hrefWordMax) throws MessagingException {
        super();
        this.setMessageIdFromMsg(msg);
        this.messageNumber = msg.getMessageNumber();
        this.createDate = msg.getReceivedDate();
        this.subject = defaultCreateSubject(msg.getSubject());
        this.body = new BlogMessageBody(this.messageId,msg).getBody();
        this.href = createHref(hrefWordMax);
    }

    public BlogMessage(Message msg, String subjectRegex, Integer hrefWordMax) throws MessagingException {
        super();
        this.setMessageIdFromMsg(msg);
        this.messageNumber = msg.getMessageNumber();
        this.createDate = msg.getReceivedDate();
        this.subject = createSubject(msg.getSubject(), subjectRegex);
        this.body = new BlogMessageBody(this.messageId,msg).getBody();
        this.href = createHref(hrefWordMax);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setMessageIdFromMsg(Message msg) throws MessagingException {
        String[] rpls = {StringPool.LESS_THAN, StringPool.GREATER_THAN};
        this.messageId = Optional.ofNullable(msg.getHeader("Message-ID")[0])
                            .orElse(getRandomDoubleAsString());
        for(String rpl : rpls){
            this.messageId = this.messageId.replace(rpl, StringPool.STRING);
        }
        this.messageId = this.messageId.split(StringPool.AT)[0];
    }

    private String getRandomDoubleAsString(){
        return Double.toHexString(new SecureRandom().nextDouble());
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
        return Optional.ofNullable(msg.getHeader("Content-Type")[0].split(StringPool.SEMICOLON)[0]).orElse("null");
    }

    private String defaultCreateSubject(String msgSubject) {
        String DEFAULT_SUBJECTREGEX = "[Bb]log";
        return createSubject(msgSubject, DEFAULT_SUBJECTREGEX);
    }

    private String createSubject(String msgSubject, String rfRegex) {
        String lsub = StringPool.STRING;
        try {
            lsub = MimeUtility.decodeText(msgSubject);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BlogMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        lsub = lsub.replaceFirst(rfRegex, StringPool.STRING);
        lsub = lsub.trim();
        if (lsub.length() == 0) {
            lsub = msgSubject;
        }
        return lsub;
    }

    private String defaultCreateHref(){
        Integer DEFAULT_HREFWORDMAX = 10;
        return createHref(DEFAULT_HREFWORDMAX);
    }

    /**
     * Create href for blog, based on title or text
     */
    private String createHref(Integer numberOfWordsInUrl) {
        final char CHAR = ' ';
        // Use title (minus non-alphanumeric characters)
        StringBuilder base = new StringBuilder();
        if (!this.subject.isEmpty()) {
            base.append(StringChanger.replaceNonAlphanumeric(this.subject, CHAR).trim());
        }
        // If we still have no base, then try body (minus non-alphanumerics)
        if (base.length() == 0 && !this.body.isEmpty()) {
            base.append(StringChanger.replaceNonAlphanumeric(this.body, CHAR).trim());
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
        }
        return response;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.messageId, this.href);
    }

}
