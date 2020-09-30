package com.hkstlr.blogbox.boundary.event;

import java.util.Optional;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.control.BlogMessageDeleteEvent;
import com.hkstlr.blogbox.control.BlogMessageSaveEvent;
import com.hkstlr.blogbox.entities.BlogMessage;

@Stateless
public class BlogMessageEventObserver {

    @EJB
    BlogMessageManager bman;
    
    public BlogMessageEventObserver() {
        super();
    }

    @Asynchronous
    public void observeSave(@Observes BlogMessageSaveEvent event) {
        save(event.getBlogMessage());
    }

    @Asynchronous
    public void observeDelete(@Observes BlogMessageDeleteEvent event) {
        deleteByHrefNotIn(event.getHrefs());
    }
    
    void deleteByHrefNotIn(String[] hrefs) {
        bman.deleteByHrefNotIn(hrefs);
    }
    
    void deleteByMessageIdNotIn(String[] messageIds) {
        bman.deleteByMessageIdNotIn(messageIds);
    }

    void save(BlogMessage b) {
        
        String href = b.getHref();
        Optional<BlogMessage> oBmsg = Optional.ofNullable(bman.getBlogMessageByHref(href));
        if (oBmsg.isPresent()) {
            BlogMessage bmsg = oBmsg.get();
            //if present, update, in case of change of message number
            if(bmsg.getMessageId().equals(b.getMessageId())){
                bman.getEm().merge(b);
            }else{
                bman.getEm().remove(bmsg);
                bman.getEm().flush();
                bman.getEm().persist(b);
            }
        } else {
            bman.getEm().persist(b);
        }

    }

}
