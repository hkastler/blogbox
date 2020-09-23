package com.hkstlr.blogbox.control;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.hkstlr.blogbox.boundary.events.EventsManager;

@SuppressWarnings("serial")
@Stateless
public class FetchHandler implements Serializable {

    private static final Logger LOG = Logger.getLogger(FetchHandler.class.getCanonicalName());

    @Inject
    Config config;

    @EJB
    EventsManager em;

    @Inject
    EmailReader er;

    public FetchHandler() {
        super();
    }

    @Asynchronous
    public void goFetch(@Observes FetchEvent event) {
        String eventName = event.getName();

        LOG.log(Level.INFO, "FetchHandler.goFetch:{0}", eventName);
        if (FetchEvent.FetchEvents.FETCH_ALL.name().equals(eventName)) {
            if (config.isSetup()) {
                LOG.log(Level.INFO, "fetch_all:{0}", eventName);
                fetchAndSetBlogMessages(event, FetchEvent.FetchEvents.FETCH_ALL);
            }
        } else if (FetchEvent.FetchEvents.SEARCH_LATEST.name().equals(eventName)) {
            LOG.log(Level.INFO, "fetch_latest:{0}", eventName);
            fetchAndSetBlogMessages(event, FetchEvent.FetchEvents.SEARCH_LATEST);
        }

    }

    @Asynchronous
    public void fetchAndSetBlogMessages(FetchEvent fevent, FetchEvent.FetchEvents type) {
      
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
        er.setProps(config.getProps());
        er.init();

        LOG.log(Level.INFO, "type:{0}", type.name());
        Optional<Boolean> fm = Optional.empty();
        try {
            if (type.equals(FetchEvent.FetchEvents.FETCH_ALL)) {
                fm = Optional.ofNullable(fetchBlogMessages(completableFuture).get());
            } else if (type.equals(FetchEvent.FetchEvents.SEARCH_LATEST)) {
                Date lastDate = (Date) fevent.getPayload();
                fm = Optional.ofNullable(searchBlogMessages(completableFuture, lastDate).get());
            }

        } catch (InterruptedException ie) {
            LOG.log(Level.WARNING, "InterruptedException", ie);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOG.log(Level.WARNING, "ExecutionException", e);
        }

        if (fm.isPresent() && fm.get()) {
            em.updateBlogMessageCount(this.getClass().getCanonicalName());
        }

    }

    @Asynchronous
    public Future<Boolean> fetchBlogMessages(CompletableFuture<Boolean> completableFuture) {
        completableFuture.complete(er.setBlogMessages(hrefMaxWordsCount()));
        return completableFuture;
    }

    @Asynchronous
    public Future<Boolean> searchBlogMessages(CompletableFuture<Boolean> completableFuture, Date givenDate) {
        completableFuture.complete(er.searchLatestBlogMessages(hrefMaxWordsCount(), givenDate));
        return completableFuture;
    }

    Integer hrefMaxWordsCount() {
        Integer DEFAULT_HREFWORDMAX = 10;
        return Optional.ofNullable(Integer.parseInt(config.getProps().getProperty("bmgs.hrefWordMax")))
                .orElse(DEFAULT_HREFWORDMAX);
    }
}
