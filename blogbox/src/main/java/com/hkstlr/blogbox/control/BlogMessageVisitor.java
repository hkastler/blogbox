package com.hkstlr.blogbox.control;

public class BlogMessageVisitor {

	 
    String subjectRegex;
    Integer maxHrefWords;
    
    public BlogMessageVisitor() {
		super();
	}        
    
    public BlogMessageVisitor(Config config) {
		super();
		this.subjectRegex = config.getProps().getProperty("bmsg.createSubjectRegex", "blog");
	    this.maxHrefWords = Integer.parseInt(config.getProps()
	    		.getProperty("bmsg.hrefWordMax", "25")) ;
	} 
    
    
	public BlogMessageVisitor(String subjectRegex, Integer maxHrefWords) {
		super();
		this.subjectRegex = subjectRegex;
		this.maxHrefWords = maxHrefWords;
	}

	public String getSubjectRegex() {
		return subjectRegex;
	}
	public void setSubjectRegex(String subjectRegex) {
		this.subjectRegex = subjectRegex;
	}
	public Integer getMaxHrefWords() {
		return maxHrefWords;
	}
	public void setMaxHrefWords(Integer maxHrefWords) {
		this.maxHrefWords = maxHrefWords;
	}
    
    
}
