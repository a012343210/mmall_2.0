package com.mmall.common;

import com.mmall.pojo.User;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.shiro.session.Session;

import java.io.Serializable;

/**
 * @Auther: Administrator
 * @Date: 2019/5/21 21:32
 * @Description:
 */
public class ShiroSessionRedisUtil {
    public static Session getSession(Serializable sessionId){
        Session session = null;
        byte[] bytes = RedisShardedPoolUtil.getObject((Const.SHIRO_REDIS_SESSION_PREFIX+sessionId.toString()).getBytes(),Const.SHIRO_REDIS_EXTIRETIME);
        if( null != bytes && bytes.length > 0){
            session = ShiroSessionConvertUtil.byteToSession(bytes);
        }
        return session;
    }
    public static void updateSession(Session session){
        RedisShardedPoolUtil.updateObject((Const.SHIRO_REDIS_SESSION_PREFIX+session.getId().toString()).getBytes(),ShiroSessionConvertUtil.sessionToByte(session),Const.SHIRO_REDIS_EXTIRETIME);
        //也要更新token
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(null != user){
            RedisShardedPoolUtil.expire(Const.SHIRO_REDIS_TOKEN_PREFIX+user.getId(),Const.SHIRO_REDIS_EXTIRETIME);
        }
    }

    public static void deleteSession(Session session){
        RedisShardedPoolUtil.delString(Const.SHIRO_REDIS_SESSION_PREFIX+session.getId().toString());
        //也要删除token
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(null != user){
            RedisShardedPoolUtil.delString(Const.SHIRO_REDIS_TOKEN_PREFIX+user.getId());
        }
    }

}