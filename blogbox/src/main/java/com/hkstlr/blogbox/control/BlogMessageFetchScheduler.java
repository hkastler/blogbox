package com.hkstlr.blogbox.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;

/**
 *
 * @author henry.kastler
 */
@Singleton
public class BlogMessageFetchScheduler {

    @Inject
    Event<FetchEvent> event;

    Logger log = Logger.getLogger(this.getClass().getName());

    @Schedule(second = "0", minute = "0", hour = "*/8", persistent = false)
    public void fetchMessages() {
        try {
            log.log(Level.INFO, "new FetchEvent(\"{0}\")","fetchMessages()");
            event.fire(new FetchEvent("fetchMessages()"));
            
            
        } catch (Exception ex) {
            log.log(Level.SEVERE, "error", ex);
        }
    }
    
    

}
