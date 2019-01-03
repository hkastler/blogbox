package com.hkstlr.blogbox.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.hkstlr.blogbox.entities.BlogMessage;

@SuppressWarnings("serial")
@Stateless
public class FetchHandler implements Serializable {
 
    private static final Logger LOG = Logger.getLogger(FetchHandler.class.getCanonicalName());
    
    @Inject
    Config config;
     
    @Inject
    Event<IndexEvent> event;
    
    public FetchHandler() {
		super();
	}

	@Asynchronous
    public void goFetch(@Observes FetchEvent event) {
        LOG.log(Level.INFO, "FetchHandler.goFetch:{0}", event.getEvent());
        if(config.isSetup()) {
        	fetchAndSetBlogMessages();
        }	
    }
    
    
    @Asynchronous  
    public void fetchAndSetBlogMessages(){
        
		try {
			Optional<ArrayList<BlogMessage>> fm = Optional.ofNullable(getBlogMessages().get());
			
			if(fm.isPresent()) {
				event.fire(new IndexEvent("setIndexMsgs",fm.get()));
			}
			
		} catch (InterruptedException | ExecutionException e) {
			LOG.log(Level.SEVERE, "error",e);
		}
		
    }
    
    @Asynchronous
    @AccessTimeout(value=45000)
    public Future<ArrayList<BlogMessage>> getBlogMessages() throws InterruptedException {
    	
        CompletableFuture<ArrayList<BlogMessage>> completableFuture 
          = new CompletableFuture<>();
        
        ArrayList<BlogMessage> bmsgs = new ArrayList<>();
        
        Integer hrefMaxWords = Optional.ofNullable(Integer.parseInt(config.getProps()
        		.getProperty("bmgs.hrefWordMax")))
        		.orElse(BlogMessage.DEFAULT_HREFWORDMAX);
        
        EmailReader er = new EmailReader(config.getProps());
                
        bmsgs = er.setBlogMessages(bmsgs, hrefMaxWords);
        
        er.storeClose();
        
        completableFuture.complete(bmsgs);
        return completableFuture;
    }
}
