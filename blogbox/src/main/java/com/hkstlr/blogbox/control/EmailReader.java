package com.hkstlr.blogbox.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.mail.FetchProfile;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

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

    @Inject
    Event<BlogMessageEvent> event;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    public EmailReader() {
        super();
    }

    public EmailReader(Properties props) {
        super();
        this.props = props;
        init();
    }

    public void init() {

        this.folderName = props.getProperty(EmailReaderPropertyKeys.FOLDER_NAME);

        setSessionFromContext(props.getProperty(EmailReaderPropertyKeys.JNDI_NAME, "java:/mail/BlogboxIMAPS"));

        if (this.session != null) {
            log.log(Level.INFO, "getting mail session from container");
            storeConnectContainer();
        } else {
            log.log(Level.INFO, "getting mail session from local client");
            storeConnectLocalClient();
        }

        try {
            this.blogBox = (IMAPFolder) store.getFolder(this.folderName);
            this.blogBox.open(IMAPFolder.READ_ONLY);

        } catch (MessagingException e) {
            log.log(Level.SEVERE, "init()", e);
        }

    }

    public void setSessionFromContext(String jndiName) {
        try {
            InitialContext ctx = new InitialContext();
            this.session = (Session) ctx.lookup(jndiName);
        } catch (NamingException e1) {
            log.log(Level.WARNING, "jndiName not found", e1);
        }
    }

    public boolean storeConnectContainer() {
        try {
            setStore();
            this.store.connect(this.session.getProperty(EmailReaderPropertyKeys.MAIL_IMAPS_HOST),
                    this.session.getProperty("mail.imaps.user"), this.session.getProperty("mail.imaps.password"));

        } catch (MessagingException e) {
            log.log(Level.INFO, "storeConnectContainer()", e);
        }
        return this.store.isConnected();
    }

    public boolean storeConnectLocalClient() {
        this.session = Session.getInstance(this.props, null);
        this.session.setDebug(false);
        String mailhost = props.getProperty(EmailReaderPropertyKeys.MAIL_IMAPS_HOST,
                EmailReaderPropertyKeys.MAIL_IMAPS_HOST);
        String username = props.getProperty(EmailReaderPropertyKeys.USERNAME, EmailReaderPropertyKeys.USERNAME);
        String password = props.getProperty(EmailReaderPropertyKeys.PASSWORD, EmailReaderPropertyKeys.PASSWORD);
        try {
            setStore();
            this.store.connect(mailhost, username, password);
        } catch (MessagingException e) {
            log.log(Level.SEVERE, "storeConnectLocalClient()", e);
        }

        return this.store.isConnected();
    }

    public void setStore() throws NoSuchProviderException {
        this.store = (IMAPSSLStore) this.session.getStore();
    }

    public void storeClose() throws MessagingException {
        this.store.close();        
    }

    public Message[] getImapEmails() {

        try {
            Message[] msgs = this.blogBox.getMessages();
            FetchProfile fp = new FetchProfile();
            fp.add(IMAPFolder.FetchProfileItem.MESSAGE);
            this.blogBox.fetch(msgs, fp);
            return msgs;

        } catch (MessagingException e) {
            log.log(Level.WARNING, "getImapEmails()", e);
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

        } catch (MessagingException e) {
            log.log(Level.WARNING, "getMessageCount()", e);
        }
        return msgCount;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public Boolean setBlogMessages(Integer hrefMaxWords) throws MessagingException {

        List<String> hrefs = new ArrayList<>();

        Arrays.asList(getImapEmails()).parallelStream().forEach(msg -> {

            try {
                if (!blogBox.isOpen()) {
                    blogBox.open(IMAPFolder.READ_ONLY);
                }

                BlogMessage bmsg = new BlogMessage(msg, hrefMaxWords);

                if (hrefs.contains(bmsg.getHref())) {
                    bmsg.makeHrefUnique();
                }
                hrefs.add(bmsg.getHref());
                event.fire(new BlogMessageEvent("save", bmsg));

            } catch (MessagingException e) {
                log.log(Level.WARNING, "bmsg", e);
            } finally {
                //do nothing
            }
        });

        storeClose();
        //convert to String array for payload type safety
        event.fire(new BlogMessageEvent("deleteByHrefNotIn", hrefs.toArray(new String[hrefs.size()])));
        log.log(Level.INFO, "{0} bmgs returned", new Object[]{Integer.toString(hrefs.size())});
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
