package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Auther: Administrator
 * @Date: 2019/5/7 21:20
 * @Description:
 */

@Component
@Slf4j
public class RedissonManager {
    private static Config config = new Config();
    private static Redisson redisson = null;

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    @PostConstruct
    private void init(){
        try {
            config.useSingleServer().setAddress(new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString());
            redisson = (Redisson) Redisson.create(config);
            log.info("Redisson 初始化完毕");
        } catch (Exception e) {
            log.error("Redisson 初始化失败",e);
        }
    }

    public static Redisson getRedisson() {
        return redisson;
    }
}