package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * @Auther: Administrator
 * @Date: 2019/5/21 22:07
 * @Description:
 */
@Slf4j
public class ShiroSessionListener implements SessionListener{

    @Override
    public void onStart(Session session) {
        log.info("ShiroSessionListener session {} 被创建", session.getId());
    }

    @Override
    public void onStop(Session session) {
        ShiroSessionRedisUtil.deleteSession(session);
        log.info("ShiroSessionListener session {} 被销毁", session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        ShiroSessionRedisUtil.deleteSession(session);
        log.info("ShiroSessionListener session {} 过期", session.getId());

    }
}