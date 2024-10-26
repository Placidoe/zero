package com.explorex.zero.config;

import com.explorex.zero.common.interceptor.ActivityDegradationInterceptor;
import com.explorex.zero.common.interceptor.CustomTokenBucketRateLimiterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private CustomTokenBucketRateLimiterInterceptor customTokenBucketRateLimiterInterceptor;

    @Autowired
    private ActivityDegradationInterceptor activityDegradationInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并指定要拦截的路径

        //活动降级拦截器
        registry.addInterceptor(activityDegradationInterceptor)
                .addPathPatterns("/**"); // 拦截所有路径

        //令牌桶拦截器
        registry.addInterceptor(customTokenBucketRateLimiterInterceptor)
                .addPathPatterns("/**"); // 拦截所有路径



    }
}