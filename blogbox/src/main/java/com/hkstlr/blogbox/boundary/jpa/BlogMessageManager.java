package com.hkstlr.blogbox.boundary.jpa;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.hkstlr.blogbox.entities.BlogMessage;
//metamodel
import com.hkstlr.blogbox.entities.BlogMessage_;

@Stateless
public class BlogMessageManager{

    @PersistenceContext(unitName = "com.hkstlr.blogbox")
    EntityManager em;

    public BlogMessageManager(){
        super();
    }
    

    public List<BlogMessage> allBlogMessages() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BlogMessage> cq = cb.createQuery(BlogMessage.class);
        Root<BlogMessage> t = cq.from(BlogMessage.class);
        cq.orderBy(cb.desc(t.get(BlogMessage_.MESSAGE_NUMBER)));
        CriteriaQuery<BlogMessage> all = cq.select(t);
        TypedQuery<BlogMessage> q = em.createQuery(all);
        return q.getResultList();
    }

    
    public List<BlogMessage> findRange(Integer[] range) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<BlogMessage> cq = cb.createQuery(BlogMessage.class);
        Root<BlogMessage> t = cq.from(BlogMessage.class);
        CriteriaQuery<BlogMessage> all = cq.select(t);
        all.orderBy(cb.desc(t.get(BlogMessage_.MESSAGE_NUMBER)));
        
        TypedQuery<BlogMessage> q = em.createQuery(all);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public Integer count() {
        CriteriaQuery<Object> cq = em.getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<BlogMessage> rt = cq.from(BlogMessage.class);
        cq.select(em.getCriteriaBuilder().count(rt));
        javax.persistence.Query q = em.createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public BlogMessage getBlogMessageByHref(String href) {
        TypedQuery<BlogMessage> q = em.createNamedQuery("BlogMessage.findByHref", BlogMessage.class);
        q.setParameter(BlogMessage_.HREF, href)
        .setMaxResults(1);
        return q.getSingleResult();
    }

    public BlogMessage getBlogMessageByMessageNumber(Integer msgNum) {
        TypedQuery<BlogMessage> q = em.createNamedQuery("BlogMessage.findByMessageNumber", BlogMessage.class);
        q.setParameter(BlogMessage_.MESSAGE_NUMBER, msgNum)
        .setMaxResults(1);
        return q.getSingleResult();
    }

    public List<BlogMessage> getBlogMessageRange(Integer start, Integer end) {
        Integer[] range = new Integer[2];
        range[0] = start;
        range[1] = end;
        return findRange(range);
    }

    public void deleteByHrefNotIn(String[] hrefs){
        Query query = em.createQuery("DELETE BlogMessage b WHERE b.href NOT IN (:hrefs)");
        query.setParameter("hrefs", Arrays.asList(hrefs));
        query.executeUpdate();
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