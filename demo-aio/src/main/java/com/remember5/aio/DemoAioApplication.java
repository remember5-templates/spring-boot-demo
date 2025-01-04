package com.remember5.aio;

import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author wangjiahao
 * @date 2024/01/04 22:09
 */
@MapperScan(basePackages = {"com.remember5.aio.mapper"})
@SpringBootApplication
public class DemoAioApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoAioApplication.class, args);
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
    }

}
