package com.hkstlr.blogbox.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import com.hkstlr.blogbox.boundary.event.BlogboxEventManager;

/**
 *
 * @author henry.kastler
 */
@Singleton
public class BlogMessageFetchScheduler {

    @EJB
    BlogboxEventManager em;

    Logger log = Logger.getLogger(this.getClass().getName());

    @Schedule(second = "0", minute = "0", hour = "*/8", persistent = false)
    public void fetchMessages() {
        try {
            em.fetchLatest(this.getClass().getCanonicalName());
            
        } catch (Exception ex) {
            log.log(Level.SEVERE, "error", ex);
        }
    }
    
    

}
