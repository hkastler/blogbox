package com.hkstlr.blogbox.control;

import com.hkstlr.blogbox.entities.BlogMessage;

public class BlogMessageEvent {

    String name = "";
    BlogMessage bmsg;
    
	public BlogMessageEvent() {
		super();
    }

    public BlogMessageEvent(String name, BlogMessage bmsg) {
        super();
        this.name = name;
        this.bmsg = bmsg;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the bmsg
     */
    public BlogMessage getBmsg() {
        return bmsg;
    }

    /**
     * @param bmsg the bmsg to set
     */
    public void setBmsg(BlogMessage bmsg) {
        this.bmsg = bmsg;
    }
    


}