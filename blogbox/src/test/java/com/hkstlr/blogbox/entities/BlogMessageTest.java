package com.hkstlr.blogbox.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.Test;

/**
 *
 * @author henry.kastler
 */
public class BlogMessageTest {

    BlogMessage cut;
    MimeMessage message;
    Logger log = Logger.getLogger(BlogMessageTest.class.getName());

    public BlogMessageTest() {
        super();
    }

    @Before
    public void setUp() throws IOException, MessagingException {
        Session session = Session.getDefaultInstance(System.getProperties(), null);
        try (InputStream is = ClassLoader.getSystemResourceAsStream("multiattach.eml")) {
            message = new MimeMessage(session, is);
            cut = new BlogMessage(message);
        } catch (Exception e) {
            log.log(Level.INFO, "setup", e);
        }

    }

    /**
     * Test of getMessageId method, of class BlogMessage.
     *
     * @throws IOException
     * @throws MessagingException
     */
    @Test
    public void testGetMessageId() {
        assertNotNull(cut.getMessageId());
    }

    /**
     * Test of getMessageNumber method, of class BlogMessage. Per
     * Message.getMessageNumber() doc: Valid message numbers start at 1.
     * Messages that do not belong to any folder (like newly composed or derived
     * messages) have 0 as their message number.
     */
    @Test
    public void testGetMessageNumber() {
        assertEquals(0, cut.getMessageNumber());
    }

    /**
     * Test of getSubject method, of class BlogMessage.
     */
    @Test
    public void testGetSubject() {
        assertEquals("Workout |status report", cut.getSubject());
    }

    /**
     * Test of getBody method, of class BlogMessage.
     */
    @Test
    public void testGetBody() {
        assertTrue(cut.getBody().contains("image/jpeg"));
    }

    /**
     * Test of getHref method, of class BlogMessage.
     */
    @Test
    public void testGetHref() {

        assertEquals("workout-status-report", cut.getHref());
    }

    /**
     * Test of getHeaders method, of class BlogMessage.
     */
    @Test
    public void testGetHeaders() {
    }

    @Test
    public void testMultipartRelated() throws IOException, MessagingException {
        Properties props = System.getProperties();
        props.put("mail.mime.charset","UTF-8");
        Session session = Session.getDefaultInstance(System.getProperties(), null);
        
        MimeMessage msg;
        
        try (InputStream is = ClassLoader.getSystemResourceAsStream("multipartrelated.eml")) {
            msg = new MimeMessage(session, is);
            cut = new BlogMessage(msg);
            assertNotNull(cut.getBody());

        } catch (Exception e) {
            log.log(Level.INFO, "testMultipartRelated catch", e);
        }

    }
    
    @Test
    public void testRawPlainText() throws IOException, MessagingException {
        Properties props = System.getProperties();
        props.put("mail.mime.charset","UTF-8");
        Session session = Session.getDefaultInstance(props, null);
        
        MimeMessage msg;
        try (InputStream is = ClassLoader.getSystemResourceAsStream("rawplaintext4.eml")) {
            msg = new MimeMessage(session, is);
            cut = new BlogMessage(msg);
            log.info("subject:" + cut.getSubject());
            log.info("href:" + cut.getHref());
            log.info("body:" + cut.getBody());
            assertNotNull(cut);

        } catch (Exception e) {
            log.log(Level.INFO, "testRawPlainText catch", e);
        }

    }

}
