package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtils;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2019/4/28 20:18
 * @Description:
 */

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getName();
        StringBuilder stringBuilder = new StringBuilder();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            Object obj = entry.getValue();
            String mapValue = StringUtils.EMPTY;
            if(obj instanceof String[]){
                String[] values = (String[]) obj;
                mapValue = Arrays.toString(values);
            }
            stringBuilder.append(key).append("=").append(mapValue);
        }
        String login_token = CookieUtil.getCookie(httpServletRequest);
        User user = null;
        if(StringUtils.isNotEmpty(login_token)){
            String userJson = RedisShardedPoolUtil.get(login_token);
            user = JsonUtils.String2Object(userJson, User.class);
        }
        if(user == null || user.getRole().intValue() != Const.Role.ROLE_ADMIN){
            httpServletResponse.reset();//必须重置否则报错 getWrite has already
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setContentType("application/json;Charset=UTF-8");

            PrintWriter out = httpServletResponse.getWriter();
            if(user == null){
                out.print(JsonUtils.objToString(ServerResponse.createByErrorMessage("您还未登录账户")));
            }else{
                out.print(JsonUtils.objToString(ServerResponse.createByErrorMessage("请使用管理员账户登录")));
            }
            out.flush();
            out.close();
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}