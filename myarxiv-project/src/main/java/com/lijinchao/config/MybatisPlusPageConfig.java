package com.lijinchao.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.lijinchao.common.MyMetaObjectHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class MybatisPlusPageConfig {

    @Resource
    MyMetaObjectHandler myMetaObjectHandler;

    /**
     * 分页查询插件
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        //创建拦截器，对执行的sql进行拦截
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //对Mysql拦截
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
