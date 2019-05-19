package com.mmall.common;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * @Auther: Administrator
 * @Date: 2019/5/19 13:50
 * @Description:
 */

@Slf4j
public class AppServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    /*解决toncat关闭时报“未撤销注册的JDBC驱动”错误 The web application [] registered the JDBC driver [com.alibaba.druid.mock.MockDriver]
    but failed to unregister it when the web application was stopped.
    To prevent a memory leak, the JDBC Driver has been forcibly unregistered.*/
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log.info( String.format("deregistering jdbc driver: %s", driver));
            } catch (SQLException e) {
                log.info( String.format("Error deregistering driver %s", driver), e);
            }
        }
    }
}