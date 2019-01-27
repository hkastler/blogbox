package com.hkstlr.blogbox.control;

import java.util.Optional;
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
        Object payload = event.getPayload();
        if("save".equals(event.getName())){
            save( (BlogMessage) payload );
        }else if ("deleteByHrefNotIn".equals(event.getName())){
            deleteByHrefNotIn( (String[]) payload );
        }
    }

    void deleteByHrefNotIn(String[] hrefs){
        bman.deleteByHrefNotIn(hrefs);
    }

    void save(BlogMessage b){
        String messageId = b.getMessageId();
        Optional<BlogMessage> bmsg = Optional.ofNullable(bman.getBlogMessageByMessageId(messageId));
        if(bmsg.isPresent()){
            //if present, update, in case of change of message number
            bman.getEm().merge(b);
        }else{
            bman.getEm().persist(b);
        }

    }

}
