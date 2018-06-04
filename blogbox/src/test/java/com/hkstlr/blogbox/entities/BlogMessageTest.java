
package com.hkstlr.blogbox.entities;

import com.hkstlr.blogbox.entities.BlogMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
    }
    
    
    @Before
    public void setUp() throws IOException, MessagingException {
        Session session = Session.getDefaultInstance(System.getProperties(), null);
        Path eml = Paths.get("src","test","resources","email-img.eml");
        
        try (InputStream is = Files.newInputStream(eml)) {
            message = new MimeMessage(session, is);
        }
        cut = new BlogMessage(message);
        
    }

    /**
     * Test of getMessageId method, of class BlogMessage.
     */
    @Test
    public void testGetMessageId() {
        assertNotNull(cut.getMessageId());
    }

    
    /**
     * Test of getMessageNumber method, of class BlogMessage.
     * Per Message.getMessageNumber() doc: Valid message numbers start at 1. 
     * Messages that do not belong to any folder (like newly composed or derived messages) 
     * have 0 as their message number.
     */
    @Test
    public void testGetMessageNumber() {
    	assertEquals(0,cut.getMessageNumber());
    }

        

    /**
     * Test of getSubject method, of class BlogMessage.
     */
    @Test
    public void testGetSubject() {
        assertEquals("The Case for 10 Minute Exercise",cut.getSubject());
    }

    
    /**
     * Test of getBody method, of class BlogMessage.
     */
    @Test
    public void testGetBody() {
    	assertTrue(cut.getBody().contains("image/jpeg"));
    	//log.info(cut.getBody().substring(0, 1000));
    }

   
    /**
     * Test of getHref method, of class BlogMessage.
     */
    @Test
    public void testGetHref() {
        
        assertEquals("the-case-for-10-minute-exercise",cut.getHref());
    }

    /**
     * Test of getHeaders method, of class BlogMessage.
     */
    @Test
    public void testGetHeaders() {
    }

    
}
