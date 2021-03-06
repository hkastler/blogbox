package com.hkstlr.blogbox.boundary.jpa;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
public class BlogMessageManager {

    @PersistenceContext(unitName = "com.hkstlr.blogbox")
    EntityManager em;

    public BlogMessageManager() {
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
        Integer maxResults = range[1] - range[0] + 1;
        if (maxResults > 0) {
            q.setMaxResults(maxResults);
        }
        q.setFirstResult(range[0]);
        q.setHint("org.hibernate.cacheable", Boolean.TRUE);
        return q.getResultList();
    }

    public List<BlogMessage> getBlogMessageRange(Integer start, Integer end) {
        Integer[] range = new Integer[2];
        range[0] = start;
        range[1] = end;
        return findRange(range);
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
        q.setParameter(BlogMessage_.HREF, href).setMaxResults(1);
        return this.getSingleResult(q);
    }

    public BlogMessage getBlogMessageByMessageId(String messageId) {
        TypedQuery<BlogMessage> q = em.createNamedQuery("BlogMessage.findByMessageId", BlogMessage.class);
        q.setParameter(BlogMessage_.MESSAGE_ID, messageId).setMaxResults(1);
        return this.getSingleResult(q);
    }

    public BlogMessage getBlogMessageByMessageNumber(Integer msgNum) {
        TypedQuery<BlogMessage> q = em.createNamedQuery("BlogMessage.findByMessageNumber", BlogMessage.class);
        q.setParameter(BlogMessage_.MESSAGE_NUMBER, msgNum).setMaxResults(1);
        return this.getSingleResult(q);
    }

    public BlogMessage getTopMessage() {
        TypedQuery<BlogMessage> q = em.createQuery("SELECT b FROM BlogMessage b ORDER BY messageNumber DESC", BlogMessage.class);
        q.setMaxResults(1);
        return this.getSingleResult(q);
    }

    BlogMessage getSingleResult(TypedQuery<BlogMessage> q) {
        BlogMessage b;
        try {
            q.setHint("org.hibernate.cacheable", Boolean.TRUE);
            b = q.getSingleResult();
        } catch (NoResultException e) {
            b = null;
        }
        return b;
    }

    Object wrappedRefQuery(Query q) {
        Object b;
        try {
            // q.setHint("org.hibernate.cacheable", Boolean.TRUE);
            b = q.getSingleResult();
        } catch (NoResultException e) {
            b = new Object[3];
        }
        return b;
    }

    public Object findRefsByMessageNumber(Integer msgNum) {
        Object[] obj = new Object[2];

        try {
            Query q = em.createNativeQuery(
                    "SELECT b.href, b.subject, b.messageNumber FROM BlogMessage b WHERE  b.messageNumber = :msgNum ");
            q.setParameter("msgNum", msgNum - 1);

            obj[0] = wrappedRefQuery(q);

            q.setParameter("msgNum", msgNum + 1);

            obj[1] = wrappedRefQuery(q);

        } catch (NoResultException nre) {
            obj = null;
        }

        return obj;
    }

    public void deleteByHrefNotIn(String[] hrefs) {
        Query query = em.createQuery("DELETE FROM BlogMessage b WHERE b.href NOT IN (:hrefs)");
        query.setParameter("hrefs", Arrays.asList(hrefs));
        query.executeUpdate();
    }

    public void deleteByMessageIdNotIn(String[] messageIds) {
        Query query = em.createQuery("DELETE FROM BlogMessage b WHERE b.messageId NOT IN (:messageIds)");
        query.setParameter("messageIds", Arrays.asList(messageIds));
        query.executeUpdate();
    }

    public void clearCache() {
        em.getEntityManagerFactory().getCache().evictAll();
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
