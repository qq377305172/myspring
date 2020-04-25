package com.jing.interceptor;

import com.example.demo.annotations.LoginRequired;
import com.example.demo.util.CookieUtil;
import com.example.demo.util.HttpClientUtil;
import com.example.demo.util.JsonUtil;
import com.jfinal.kit.StrKit;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Admin
 * @title: AuthInterceptor
 * @projectName demo
 * @description: TODO
 * @date 2020/3/12 21:30
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        if (methodAnnotation == null) {
            //当前访问的方法无需登录,比如搜索方法,直接放行
            return true;
        }
        //需要登录
        //获取方法的是否需要登录成功注解的值
        boolean loginSuccess = methodAnnotation.loginSuccess();
        //获取cookie中的旧的token
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        //获取请求url中携带的token参数
        String newToken = request.getParameter("token");
        //以新的token为准
        String token = StrKit.isBlank(newToken) ? oldToken : newToken;
        if (StrKit.isBlank(token)) {
            //新旧token都为空则不进行验证,直接中定向到认证中心
            try {
                response.sendRedirect("http://127.0.0.1:9000/toLogin?returnUrl=" + request.getRequestURL().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        //使用认证中心进行验证
        String ip = request.getHeader("x-forwarded-for");
        ip = ip == null ? request.getRemoteAddr() : ip;
        ip = ip == null ? "127.0.0.1" : ip;
        String doGet = HttpClientUtil.doGet("http://127.0.0.1:9000/verify?token=" + token + "&currentIp=" + ip);
        Map<String, Object> userMap = JsonUtil.json2Map(doGet) == null ? new HashMap<>() : JsonUtil.json2Map(doGet);

        if (loginSuccess) {
            //当前请求的方法要求必须登录验证成功
            if (!"success".equals(userMap.get("status"))) {
                //验证失败,重定向回认证中心登录
                try {
                    response.sendRedirect("http://127.0.0.1:9000/toLogin?returnUrl=" + request.getRequestURL().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        } else {
            //当前请求的方法成功登陆或未登录都可以,但是成功登录和未登录的逻辑不通,比如购物车功能
            if (!"success".equals(userMap.get("status"))) {
                //验证失败,也放行,但是直接return,不走下面的更新cookie和设置会员信息代码
                return true;
            }
        }
        //当前请求的方法需要登录验证成功,且用户登录验证成功
        //更新cookie中的token
        CookieUtil.setCookie(request, response, "oldToken", token, 60 * 30, true);
        //向request域中设置会员信息
        request.setAttribute("memberId", userMap.get("memberId"));
        request.setAttribute("nickName", userMap.get("nickName"));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }
}
