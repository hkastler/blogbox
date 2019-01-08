package com.hkstlr.blogbox.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.hkstlr.blogbox.entities.BlogMessage;
import com.hkstlr.blogbox.entities.BlogMessage.BlogMessageBuilder;

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
    
        Optional<List<BlogMessage>> fm = Optional.empty();
		try {
			fm = Optional.ofNullable(getBlogMessages().get());
		} catch (InterruptedException ie) {
			LOG.log(Level.WARNING, "InterruptedException!", ie);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
            LOG.log(Level.WARNING, "ExecutionException!", e);
        }
        
        if(fm.isPresent()) {
            event.fire(new IndexEvent("setIndexMsgs",fm.get()));
        }
		
    }
    
    @Asynchronous
    public Future<List<BlogMessage>> getBlogMessages() {
    	
        CompletableFuture<List<BlogMessage>> completableFuture 
          = new CompletableFuture<>();
        
        List<BlogMessage> bmsgs = new ArrayList<>();
        
        Integer hrefMaxWords = Optional.ofNullable(Integer.parseInt(config.getProps()
        		.getProperty("bmgs.hrefWordMax")))
        		.orElse(BlogMessageBuilder.DEFAULT_HREFWORDMAX);
        
        EmailReader er = new EmailReader(config.getProps());
                
        bmsgs = er.setBlogMessages(bmsgs, hrefMaxWords);
        
        er.storeClose();
        
        completableFuture.complete(bmsgs);
        return completableFuture;
    }
}
