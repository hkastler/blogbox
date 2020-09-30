package com.hkstlr.blogbox.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author henry.kastler
 */
public class BlogMessageEventTest {
    
    BlogMessageEvent cut;
    
    public BlogMessageEventTest() {
        super();
        cut = new BlogMessageEvent();
    }
    
    /**
     * Test of getName method, of class BlogMessageEvent.
     */
    @Test
    public void testGetName() {
         
        String expResult = "testEvent";
        cut.setName(expResult);
        String result = cut.getName();
        assertEquals(expResult, result);
        
    }

     /**
     * Test of getPayload method, of class BlogMessageEvent.
     */
    @Test
    public void testGetPayload() {
     
        Object expResult = null;
        Object result = cut.getPayload();
        assertEquals(expResult, result);

        expResult = new String[]{"hello","world"};
        cut.setPayload(expResult);
        result = cut.getPayload();
        assertEquals(expResult, result);

        expResult = new Integer[]{1,2,3};
        cut.setPayload(expResult);
        result = cut.getPayload();
        assertEquals(expResult, result);
        
    }

    @Test
    public void testConstructors(){
        BlogMessageSaveEvent bmeFull = new BlogMessageSaveEvent("name", new Integer[]{1,2,3});
        assertNotNull(bmeFull.getPayload());
        assertFalse(bmeFull.getPayload() instanceof String);
        BlogMessageSaveEvent bmeName = new BlogMessageSaveEvent("name");
        assertNotNull(bmeName.getName());
    }
    
}
