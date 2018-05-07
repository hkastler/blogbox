package com.hkstlr.blogbox.boundary;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import com.hkstlr.blogbox.control.Config;
import com.hkstlr.blogbox.control.DateFormatter;
import com.hkstlr.blogbox.control.FetchEvent;
import com.hkstlr.blogbox.control.Index;
import com.hkstlr.blogbox.control.IndexEvent;
import com.hkstlr.blogbox.entities.BlogMessage;

@RequestScoped
@Named("index")
public class IndexBean {
	
	
    private static Logger log = Logger.getLogger(IndexBean.class.getName());
        
    @Inject
    Index index;
   
    @Inject
    Event<String> indexEvent;

    public IndexBean() {
        super();
    }

    public List<BlogMessage> getMsgs() {
        return index.getMsgs();
    }

    public Map<String, Integer> getMsgMap() {
        return index.getMsgMap();
    }
    
    public Config getConfig() {
    	return index.getConfig();
    }
    
    public int min(int a, int b) {
    	return Math.min(a, b);
    }

    @Produces
    public String view() {
    	
        String template = "view.xhtml";
        if (!index.getConfig().isSetup()) {
            template = "setup/index.xhtml";
        }

        return template;
    }
    
    public String jsFormat(Date date) {
    	
    	return new DateFormatter(date).formatjsFormat();
    }
    
    @Asynchronous
    public void goFetch() {
    	index.getEvent().fire(new FetchEvent(this.getClass().getCanonicalName()
        		.concat(".goFetch()")));
    }
   

    @Asynchronous
    public void processIndexEvent(@Observes IndexEvent event) {
    	
        if("setIndexMsgs".equals(event.getName())) {        	
        	index.setIndexMsgs(event.getMsgs());
        }
 
    }

}
