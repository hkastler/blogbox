package com.hkstlr.blogbox.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author henry.kastler
 */
@Ignore 
 public class EmailReaderTest {
    
    EmailReader cut;
    Config config;
        
    
    public EmailReaderTest() {
    	//no-arg constructor
    }
    
  
    @Before
    public void setUp() throws IOException {
    	
    	try (InputStream is = ClassLoader.getSystemResourceAsStream("app.properties")) {
        	Properties props = new Properties();
        	props.load(is);
        	config = new Config(props);
        }
        
    }

    /**
     * Test of getImapEmails method, of class EmailReader.
     */
    @Test
    public void testGetImapEmails() {
       cut = new EmailReader(config.getProps());
       assertEquals(0,cut.getImapEmails().length); 
    }

    /**
     * Test of getMessageCount method, of class EmailReader.
     */
    @Test
    public void testGetMessageCount() {
        cut = new EmailReader(config.getProps());
        assertTrue(cut.blogBox.isOpen());
        assertEquals(0,cut.getMessageCount());
    }

    /**
     * Test of init method, of class EmailReader.
     */
    @Test
    public void testInit() throws MessagingException {
        
        cut = new EmailReader(config.getProps());
        Arrays.asList(cut.store.getPersonalNamespaces())
                .stream()
                .forEach(f -> System.out.println("name:" + f.getName()));
          
        assertEquals("INBOX",cut.blogBox.getName());
            
    }

    /**
     * Test of storeClose method, of class EmailReader.
     */
    @Test
    public void testStoreClose() {
       cut = new EmailReader(config.getProps());
       assertTrue(cut.store.isConnected());
       cut.storeClose();
       assertTrue(!cut.store.isConnected());
    }

    /**
     * Test of storeConnect method, of class EmailReader.
     */
    @Test
    public void testStoreConnect() {
       cut = new EmailReader(config.getProps());
       assertTrue(cut.store.isConnected()); 
    }

    
}
