package com.hkstlr.blogbox.boundary.jax;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
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
    @Path("/entry/{href}/refs")
    public Object[] getEntryAndNavRefs(@PathParam("href") String href) {
        Object[] rtn = new Object[2];
        BlogMessage entry = bman.getBlogMessageByHref(href);
        rtn[0] = entry;
        Integer msgNumber = entry.getMessageNumber();
        Object nav = Optional.ofNullable(bman.findRefsByMessageNumber(msgNumber)).orElse(new String[0]);
        rtn[1] = nav;
        return rtn;
    }

    @GET
    @Produces("application/json")
    @Path("/entry/{href}/refsonly")
    public Object getNavRefs(@PathParam("href") String href) {
        BlogMessage entry = bman.getBlogMessageByHref(href);
        Integer msgNumber = entry.getMessageNumber();
        Object nav = Optional.ofNullable(bman.findRefsByMessageNumber(msgNumber)).orElse(new ArrayList<>());
        return nav;
    }

    @GET
    @Produces("application/json")
    @Path("/entries/page/{page}/pageSize/{pageSize}")
    public List<BlogMessage> getEntries(@PathParam("page") Integer page, @PathParam("pageSize") Integer pageSize) {
        Paginator paginator = new Paginator(pageSize, page, index.getBlogMessageCount());
        List<BlogMessage> entries = new LinkedList<>();
        bman.getBlogMessageRange(paginator.getPageFirstItem() - 1, paginator.getPageLastItem() - 1).stream()
                .forEach(entry -> {
                    entry.setBody(getNewBody(entry.getBody()));
                    entries.add(entry);
                });
        return entries;
    }

    private String getNewBody(String body) {
        String regex = "<img[^>]+>|<img>";
        String replacement = "&lt;image&gt;";
        String newBody = body.replaceAll(regex, replacement);
        return newBody;
    }

    @GET
    @Produces("application/json")
    @Path("/msgs")
    public List<BlogMessage> getMsgs() {
        return bman.allBlogMessages();
    }

    @GET
    @Produces("application/json")
    @Path("/count")
    public Integer getCount() {
        return index.getBlogMessageCount();
    }

    @GET
    @Produces("application/json")
    @Path("/props")
    public Properties getProps() {
        // new prop to remove password from view without removing it from app config
        Properties rProps = new Properties();
        rProps.putAll(index.getConfig().getProps());
        rProps.remove(EmailReader.EmailReaderPropertyKeys.PASSWORD);
        return rProps;
    }

    @GET
    @Produces("text/plain")
    @Path("/clearcache")
    public String clearCache() {
        bman.clearCache();
        return "cleared";
    }
}
