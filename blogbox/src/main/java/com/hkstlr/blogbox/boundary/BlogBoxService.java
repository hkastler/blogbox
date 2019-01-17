package com.hkstlr.blogbox.boundary;

import java.util.List;
import java.util.Properties;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.control.BlogMessageHandler;
import com.hkstlr.blogbox.control.EmailReader;
import com.hkstlr.blogbox.control.Index;
import com.hkstlr.blogbox.control.Paginator;
import com.hkstlr.blogbox.entities.BlogMessage;

@Path("/srvc")
public class BlogBoxService {

	@Inject
    Index index;
    
    @EJB
    BlogMessageManager bman;
	    
    public BlogBoxService() { 
    	super();
    }
    
    @GET
    @Produces("application/json")
    @Path("/entry/{href}")
    public BlogMessage getHref(@PathParam("href") String href) {	
        return bman.getBlogMessageByHref(href);
    }

    @GET
    @Produces("application/json")
    @Path("/entries/page/{page}/pageSize/{pageSize}")
    public List<BlogMessage> getEntries(@PathParam("page") Integer page, @PathParam("pageSize") Integer pageSize) {	
        Paginator paginator = new Paginator(pageSize, page, index.getMsgMap().size());
        return bman.getBlogMessageRange(paginator.getPageFirstItem()-1, paginator.getPageLastItem()-1 );
    }
    
    @GET
    @Produces("application/json")
    @Path("/msgs")
    public List<BlogMessage> getMsgs() {
        return bman.allBlogMessages();
    }
    
    @GET
    @Produces("application/json")
    @Path("/props")
    public Properties getProps() {
    	//new prop to remove password from view without removing it from app config
    	Properties rProps = new Properties();
    	rProps.putAll(index.getConfig().getProps());
    	rProps.remove(EmailReader.EmailReaderPropertyKeys.PASSWORD);
        return rProps;
    }
}
