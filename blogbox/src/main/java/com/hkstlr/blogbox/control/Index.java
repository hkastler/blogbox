package com.hkstlr.blogbox.control;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;

@ApplicationScoped
@DependsOn(value = "config")
public class Index {
    
    private static Logger log = Logger.getLogger(Index.class.getName());

    @Inject
    Config config;

    @Inject
    Event<FetchEvent> event;

    @EJB
    BlogMessageManager bman;

    ConcurrentMap<String, Integer> msgMap = new ConcurrentHashMap<>();

    @PostConstruct
    void init() {

        log.log(Level.INFO, "setup:{0}", config.isSetup());
        if (config.isSetup()) {
            event.fire(new FetchEvent(this.getClass().getCanonicalName()
                    .concat(".init()")));
        }
    }

    public Map<String, Integer> getMsgMap() {
        return msgMap;
    }

    public void setMsgMap(ConcurrentMap<String, Integer> msgMap) {
        this.msgMap = msgMap;
    }

    public Config getConfig() {
        return config;
    }

    public void setIndexMsgs() {

        this.getMsgMap().clear();
        AtomicInteger i = new AtomicInteger(0);
        bman.allBlogMessages().forEach(bmsg
                -> getMsgMap().put(bmsg.getHref(), i.getAndIncrement()));

    }

    /**
     * @return the event
     */
    public Event<FetchEvent> getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event<FetchEvent> event) {
        this.event = event;
    }

}
