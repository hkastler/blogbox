package com.hkstlr.blogbox.boundary;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.control.Config;
import com.hkstlr.blogbox.control.DateFormatter;
import com.hkstlr.blogbox.control.FetchEvent;
import com.hkstlr.blogbox.control.Index;
import com.hkstlr.blogbox.control.IndexEvent;
import com.hkstlr.blogbox.control.Paginator;
import com.hkstlr.blogbox.entities.BlogMessage;

@RequestScoped
@Named("index")
public class IndexBean {

    Paginator paginator;

    @Inject
    BlogMessageManager bmm;

    @Inject
    Index index;

    @Inject
    Event<String> indexEvent;

    public IndexBean() {
        super();
    }

    @PostConstruct
    void init() {
        String itemsPerPage = (String) index.getConfig().getProps().getOrDefault("blog.itemsPerPage", "4");
        setPaginator(new Paginator(Integer.parseInt(itemsPerPage), 1, index.getMsgMap().size()));
    }

    public void viewAction() {
        paginator.setNumberOfPages();
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

    public Boolean hasMessages(){
        Integer msgCount = bmm.count();
        return (msgCount > 0);
    }

    public List<BlogMessage> currentList() {

         return bmm.getBlogMessageRange(paginator.getPageFirstItem() - 1, paginator.getPageLastItem() - 1);
        
    }

    public BlogMessage getMsgByHref(String href){
        return bmm.getBlogMessageByHref(href);
    }

    public BlogMessage getMsgByMessageNumber(Integer msgNum){
        return bmm.getBlogMessageByMessageNumber(msgNum);
    }

    public String view() {
        String template = "view.xhtml";
        if (!index.getConfig().isSetup()) {
            template = "setup/index.xhtml";
        }
        return template;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    public String jsFormat(Date date) {

        return new DateFormatter(date).formatjsFormat();
    }

    @Asynchronous
    public void goFetch() {
        index.getEvent().fire(new FetchEvent(this.getClass().getCanonicalName().concat(".goFetch()")));
    }

    @Asynchronous
    public void processIndexEvent(@Observes IndexEvent event) {

        if ("setIndexMsgs".equals(event.getName())) {
            index.setIndexMsgs();
        }

    }

}
