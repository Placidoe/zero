//package com.explorex.zero.common.interceptor;
//
//import com.explorex.zero.constants.HttpConstant;
//import com.google.common.util.concurrent.RateLimiter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@Component
//public class TokenBucketRateLimiterInterceptor implements HandlerInterceptor {
//
//    private final RateLimiter rateLimiter = RateLimiter.create(5.0); // 每秒生成5个令牌
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 获取请求中的 flag 参数
//        String flag = request.getParameter("flag");
//
//        if ("true".equals(flag)) {
//            // 如果 flag 为 true，则进行限流
//            if (rateLimiter.tryAcquire()) {
//                // 成功获取令牌，允许请求通过
//                return true;
//            } else {
//                // 无法获取令牌，返回 429 Too Many Requests
//                response.setStatus(HttpConstant.SC_TOO_MANY_REQUESTS);
//                response.getWriter().write("Too many requests, please try again later.");
//                return false;
//            }
//        }
//
//        // 如果 flag 不为 true 或不存在，则不限流
//        return true;
//    }
//}