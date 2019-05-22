package com.mmall.shiro;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: Administrator
 * @Date: 2019/5/22 14:55
 * @Description:
 * 暂时还未使用
 */
public class CheckLoginFilter extends AccessControlFilter {

    private String LOGIN_PATH = "/pages/login.jsp";
    public static HttpServletRequest httpServletRequest;

    //过滤是否已经登录
    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        User user =(User) subject.getPrincipal();
        String login_token = CookieUtil.getCookie(httpServletRequest);
        if (StringUtils.isEmpty(login_token)) {
            return false;
        }
        String token = RedisShardedPoolUtil.get(Const.SHIRO_REDIS_TOKEN_PREFIX + user.getId());
        if(StringUtils.equals(token,login_token)){
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+LOGIN_PATH);
        return false;
    }

}