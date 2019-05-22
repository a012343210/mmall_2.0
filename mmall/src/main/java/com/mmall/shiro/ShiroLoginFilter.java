package com.mmall.shiro;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: Administrator
 * @Date: 2019/5/22 23:04
 * @Description:
 */
public class ShiroLoginFilter extends AdviceFilter {
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //如果是登录请求直接放行
        String servletPath = httpServletRequest.getServletPath();
        if (servletPath.indexOf("login") >= 0 ) {
            return true;
        }

        //单点登录的判断
        String login_token = CookieUtil.getCookie(httpServletRequest);
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/pages/login.jsp");
            return false;
        }
        String token = RedisShardedPoolUtil.get(Const.SHIRO_REDIS_TOKEN_PREFIX + user.getId());
        if (!StringUtils.equals(token, login_token)) {
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/pages/login.jsp");
            return false;
        }
        return true;
    }


}