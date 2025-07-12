package com.remember.elasticsearch6.configuration;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author wangjiahao
 * EnbaleTransactional Spring boot方式 开启事物
 * @date 2020/3/25
 */
@EnableTransactionManagement
@Configuration
@MapperScan(basePackages = {"com.remember.elasticsearch6.mapper"})
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
