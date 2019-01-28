package com.hkstlr.blogbox.control;

import java.util.Optional;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.entities.BlogMessage;

@Stateless
public class BlogMessageEventHandler {

    Logger LOG = Logger.getLogger(BlogMessageEventHandler.class.getCanonicalName());
    
    @Inject
    Config config;

    @EJB
    BlogMessageManager bman;
    
    @Inject
    Event<BlogMessageEvent> event;

    public BlogMessageEventHandler() {
        super();
    }

    @Asynchronous
    public void handle(@Observes BlogMessageEvent event) {
        String eventName = event.getName();
        Object payload = event.getPayload();
        if (null != eventName) switch (eventName) {
            case "save":
                save((BlogMessage) payload);
                break;
            case "deleteByHrefNotIn":
                deleteByHrefNotIn((String[]) payload);
                break;
            default:
                break;
        }
    }
    
    void deleteByHrefNotIn(String[] hrefs) {
        bman.deleteByHrefNotIn(hrefs);
    }
    
    void deleteByMessageIdNotIn(String[] messageIds) {
        bman.deleteByMessageIdNotIn(messageIds);
    }

    void save(BlogMessage b) {
        String messageId = b.getMessageId();
        Optional<BlogMessage> bmsg = Optional.ofNullable(bman.getEm().find(BlogMessage.class, messageId));
        
        if (bmsg.isPresent()) {
            //if present, update, in case of change of message number
            bman.getEm().merge(b);
           
        } else {
            bman.getEm().persist(b);
        }

    }

}
