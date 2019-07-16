package com.hkstlr.blogbox.control;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.mail.MessagingException;


import org.junit.Test;

/**
 *
 * @author henry.kastler
 */
public class BlogMessageBodyTest {

    BlogMessageBody cut;
    

    @Test
    public void testMultipartRelated() throws IOException, MessagingException {
        BlogMessageTestHelper bmth = new BlogMessageTestHelper();
        String emlFile = "multipartrelated.eml";
        cut = bmth.getBlogMessageBodyFromEmlFile(emlFile);
        assertFalse(cut.getBody().isEmpty());
    }

    @Test
    public void testRawPlainText() throws IOException, MessagingException {
        BlogMessageTestHelper bmth = new BlogMessageTestHelper();
        String emlFile;
        for (int i = 1; i <= 7; i++) {
            emlFile = "rawplaintext".concat(Integer.toString(i)).concat(".eml");
            cut = bmth.getBlogMessageBodyFromEmlFile(emlFile);
            assertTrue(cut.getBody().length() > 0);
        }
    }

    @Test
    public void testRawPlainTextInlineImage() throws IOException, MessagingException {
        BlogMessageTestHelper bmth = new BlogMessageTestHelper();
        String emlFile = "plaintextInlineImage.eml";
        cut = bmth.getBlogMessageBodyFromEmlFile(emlFile);
        assertFalse(cut.getBody().isEmpty());
    }


    @Test
    public void testRawPlainTextPdf() throws IOException, MessagingException {
        BlogMessageTestHelper bmth = new BlogMessageTestHelper();
        String emlFile = "pdfattach.eml";
        cut = bmth.getBlogMessageBodyFromEmlFile(emlFile);
        
        assertFalse(cut.getBody().isEmpty());
    }
}


