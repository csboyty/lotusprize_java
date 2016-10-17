package com.zhongyi.lotusprize.auth.cache;

import java.io.Serializable;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;


public class ShiroRedisCacheSessionDAO extends CachingSessionDAO {

    @Override
    protected void doUpdate(Session session) {
        //does nothing - parent class removes from cache.
    }

    @Override
    protected void doDelete(Session session) {
        //does nothing - parent class persists to cache.
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        return null; //should never execute because this implementation relies on parent class to access cache, which
        //is where all sessions reside - it is the cache implementation that determines if the
        //cache is memory only or disk-persistent, etc.
    }

}
