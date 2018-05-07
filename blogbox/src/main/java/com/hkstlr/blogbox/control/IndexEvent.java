package com.hkstlr.blogbox.control;

import java.util.ArrayList;

import com.hkstlr.blogbox.entities.BlogMessage;

public class IndexEvent {

	String name = "";
	ArrayList<BlogMessage> msgs;
	public IndexEvent() {
		super();
	}
	
	public IndexEvent(String name, ArrayList<BlogMessage> msgs) {
		super();
		this.name = name;
		this.msgs = msgs;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<BlogMessage> getMsgs() {
		return msgs;
	}
	public void setMsgs(ArrayList<BlogMessage> msgs) {
		this.msgs = msgs;
	}
	
	
	
	
}
