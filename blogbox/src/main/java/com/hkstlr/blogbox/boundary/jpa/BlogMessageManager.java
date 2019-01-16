package com.hkstlr.blogbox.boundary.jpa;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.hkstlr.blogbox.entities.BlogMessage;
//metamodel
import com.hkstlr.blogbox.entities.BlogMessage_;

@Stateless
public class BlogMessageManager{

    @PersistenceContext
    protected EntityManager em;

    public BlogMessageManager(){
        super();
    }

    public List<BlogMessage> allBlogMessages() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BlogMessage> cq = cb.createQuery(BlogMessage.class);
        Root<BlogMessage> rootEntry = cq.from(BlogMessage.class);
        CriteriaQuery<BlogMessage> all = cq.select(rootEntry);
        TypedQuery<BlogMessage> q = em.createQuery(all);
        return q.getResultList();
    }

    public BlogMessage getBlogMessageByHref(String href) {
        TypedQuery<BlogMessage> q = em.createNamedQuery("BlogMessage.findByHref", BlogMessage.class);
        q.setParameter(BlogMessage_.HREF, href)
        .setMaxResults(1);
        return (BlogMessage) q.getSingleResult();
    }

    public List<BlogMessage> getBlogMessageRange(Integer start, Integer end) {
        TypedQuery<BlogMessage> q = em.createNamedQuery("BlogMessage.findMessageNumberRange", BlogMessage.class);
        q.setParameter("messageNumberStart",start)
        .setParameter("messageNumberEnd", end);
        return q.getResultList();
    }

    public void saveBlogMessage(BlogMessage bmsg){
        em.persist(bmsg);
    }

    /**
     * @return the em
     */
    public EntityManager getEm() {
        return em;
    }

    /**
     * @param em the em to set
     */
    public void setEm(EntityManager em) {
        this.em = em;
    }



}