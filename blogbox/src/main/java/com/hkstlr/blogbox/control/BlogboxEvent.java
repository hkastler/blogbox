package com.hkstlr.blogbox.control;

public class BlogboxEvent {
    String name = "";
    Object payload = null;
    
	public BlogboxEvent() {
		super();
    }

    public BlogboxEvent(String name, Object payload) {
        super();
        this.name = name;
        this.payload = payload;
    }

    public BlogboxEvent(String name) {
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
