package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: Administrator
 * @Date: 2019/5/5 22:14
 * @Description:
 */

@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;

    //每一分钟执行一次关单
    //@Scheduled(cron = "0 */1 * * * ?")
    public void CloseOrderV1(){
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.hour"));
        iOrderService.closeOrder(hour);
    }
    //每一分钟执行一次关单
    //@Scheduled(cron = "0 */1 * * * ?")
    public void CloseOrderV2(){
        log.info("定时关单启动");
        Long timeout = Long.parseLong(PropertiesUtil.getProperty("close.order.timeout"));
        Long result = RedisShardedPoolUtil.setnx(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME, String.valueOf(System.currentTimeMillis()+timeout));
        if(result != null && result.intValue() == 1){
            //设置成功获取到锁,执行关单
            closeOrder(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
        }else{
            log.info("没有获得分布式锁:{}",Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
        }
        log.info("定时关单结束");
    }

    //每一分钟执行一次关单
    //@Scheduled(cron = "0 */1 * * * ?")
    public void CloseOrderV3(){
        log.info("定时关单启动");
        Long timeout = Long.parseLong(PropertiesUtil.getProperty("close.order.timeout"));
        Long result = RedisShardedPoolUtil.setnx(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME, String.valueOf(System.currentTimeMillis()+timeout));
        if(result != null && result.intValue() == 1){
            //设置成功获取到锁,执行关单
            closeOrder(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
        }else{
            String oldLockTime = RedisShardedPoolUtil.get(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
            if(oldLockTime != null && (System.currentTimeMillis() > Long.parseLong(oldLockTime))){
                String getSetResult = RedisShardedPoolUtil.getSet(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME, String.valueOf(System.currentTimeMillis() + timeout));
                //两次获取的值相同说明没有其他线程正在使用这个锁
                //getSetResult值与oldLockTime值不同  或者 getSetResult为null 说明有线程正在使用 不需要重新获得锁
                if(StringUtils.equals(getSetResult,oldLockTime)){
                    closeOrder(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
                }else{
                    log.info("没有获得分布式锁:{}",Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
                }
            }
            log.info("没有获得分布式锁:{}",Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
        }
        log.info("定时关单结束");
    }

    //每一分钟执行一次关单
    @Scheduled(cron = "0 */1 * * * ?")
    public void CloseOrderV4(){
        Redisson redisson = RedissonManager.getRedisson();
        RLock lock = redisson.getLock(Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME);
        Boolean getLock = false;
        try {
            log.info("Redisson 开始获取锁:{},ThreadName为:{}",Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME,Thread.currentThread().getName());
            getLock = lock.tryLock(0,50,TimeUnit.SECONDS);
            if(getLock){
                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.hour"));
                iOrderService.closeOrder(hour);
            }else{
                log.info("Redisson没有获取到分布式锁:{},ThreadName:{}",Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME,Thread.currentThread().getName());
            }
        } catch (Exception e) {
            log.info("Redisson没有获取到分布式锁",e);
        }finally {
            if(!getLock){
                return;
            }
            lock.unlock();
            log.info("Redisson 释放锁成功");
        }

    }

    private void closeOrder(String key){
        //设置有效期防止死锁
        RedisShardedPoolUtil.expire(key,50);
        log.info("当前锁为:{},Thread为{}",Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.hour"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(key);
        log.info("释放锁为:{},Thread为{}",Const.closeOrderLock.CLOSE_ORDER_LOCK_TIME,Thread.currentThread().getName());
        log.info("========================================");
    }
}