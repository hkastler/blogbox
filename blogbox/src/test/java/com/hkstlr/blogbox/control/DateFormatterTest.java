package com.hkstlr.blogbox.control;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author henry.kastler
 */
public class DateFormatterTest {
    
    Date date;
    DateFormatter cut;
    
    public DateFormatterTest() {
    }
    
    
    @Before
    public void setUp() {
        Instant di = LocalDate.parse("2015-04-10").atStartOfDay().toInstant(ZoneOffset.UTC);
        date = Date.from(di);
        cut = new DateFormatter(date);
    }

    /**
     * Test of format8chars method, of class DateFormatter.
     */
    @Test
    public void testFormat8chars() {
        System.out.println("testFormat8chars()");
        //System.out.println(cut.getDate());
        //System.out.println(cut.format8chars());
        assertEquals("20150410", cut.format8chars());
    }

    /**
     * Test of formatjsFormat method, of class DateFormatter.
     */
    @Test
    public void testFormatjsFormat() {
        System.out.println("testFormatjsFormat()");
        //System.out.println(cut.getDate());
        //System.out.println(cut.formatjsFormat());
        assertEquals("2015-04-10T00:00:00", cut.formatjsFormat());
    }

    
    
}
