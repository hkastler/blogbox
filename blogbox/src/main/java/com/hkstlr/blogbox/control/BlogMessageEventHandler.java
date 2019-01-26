package com.hkstlr.blogbox.control;

import java.util.Optional;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.entities.BlogMessage;
import java.io.IOException;
import java.util.logging.Level;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;

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
            case "deleteByMessageIdNotIn":
                break;
            case "processMessage":
                processMessage((Message)payload);
                break;
            default:
                break;
        }
    }
    
    void processMessage(Message msg) {        
        Integer hrefMaxWords = Optional.ofNullable(Integer.parseInt(config.getProps()
                .getProperty("bmgs.hrefWordMax")))
                .orElse(BlogMessage.DEFAULT_HREFWORDMAX);
        try {
            BlogMessage bmsg = new BlogMessage(msg, hrefMaxWords);
            event.fire( new BlogMessageEvent("save", bmsg) );
        } catch (MessagingException | IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
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
