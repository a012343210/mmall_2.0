package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Properties;

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
    @Scheduled(cron = "0 */1 * * * ?")
    public void CloseOrderV1(){
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.hour"));
        iOrderService.closeOrder(hour);
    }
}