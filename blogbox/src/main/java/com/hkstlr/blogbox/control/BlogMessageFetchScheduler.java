package com.hkstlr.blogbox.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.hkstlr.blogbox.boundary.events.EventsManager;

/**
 *
 * @author henry.kastler
 */
@Singleton
public class BlogMessageFetchScheduler {

    @EJB
    EventsManager em;

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
