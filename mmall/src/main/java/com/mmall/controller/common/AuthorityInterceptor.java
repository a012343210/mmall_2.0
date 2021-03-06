package com.mmall.controller.common;

import com.google.common.collect.Maps;
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
        //请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod)handler;

        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数,具体的参数key以及value是什么，我们打印日志
        StringBuilder stringBuilder = new StringBuilder();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            //request这个参数的map，里面的value返回的是一个String[]
            Object obj = entry.getValue();
            //mapValue作为最终的数据接收容器
            String mapValue = StringUtils.EMPTY;
            if(obj instanceof String[]){
                String[] values = (String[]) obj;
                mapValue = Arrays.toString(values);
            }
            stringBuilder.append(key).append("=").append(mapValue);
        }

        if(StringUtils.equals(className,"UserManageController") && StringUtils.equals(methodName,"login")){
            log.info("权限拦截器拦截到请求,className:{},methodName:{}",className,methodName);
            //如果是拦截到登录请求，不打印参数，因为参数里面有密码，全部会打印到日志中，防止日志泄露
            return true;
        }
        log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}",className,methodName,stringBuilder.toString());

        String login_token = CookieUtil.getCookie(httpServletRequest);
        User user = null;
        if(StringUtils.isNotEmpty(login_token)){
            String userJson = RedisShardedPoolUtil.get(login_token);
            user = JsonUtils.String2Object(userJson, User.class);
        }
        if(user == null || user.getRole().intValue() != Const.Role.ROLE_ADMIN){
            //返回false.即不会调用controller里的方法
            //这里要添加reset，否则报异常 getWriter() has already been called for this response.
            httpServletResponse.reset();
            //这里要设置编码，否则会乱码
            httpServletResponse.setCharacterEncoding("UTF-8");
            //这里要设置返回值的类型，因为全部是json接口
            httpServletResponse.setContentType("application/json;Charset=UTF-8");
            //上传由于富文本的控件要求，要特殊处理返回值，这里面区分是否登录以及是否有权限
            PrintWriter out = httpServletResponse.getWriter();
            if(user == null){
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","请登录管理员");
                    out.print(JsonUtils.objToString(resultMap));
                }else{
                    out.print(JsonUtils.objToString(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
                }
            }else{
                if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","无权限操作");
                    out.print(JsonUtils.objToString(resultMap));
                }else{
                    out.print(JsonUtils.objToString(ServerResponse.createByErrorMessage("拦截器拦截,用户无权限操作")));
                }
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