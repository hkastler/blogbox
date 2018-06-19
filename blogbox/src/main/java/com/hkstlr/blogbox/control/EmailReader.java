package com.hkstlr.blogbox.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import com.hkstlr.blogbox.entities.BlogMessage;
import com.sun.mail.imap.IMAPFolder;

public class EmailReader {

	public class EmailReaderPropertyKeys {

		public final static String FOLDER_NAME = "folderName";
		public final static String MAIL_IMAP_HOST = "mail.imap.host";
		public final static String USERNAME = "username";
		public final static String PASSWORD = "password";
		public final static String STORE_PROTOCOL = "mail.store.protocol";

		private EmailReaderPropertyKeys() {
			// strings
		}
	}

	public final static String DEFAULT_PROTOCOL = "imaps";
	Properties props = new Properties();
	Session session;
	Store store;
	String mailhost;
	String folderName;

	Folder blogBox;
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
		this.mailhost = props.getProperty(EmailReaderPropertyKeys.MAIL_IMAP_HOST, "hostname");
		this.protocol = props.getProperty(EmailReaderPropertyKeys.STORE_PROTOCOL, DEFAULT_PROTOCOL);
		this.username = props.getProperty(EmailReaderPropertyKeys.USERNAME, "username");
		this.password = props.getProperty(EmailReaderPropertyKeys.PASSWORD, "password");
		this.folderName = props.getProperty(EmailReaderPropertyKeys.FOLDER_NAME);

		try {

			storeConnect();
			
			this.blogBox = store.getFolder(this.folderName);

			this.blogBox.open(Folder.READ_ONLY);			

		} catch (MessagingException e) {
			log.log(Level.SEVERE, "init()", e);
		}

	}

	public Message[] getImapEmails() {

		try {
			
			Message msgs[] = blogBox.getMessages();
			FetchProfile fp = new FetchProfile();
			fp.add(IMAPFolder.FetchProfileItem.MESSAGE);
			blogBox.fetch(msgs, fp);
			return msgs;

		} catch (MessagingException e) {
			storeClose();
			log.log(Level.WARNING, "", e);
		}

		return null;

	}

	public int getMessageCount() {
		int msgCount = 0;
		
		try {

			if (!blogBox.isOpen()) {
				blogBox.open(Folder.READ_ONLY);
			}

			msgCount = blogBox.getMessageCount();

		} catch (MessagingException e) {
			log.log(Level.WARNING, "", e);
		}
		return msgCount;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public void storeClose() {

		try {
			this.store.close();
		} catch (MessagingException e) {
			log.log(Level.WARNING, "", e);
		}
	}

	public boolean storeConnect() {
		this.session = Session.getDefaultInstance(this.props, null);
		try {
			this.store = session.getStore(this.protocol);
			store.connect(this.mailhost, this.username, this.password);
		} catch (MessagingException e) {
			log.log(Level.SEVERE, "error", e);
		}

		return this.store.isConnected();
	}

	public ArrayList<BlogMessage> setBlogMessages(ArrayList<BlogMessage> bmsgs, Integer hrefMaxWords) {

		
		List<String> hrefs = new ArrayList<>();
		
		for (Message msg : getImapEmails()) {
			
			if (!blogBox.isOpen()) {
				try {
					blogBox.open(Folder.READ_ONLY);
				} catch (MessagingException e) {
					log.log(Level.WARNING, "setBlogMessages", e);
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
		return bmsgs;
	}

}
