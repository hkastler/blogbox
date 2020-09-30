package com.hkstlr.blogbox.control;

public class BlogMessageEvent extends BlogboxEvent {

    enum BlogMessageEvents { SAVE, DELETE_BY_HREF };

    public BlogMessageEvent(){
        super();
    }
    
    public BlogMessageEvent(final String name){
        super(name);
    }

    public BlogMessageEvent(final String name, final Object payload){
        super(name, payload);
    }

}