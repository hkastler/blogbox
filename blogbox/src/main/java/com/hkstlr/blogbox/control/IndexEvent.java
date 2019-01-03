package com.hkstlr.blogbox.control;

import java.util.List;

import com.hkstlr.blogbox.entities.BlogMessage;



public class IndexEvent {

	String name = "";
	List<BlogMessage> msgs;
	public IndexEvent() {
		super();
	}
	
	public IndexEvent(String name, List<BlogMessage> msgs) {
		super();
		this.name = name;
		this.msgs =  msgs;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BlogMessage> getMsgs() {
		return msgs;
	}
	public void setMsgs(List<BlogMessage> msgs) {
		this.msgs = msgs;
	}
	
	
	
	
}
