package com.hkstlr.blogbox.control;

import com.hkstlr.blogbox.entities.BlogMessage;
import com.hkstlr.blogbox.entities.BlogMessageTest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;

/**
 *
 * @author henry.kastler
 */
public class BlogMessageTestHelper {

    Logger log = Logger.getLogger(BlogMessageTest.class.getName());

    public BlogMessageTestHelper() {
        super();
    }

    public BlogMessage getBlogMessageFromEmlFile(String emlFile) throws IOException, MessagingException {
        return new BlogMessage(getMessageFromEmlFile(emlFile));
    }
    
    public BlogMessageBody getBlogMessageBodyFromEmlFile(String emlFile) throws IOException, MessagingException {
        return new BlogMessageBody(getMessageFromEmlFile(emlFile));   
    }
    
    Message getMessageFromEmlFile(String emlFile){
        
        Properties props = System.getProperties();
        props.put("mail.mime.charset", "UTF-8");
        Session session = Session.getDefaultInstance(props, null);
        try (InputStream is = ClassLoader.getSystemResourceAsStream(emlFile)) {
            Message message = new MimeMessage(session, is);
            return message;
        } catch (Exception e) {
            log.log(Level.INFO, "getBlogMessageFromEmlFile", e);
        }
        return null;
        
    }

}
