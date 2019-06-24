package com.hkstlr.blogbox.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import com.hkstlr.blogbox.entities.BlogMessageTest;

import org.junit.Test;

/**
 *
 * @author henry.kastler
 */
public class BlogMessageBodyTest {

    BlogMessageBody cut;
    Logger log = Logger.getLogger(BlogMessageTest.class.getName());

    @Test
    public void testMultipartRelated() throws IOException, MessagingException {
        BlogMessageTestHelper bmth = new BlogMessageTestHelper();
        String emlFile = "multipartrelated.eml";
        cut = bmth.getBlogMessageBodyFromEmlFile(emlFile);
        
    }

    @Test
    public void testRawPlainText() throws IOException, MessagingException {
        BlogMessageTestHelper bmth = new BlogMessageTestHelper();
        String emlFile = "rawplaintext4.eml";
        cut = bmth.getBlogMessageBodyFromEmlFile(emlFile);
        assertTrue(cut.getBody().length() > 0);
    }

    @Test
    public void testRawPlainTextInlineImage() throws IOException, MessagingException {
        BlogMessageTestHelper bmth = new BlogMessageTestHelper();
        String emlFile = "plaintextInlineImage.eml";
        cut = bmth.getBlogMessageBodyFromEmlFile(emlFile);
        assertNotNull(cut);
        assertFalse(cut.getBody().isEmpty());
    }
}
