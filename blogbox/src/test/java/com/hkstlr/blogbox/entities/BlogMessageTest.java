package com.hkstlr.blogbox.entities;

import com.hkstlr.blogbox.control.BlogMessageTestHelper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
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
        BlogMessageTestHelper b = new BlogMessageTestHelper();
        cut = b.getBlogMessageFromEmlFile("multiattach.eml");
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
        log.info(cut.getBody());
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

   
    

}
