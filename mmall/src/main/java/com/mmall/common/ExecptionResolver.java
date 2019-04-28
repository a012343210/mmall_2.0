package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: Administrator
 * @Date: 2019/4/27 22:58
 * @Description:
 */
@Slf4j
@Component
public class ExecptionResolver implements HandlerExceptionResolver {
    @Override
    /**
     *
     * 功能描述: 全局异常处理类
     *
     * @param: [httpServletRequest, httpServletResponse, o, e]
     * @return: org.springframework.web.servlet.ModelAndView
     * @auther: Administrator
     * @date: 2019/4/27 23:07
     */
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("{},Execption",httpServletRequest.getRequestURI(),e);
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());
        modelAndView.addObject("status",ResponseCode.ERROR.getCode());
        modelAndView.addObject("msg","接口异常");
        modelAndView.addObject("data",e.toString());
        return modelAndView;
    }
}