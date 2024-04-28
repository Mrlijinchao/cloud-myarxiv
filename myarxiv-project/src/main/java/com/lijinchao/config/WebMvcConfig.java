package com.lijinchao.config;

import com.lijinchao.permission.AuthenticationInterceptor;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

//    @Value("${myConfig.staticResource.staticPath}")
//    private String staticPath;

    @Resource
    UserService userService;

    @Resource
    RedisUtil redisUtil;

    /**
     * 静态资源映射
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("file:"+staticPath);
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authenticationInterceptor());
        registry.addInterceptor(authenticationInterceptor())
//                表示拦截所有请求
                .addPathPatterns("/**")
                .excludePathPatterns("/openapi/**")
                .excludePathPatterns("/auth/doLogin")
                .excludePathPatterns("/auth/**")
                .excludePathPatterns("/email/**")
                .excludePathPatterns("/attr/query")
                .excludePathPatterns("/subject/query")
                .excludePathPatterns("/user/register")
                .excludePathPatterns("/auth/checkLoginState")
                .excludePathPatterns("/user/checkUserCodeIsExist")
                .excludePathPatterns("/user/checkUserEmailIsExist")
                .excludePathPatterns("/user/checkUserPhoneIsExist")
                .excludePathPatterns("/home/**")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/paper/getPaperByPage")
                .excludePathPatterns("/paper/queryPaperInfoById")
                .excludePathPatterns("/paper/queryPaperByCondition")
                .excludePathPatterns("/paper/queryPaperVersionAll")
                .excludePathPatterns("/subject")
//                开放测试接口
                .excludePathPatterns("/test/**");
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(userService, redisUtil);
    }


}
