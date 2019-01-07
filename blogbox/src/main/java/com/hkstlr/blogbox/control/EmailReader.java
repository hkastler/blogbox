package com.hkstlr.blogbox.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.mail.FetchProfile;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import com.hkstlr.blogbox.entities.BlogMessage;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPSSLStore;

public class EmailReader {


	public static final String SUPPORTED_PROTOCOL = "imaps";
	Properties props = new Properties();
	Session session;
	IMAPSSLStore store;
	String mailhost;
	String folderName;

	IMAPFolder blogBox;
	String username;
	String password;

	String protocol;

	private final Logger log = Logger.getLogger(this.getClass().getName());

	public EmailReader(Properties props) {
		super();
		this.props = props;
		init();
	}

	@PostConstruct
	void init() {
		this.mailhost = props.getProperty(EmailReaderPropertyKeys.MAIL_IMAPS_HOST, EmailReaderPropertyKeys.MAIL_IMAPS_HOST);
		this.protocol = SUPPORTED_PROTOCOL;
		this.username = props.getProperty(EmailReaderPropertyKeys.USERNAME, EmailReaderPropertyKeys.USERNAME);
		this.password = props.getProperty(EmailReaderPropertyKeys.PASSWORD, EmailReaderPropertyKeys.PASSWORD);
		this.folderName = props.getProperty(EmailReaderPropertyKeys.FOLDER_NAME);

		try {
			storeConnect();
			this.blogBox = (IMAPFolder)store.getFolder(this.folderName);
			this.blogBox.open(IMAPFolder.READ_ONLY);
		} catch (MessagingException e) {
			log.log(Level.SEVERE, "init()", e);
		}

	}

	public boolean storeConnect() {
		this.session = Session.getDefaultInstance(this.props, null);
		try {
			this.store = (IMAPSSLStore)session.getStore(this.protocol);
			store.connect(this.mailhost, this.username, this.password);
		} catch (MessagingException e) {
			log.log(Level.SEVERE, "storeConnect()", e);
		}

		return this.store.isConnected();
	}

	public void storeClose() {
		try {
			this.store.close();
		} catch (MessagingException e) {
			log.log(Level.WARNING, "storeClose()", e);
		}
	}

	public Message[] getImapEmails() {
		
		try {
			Message[] msgs = blogBox.getMessages();
			FetchProfile fp = new FetchProfile();
			fp.add(IMAPFolder.FetchProfileItem.MESSAGE);
			blogBox.fetch(msgs, fp);
			return msgs;

		} catch (MessagingException e) {
			storeClose();
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

	public List<BlogMessage> setBlogMessages(List<BlogMessage> bmsgs, Integer hrefMaxWords) {
		log.log(Level.INFO, "{0} bmgs setBlogMessages", new Object[]{Integer.toString(bmsgs.size())});
		List<String> hrefs = new ArrayList<>();
		log.log(Level.INFO, "getImapEmails");
		
		for (Message msg : getImapEmails()) {
			log.log(Level.INFO, "{0} msg.number", new Object[]{Integer.toString(msg.getMessageNumber())});
			if (!blogBox.isOpen()) {
				try {
					blogBox.open(IMAPFolder.READ_ONLY);
				} catch (MessagingException e) {
					log.log(Level.WARNING, "setBlogMessages:blogBox.open", e);
				}
			}

			try {
				BlogMessage bmsg = new BlogMessage(msg, hrefMaxWords);
			
				if( hrefs.contains(bmsg.getHref()) ){
					bmsg.makeHrefUnique();
				}
				hrefs.add(bmsg.getHref());
				bmsgs.add(bmsg);

			} catch (IOException | MessagingException e) {
				log.log(Level.WARNING, "bmsg", e);
				continue;
			} finally {
				storeClose();
			}
		}
		
		log.log(Level.INFO, "{0} bmgs returned}", new Object[]{Integer.toString(bmsgs.size())});
		return bmsgs;
	}

	
	public class EmailReaderPropertyKeys {

		public static final String FOLDER_NAME = "folderName";
		public static final String MAIL_IMAPS_HOST = "mail.imaps.host";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String STORE_PROTOCOL = "mail.store.protocol";

		private EmailReaderPropertyKeys() {
			// strings
		}
	}

}
