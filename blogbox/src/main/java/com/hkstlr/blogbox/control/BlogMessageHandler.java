package com.hkstlr.blogbox.control;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;

import com.hkstlr.blogbox.boundary.jpa.BlogMessageManager;
import com.hkstlr.blogbox.entities.BlogMessage;

@Stateless
public class BlogMessageHandler{
    Logger LOG = Logger.getLogger(BlogMessageHandler.class.getCanonicalName());

	@EJB
	BlogMessageManager bman;

    public BlogMessageHandler(){
        super();
    }

    @Asynchronous
    public void handle(@Observes BlogMessageEvent event) {
        
        if("save".equals(event.getName())){
            save(event.getBmsg());
        }
    }

    void save(BlogMessage b){
        String messageId = b.getMessageId();
        Optional<BlogMessage> bmsg = Optional.ofNullable(bman.getEm().find(BlogMessage.class, messageId));
        if(bmsg.isPresent()){
            bman.getEm().merge(b);
        }else{
            bman.saveBlogMessage(b);
        }
        
    }

}