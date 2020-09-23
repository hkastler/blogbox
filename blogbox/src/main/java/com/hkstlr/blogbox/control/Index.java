package com.hkstlr.blogbox.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.hkstlr.blogbox.boundary.events.EventsManager;
import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;

@ApplicationScoped
@DependsOn(value = "config")
public class Index {

    private static Logger log = Logger.getLogger(Index.class.getName());

    @Inject
    Config config;    

    @EJB
    BlogMessageManager bman;

    @EJB
    EventsManager em;

    Integer blogMessageCount;

    @PostConstruct
    void init() {
        blogMessageCount = bman.count();
        log.log(Level.INFO,"count:{0}", Integer.toString(blogMessageCount));
        log.log(Level.INFO, "setup:{0}", config.isSetup());
        if (config.isSetup()) {
            if(blogMessageCount == 0){
                em.fetchAll(this.getClass().getCanonicalName().concat(".init()"));
            } else {
                em.fetchLatest(this.getClass().getCanonicalName().concat(".init()"));
            }
        }
    }

    public Config getConfig() {
        return config;
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

    public void goFetch(String callerName){
        em.fetchAll(callerName.concat(".goFetch()"));
    }

    @Asynchronous
    public void handle(@Observes IndexEvent event) {
        if ("updateBlogMessageCount".equals(event.getName())) {
            bman.clearCache();
            updateBlogMessageCount();
        }
    }

}
