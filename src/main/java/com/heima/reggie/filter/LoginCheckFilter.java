package com.heima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.heima.reggie.common.BaseContext;
import com.heima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.PublicKey;

//检查用户是否已经完成登录
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestRUI = request.getRequestURI();

        log.info("拦截到请求：{}",requestRUI);

        //定义不需要处理的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**"
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls,requestRUI);
        //3、如果不需要处理，直接放行
        if (check){
            filterChain.doFilter(request,response);
            return;
        }
        //4、判断登录状态，如果已登录，直接放行
        if (request.getSession().getAttribute("employee") != null){

            Long empId = (long)request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //5、如果未登录返回未登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    public boolean check (String[] urls,String requestRUI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url,requestRUI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
