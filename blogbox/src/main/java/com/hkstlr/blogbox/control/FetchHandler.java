package com.hkstlr.blogbox.control;

import java.io.Serializable;
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

@SuppressWarnings("serial")
@Stateless
public class FetchHandler implements Serializable {
 
    private static final Logger LOG = Logger.getLogger(FetchHandler.class.getCanonicalName());
    
    @Inject
    Config config;
     
    @Inject
    Event<IndexEvent> event;

    @Inject
    EmailReader er;
    
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
    
        Optional<Boolean> fm = Optional.empty();
		try {
            fm = Optional.ofNullable( getBlogMessages().get() );
		} catch (InterruptedException ie) {
			LOG.log(Level.WARNING, "InterruptedException", ie);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
            LOG.log(Level.WARNING, "ExecutionException", e);
        }
        
        if(fm.isPresent() && fm.get()) {
            event.fire(new IndexEvent("updateBlogMessageCount"));
        }
		
    }
    
    @Asynchronous
    public Future<Boolean> getBlogMessages() {
    	Integer DEFAULT_HREFWORDMAX = 10;
        CompletableFuture<Boolean> completableFuture 
          = new CompletableFuture<>();
         
        Integer hrefMaxWords = Optional.ofNullable(Integer.parseInt(config.getProps()
        		.getProperty("bmgs.hrefWordMax")))
        		.orElse(DEFAULT_HREFWORDMAX);
        
        er.setProps(config.getProps());
        er.init();
        completableFuture.complete( er.setBlogMessages(hrefMaxWords) );
        return completableFuture;
    }
}
