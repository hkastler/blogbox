package com.hkstlr.blogbox.control;

public class IndexEvent extends BlogboxEvent {

	public enum IndexEvents { UPDATE_COUNT }

	public IndexEvent() {
		super();
	}

	public IndexEvent(String name) {
		super(name);
	}

}
