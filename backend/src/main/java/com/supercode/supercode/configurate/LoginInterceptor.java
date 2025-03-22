package com.supercode.supercode.configurate;

import com.supercode.supercode.exception.SupercodeException;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.supercode.supercode.util.TokenUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Objects;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    TokenUtil tokenUtil;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Cookie[] cookies=request.getCookies();
        if(cookies==null)
            throw SupercodeException.loginRequired();
        String token=null;
        for(Cookie cookie:cookies)
        {
            if(Objects.equals(cookie.getName(), "token"))
            {
                token=cookie.getValue();
                break;
            }
        }
        if (token != null && tokenUtil.vertifyToken(token)) {
            // For Frontend to get Information about current User
            // request.getSession().setAttribute("currentUser", tokenUtil.getUser(token));
            return true;
        } else {
            throw SupercodeException.loginRequired();
        }
    }
}
