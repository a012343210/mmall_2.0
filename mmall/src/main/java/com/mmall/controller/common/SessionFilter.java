package com.mmall.controller.common;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtils;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Auther: Administrator
 * @Date: 2019/4/24 22:45
 * @Description:
 */
public class SessionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    /**
     *
     * 功能描述: 重置redis中hsx_login_token的存在时间
     *
     * @param: [servletRequest, servletResponse, filterChain]
     * @return: void
     * @auther: Administrator
     * @date: 2019/4/28 22:51
     */
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String login_token = CookieUtil.getCookie(httpServletRequest);
        if(StringUtils.isNotEmpty(login_token)){
            User user = JsonUtils.String2Object(RedisShardedPoolUtil.get(login_token), User.class);
            if(user != null){
                RedisShardedPoolUtil.expire(login_token, Const.RedisCacheExTime.REDIS_CACHE_EX_TIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);

    }

    @Override
    public void destroy() {

    }
}