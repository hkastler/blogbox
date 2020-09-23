package com.hkstlr.blogbox.control;

public class FetchEvent extends BlogboxEvent {
	
	public enum FetchEvents { FETCH_ALL, SEARCH_LATEST };

	public FetchEvent() {
		super();
	}
 
	public FetchEvent(final String name) {
		super(name);
	}
	
	public FetchEvent(final String name, final Object payload) {
		super(name, payload);
	}

}
