package com.hkstlr.blogbox.control;

public class BlogMessageEvent {

    String name = "";
    Object payload;
    
	public BlogMessageEvent() {
		super();
    }

    public BlogMessageEvent(String name, Object payload) {
        super();
        this.name = name;
        this.payload = payload;
    }

    public BlogMessageEvent(String name) {
        super();
        this.name = name;
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
     * @return the payload
     */
    public Object getPayload() {
        return payload;
    }

    /**
     * @param payload the payload to set
     */
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    
    


}