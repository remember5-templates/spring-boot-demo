package com.remember.elasticsearch6.configuration;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wangjiahao
 * @date 2020/3/23
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {


    /**
     * 允许跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("跨域已设置");
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }


    /**
     * 添加拦截器
     *
     * @param registry 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        log.info("拦截器已设置");
//        registry.addInterceptor(myInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns(Lists.newArrayList(apiConstant.getExcludePathList().split(StrUtil.COMMA)));
    }
}
