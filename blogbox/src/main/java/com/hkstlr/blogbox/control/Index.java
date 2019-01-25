package com.hkstlr.blogbox.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
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

    Integer blogMessageCount;

    @PostConstruct
    void init() {
        blogMessageCount = 0;
        log.log(Level.INFO, "setup:{0}", config.isSetup());
        if (config.isSetup()) {
            event.fire(new FetchEvent(this.getClass().getCanonicalName()
                    .concat(".init()")));
        }
    }

    public Config getConfig() {
        return config;
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

    /**
     * @param config the config to set
     */
    public void setConfig(Config config) {
        this.config = config;
    }

	public void updateBlogMessageCount() {
        log.info("updating blogMessageCount");
        this.blogMessageCount = bman.count();
	}

    /**
     * @return the blogMessageCount
     */
    public Integer getBlogMessageCount() {
        return blogMessageCount;
    }

    /**
     * @param blogMessageCount the blogMessageCount to set
     */
    public void setBlogMessageCount(Integer blogMessageCount) {
        this.blogMessageCount = blogMessageCount;
    }

    @Asynchronous
    public void handle(@Observes IndexEvent event) {
        if("updateBlogMessageCount".equals(event.getName())){
            updateBlogMessageCount();
        }
    }

}
