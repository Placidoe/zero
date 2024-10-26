package com.explorex.zero.common.interceptor;

import com.explorex.zero.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class ActivityDegradationInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;

    public ActivityDegradationInterceptor(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从Redis中获取特定键的值
        String key = "activityStatus"; // 假设这是你要检查的键，可来自请求头
        String value = (String) redisUtils.get(key);

        if ("1".equals(value)) {
            // 如果值为1，执行降级逻辑
            handleDegradation(response);
            return false; // 返回false表示不再继续处理请求
        }

        // 如果值不为1，继续处理请求
        return true;
    }

    private void handleDegradation(HttpServletResponse response) throws IOException {
        // 设置响应状态码和内容类型
        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 创建PrintWriter以写入响应体
        try (PrintWriter writer = response.getWriter()) {
            // 写入JSON格式的降级信息
            writer.print("{\"status\":\"degraded\",\"message\":\"Service is temporarily unavailable\"}");
            //这里可以是抛出异常
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 可选：处理请求完成后需要执行的操作
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 可选：清理资源等操作
    }
}