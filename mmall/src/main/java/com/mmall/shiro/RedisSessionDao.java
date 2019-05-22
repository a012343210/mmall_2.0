package com.mmall.shiro;

import com.mmall.common.Const;
import com.mmall.common.RedisShardedPool;
import com.mmall.common.ShiroSessionConvertUtil;
import com.mmall.pojo.User;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;


import java.io.Serializable;
import java.util.Collection;

/**
 * @Auther: Administrator
 * @Date: 2019/5/19 23:01
 * @Description:
 */
@Slf4j
public class RedisSessionDao extends EnterpriseCacheSessionDAO {

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = super.doCreate(session);
        RedisShardedPoolUtil.setObject((Const.SHIRO_REDIS_SESSION_PREFIX+sessionId.toString()).getBytes(),
        ShiroSessionConvertUtil.sessionToByte(session),Const.SHIRO_REDIS_EXTIRETIME);
        return sessionId;
    }

    /**
     * 从Redis中读取Session,并重置过期时间
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        Session session = null;
        byte[] bytes = RedisShardedPoolUtil.getObject((Const.SHIRO_REDIS_SESSION_PREFIX + sessionId.toString()).getBytes(),Const.SHIRO_REDIS_EXTIRETIME);
        if(null != bytes && bytes.length > 0){
             session = ShiroSessionConvertUtil.byteToSession(bytes);
        }
        return session;
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        super.update(session);
        RedisShardedPoolUtil.updateObject((Const.SHIRO_REDIS_SESSION_PREFIX + session.getId().toString()).getBytes(),
                ShiroSessionConvertUtil.sessionToByte(session),Const.SHIRO_REDIS_EXTIRETIME);
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(null != user){
            RedisShardedPoolUtil.expire(Const.SHIRO_REDIS_TOKEN_PREFIX+user.getId(),Const.SHIRO_REDIS_EXTIRETIME);
        }
    }

    @Override
    public void delete(Session session) {
        super.doDelete(session);
        RedisShardedPoolUtil.del(Const.SHIRO_REDIS_SESSION_PREFIX + session.getId().toString());
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(null != user){
            RedisShardedPoolUtil.del(Const.SHIRO_REDIS_TOKEN_PREFIX+user.getId());
        }
    }

    @Override
    public Session readSession(Serializable sessionId) throws UnknownSessionException {
        return this.doReadSession(sessionId);
    }
}