package com.hkstlr.blogbox.control;

import com.hkstlr.blogbox.control.BlogMessageEvent.BlogMessageEvents;
import com.hkstlr.blogbox.entities.BlogMessage;

public class BlogMessageSaveEvent extends BlogboxEvent {

    public BlogMessageSaveEvent(final Object payload) {
        super(BlogMessageEvents.SAVE.name());
        this.payload = payload;
     }
     
    public BlogMessageSaveEvent(final String name, final Object payload){
        super(BlogMessageEvents.SAVE.name(), payload);
    }

    public BlogMessage getBlogMessage() {
        return (BlogMessage) this.payload;
    }

}