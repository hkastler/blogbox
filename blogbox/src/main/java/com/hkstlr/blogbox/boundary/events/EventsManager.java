package com.hkstlr.blogbox.boundary.events;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.control.BlogMessageEvent;
import com.hkstlr.blogbox.control.BlogboxEvent;
import com.hkstlr.blogbox.control.FetchEvent;
import com.hkstlr.blogbox.control.IndexEvent;
import com.hkstlr.blogbox.entities.BlogMessage;


@Stateless
public class EventsManager {

    @Inject
    Event<BlogboxEvent> event;
    
    @EJB
    BlogMessageManager bman;
    
    public EventsManager(){
        super();
    }

    public void saveBlogMessage(BlogMessage bmsg){
        event.fire(new BlogMessageEvent("save", bmsg));
    }

    public void deleteByHrefNotIn(String[] hrefs){
        event.fire(new BlogMessageEvent("deleteByHrefNotIn", hrefs));
    }

    public String fetchAll(String callerName){
        String fetchEvent = callerName.concat(".fetch()");
        event.fire(new FetchEvent(FetchEvent.FetchEvents.FETCH_ALL.name()));
        Logger.getLogger(this.getClass().getCanonicalName()).info(fetchEvent);
        return fetchEvent;
    }

    public String fetchLatest(String callerName){
        String fetchEvent = callerName.concat(".fetchlatest()");
        BlogMessage latest = bman.getTopMessage();
        event.fire(new FetchEvent(FetchEvent.FetchEvents.SEARCH_LATEST.name(),latest.getCreateDate()));        
        Logger.getLogger(this.getClass().getCanonicalName()).info(fetchEvent);
        return fetchEvent;
    }

    public void updateBlogMessageCount(String callerName){
        event.fire(new IndexEvent(callerName.concat("updateBlogMessageCount")));
    }
}
