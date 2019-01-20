package com.hkstlr.blogbox.control;

public class IndexEvent {

	String name = "";
	
	public IndexEvent() {
		super();
	}
	
	public IndexEvent(String name) {
		super();
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
