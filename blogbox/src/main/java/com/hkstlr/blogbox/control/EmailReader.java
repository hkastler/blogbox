package com.hkstlr.blogbox.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.FetchProfile;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.search.DateTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.hkstlr.blogbox.boundary.event.BlogboxEventManager;
import com.hkstlr.blogbox.entities.BlogMessage;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPSSLStore;

@Stateless
public class EmailReader {

    public static final String SUPPORTED_PROTOCOL = "imaps";
    Properties props = new Properties();
    Session session;
    IMAPSSLStore store;

    String folderName;
    IMAPFolder blogBox;

    @EJB
    BlogboxEventManager em;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    public EmailReader() {
        super();
    }

    public EmailReader(final Properties props) {
        super();
        this.props = props;
        init();
    }

    public void init() {

        this.folderName = props.getProperty(EmailReaderPropertyKeys.FOLDER_NAME);

        this.session = getSessionFromContext(props.getProperty(EmailReaderPropertyKeys.JNDI_NAME, "java:/mail/BlogboxIMAPS"));

        if (this.session != null) {
            log.log(Level.INFO, "getting mail session from container");
            storeConnectContainer();
        } else {
            log.log(Level.INFO, "getting mail session from local client");
            storeConnectLocalClient();
        }

        if (this.store.isConnected()) {
            try {
                this.blogBox = (IMAPFolder) store.getFolder(this.folderName);
                this.blogBox.open(IMAPFolder.READ_ONLY);

            } catch (final MessagingException e) {
                log.log(Level.SEVERE, "init()", e);
            }
        } else {
            log.log(Level.SEVERE, "init() not connected");
        }

    }

    public Session getSessionFromContext(final String jndiName) {
        
        try {
            final InitialContext ctx = new InitialContext();
            final Object lookup = ctx.lookup(jndiName);
            if(lookup instanceof Session){
                return (Session) lookup;
            } else {
                return null;
            }
            
        } catch (final NamingException e1) {
            log.log(Level.FINE, "jndiName not found", e1);
        }
        return null;
    }

    public boolean storeConnectContainer() {
        try {
            setStore();
            this.store.connect(this.session.getProperty(EmailReaderPropertyKeys.MAIL_IMAPS_HOST),
                    this.session.getProperty("mail.imaps.user"), this.session.getProperty("mail.imaps.password"));

        } catch (final MessagingException e) {
            log.log(Level.INFO, "storeConnectContainer()", e);
        }
        return this.store.isConnected();
    }

    public boolean storeConnectLocalClient() {
        this.session = Session.getInstance(this.props, null);
        this.session.setDebug(false);
        final String mailhost = props.getProperty(EmailReaderPropertyKeys.MAIL_IMAPS_HOST,
                EmailReaderPropertyKeys.MAIL_IMAPS_HOST);
        final String username = props.getProperty(EmailReaderPropertyKeys.USERNAME, EmailReaderPropertyKeys.USERNAME);
        final String password = props.getProperty(EmailReaderPropertyKeys.PASSWORD, EmailReaderPropertyKeys.PASSWORD);
        try {
            setStore();
            this.store.connect(mailhost, username, password);
        } catch (final MessagingException e) {
            log.log(Level.SEVERE, "storeConnectLocalClient()", e);
        }

        return this.store.isConnected();
    }

    public void setStore() throws NoSuchProviderException {
        this.store = (IMAPSSLStore) this.session.getStore();
    }

    public void storeClose() {
        try {
            this.store.close();
        } catch (final MessagingException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        this.store = null;
        this.session = null;
    }

    public Message[] getImapEmails() {

        try {
            if (!this.blogBox.isOpen()) {
                this.blogBox.open(IMAPFolder.READ_ONLY);
            }
            final Message[] msgs = this.blogBox.getMessages();
            final FetchProfile fp = new FetchProfile();
            fp.add(IMAPFolder.FetchProfileItem.MESSAGE);
            this.blogBox.fetch(msgs, fp);
            return msgs;

        } catch (final MessagingException e) {
            log.log(Level.WARNING, "getImapEmails()", e);
        }

        return new Message[0];
    }

    public Message[] searchLatestMessages(final Date givenDate) {
        log.log(Level.INFO, "date:{0}", givenDate.toString());

        final SearchTerm st = new ReceivedDateTerm(DateTerm.GT, givenDate);

        try {
            if (!this.blogBox.isOpen()) {
                this.blogBox.open(IMAPFolder.READ_ONLY);
            }
            final Message[] messages = this.blogBox.search(st);
            return messages;
        } catch (final MessagingException e) {
            log.log(Level.SEVERE, "searchLatestMessages()", e);
        }
        return new Message[0];

    }

    public int getMessageCount() {
        int msgCount = 0;

        try {

            if (!blogBox.isOpen()) {
                blogBox.open(IMAPFolder.READ_ONLY);
            }

            msgCount = blogBox.getMessageCount();

        } catch (final MessagingException e) {
            log.log(Level.WARNING, "getMessageCount()", e);
        }
        return msgCount;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(final Properties props) {
        this.props = props;
    }

    public Boolean setBlogMessages(final Integer hrefMaxWords) {
        final List<String> hrefs = new ArrayList<>();

        Arrays.asList(getImapEmails()).parallelStream().forEach(msg -> {
            try {
                if (!blogBox.isOpen()) {
                    blogBox.open(IMAPFolder.READ_ONLY);
                }
                final BlogMessage bmsg = new BlogMessage(msg, hrefMaxWords);

                if (hrefs.contains(bmsg.getHref())) {
                    bmsg.makeHrefUnique();
                }
                hrefs.add(bmsg.getHref());
                em.saveBlogMessage(bmsg);
            } catch (final MessagingException e) {
                log.log(Level.WARNING, "bmsg", e);
            } finally {
                // do nothing
            }
        });
        storeClose();
        // convert to String array for payload type safety
        em.deleteByHrefNotIn(hrefs.toArray(new String[hrefs.size()]));
        log.log(Level.INFO, "{0} bmgs returned", new Object[] { Integer.toString(hrefs.size()) });
        return true;
    }

    public Boolean searchLatestBlogMessages(final Integer hrefMaxWords, final Date date) {
        final AtomicInteger runCount = new AtomicInteger(0);
        Arrays.asList(searchLatestMessages(date)).parallelStream().forEach(msg -> {
            try {
                if (!blogBox.isOpen()) {
                    blogBox.open(IMAPFolder.READ_ONLY);
                }
                // search works only by date, not datetime
                if (msg.getReceivedDate().after(date)) {
                    final BlogMessage bmsg = new BlogMessage(msg, hrefMaxWords);
                    bmsg.makeHrefUnique();
                    em.saveBlogMessage(bmsg);
                    runCount.getAndIncrement();
                }

            } catch (final MessagingException e) {
                log.log(Level.WARNING, "bmsg", e);
            } finally {
                // do nothing
            }
        });
        storeClose();
        log.log(Level.INFO, "{0} bmgs returned", new Object[] { runCount.toString() });
        return true;
    }

    public class EmailReaderPropertyKeys {

        public static final String FOLDER_NAME = "folderName";
        public static final String MAIL_IMAPS_HOST = "mail.imaps.host";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String STORE_PROTOCOL = "mail.store.protocol";
        public static final String JNDI_NAME = "jndiName";

        private EmailReaderPropertyKeys() {
            // strings
        }
    }

}
