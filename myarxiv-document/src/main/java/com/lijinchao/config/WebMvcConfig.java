package com.lijinchao.config;

import com.lijinchao.feign.feignclient.auth.AuthClient;
import com.lijinchao.permission.AuthenticationInterceptor;
import com.lijinchao.uitls.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

//    @Value("${myConfig.staticResource.staticPath}")
//    private String staticPath;

    @Resource
    RedisUtil redisUtil;

    @Resource
    AuthClient authClient;

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
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/file/new")
//                开放测试接口
                .excludePathPatterns("/test/**");
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(redisUtil,authClient);
    }

//    /**
//     * 枚举类的转换器工厂 addConverterFactory
//     */
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        registry.addConverterFactory(new IntegerCodeToEnumConverterFactory());
//        registry.addConverterFactory(new StringCodeToEnumConverterFactory());
//    }

}
