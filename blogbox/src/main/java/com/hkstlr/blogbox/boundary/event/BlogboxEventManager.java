package com.hkstlr.blogbox.boundary.event;

import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.control.BlogMessageDeleteEvent;
import com.hkstlr.blogbox.control.BlogMessageSaveEvent;
import com.hkstlr.blogbox.control.BlogboxEvent;
import com.hkstlr.blogbox.control.FetchEvent;
import com.hkstlr.blogbox.control.IndexEvent;
import com.hkstlr.blogbox.entities.BlogMessage;


@Stateless
public class BlogboxEventManager {

    @Inject
    Event<BlogboxEvent> event;
    
    @EJB
    BlogMessageManager bman;
    
    public BlogboxEventManager(){
        super();
    }

    public void saveBlogMessage(final BlogMessage bmsg) {
        event.fire(new BlogMessageSaveEvent(bmsg));
    }

    public void deleteByHrefNotIn(final String[] hrefs) {
        event.fire(new BlogMessageDeleteEvent(hrefs));
    }

    public String fetchAll(final String callerName) {
        final String fetchEvent = callerName.concat(".fetch()");
        event.fire(new FetchEvent(FetchEvent.FetchEvents.FETCH_ALL.name()));
        Logger.getLogger(this.getClass().getCanonicalName()).info(fetchEvent);
        return fetchEvent;
    }

    public String fetchLatest(final String callerName) {
        final String fetchEvent = callerName.concat(".fetchlatest()");
        final BlogMessage latest = bman.getTopMessage();
        event.fire(new FetchEvent(FetchEvent.FetchEvents.SEARCH_LATEST.name(), latest.getCreateDate()));
        Logger.getLogger(this.getClass().getCanonicalName()).info(fetchEvent);
        return fetchEvent;
    }

    public void updateBlogMessageCount(final String callerName) {
        event.fire(new IndexEvent(callerName.concat(IndexEvent.IndexEvents.UPDATE_COUNT.name())));
    }

}
