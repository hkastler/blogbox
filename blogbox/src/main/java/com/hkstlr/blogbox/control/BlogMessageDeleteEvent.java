package com.hkstlr.blogbox.control;

import com.hkstlr.blogbox.control.BlogMessageEvent.BlogMessageEvents;

public class BlogMessageDeleteEvent extends BlogboxEvent {

    public BlogMessageDeleteEvent(final Object payload) {
        super(BlogMessageEvents.DELETE_BY_HREF.name());
        this.payload = payload;
    }

    public BlogMessageDeleteEvent(final String name, final Object payload) {
        super(BlogMessageEvents.DELETE_BY_HREF.name(), payload);
    }

    public String[] getHrefs() {
        return (String[]) payload;
    }

}