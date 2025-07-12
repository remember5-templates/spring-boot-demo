package com.remember5.elasticsearch7.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.time.Duration;

/**
 * 手动创建链接es
 * @author wangjiahao
 * @date 2022/5/25 19:55
 */
//@Configuration
public class ElasticSearchConfiguration {

//    @Bean
    public RestHighLevelClient elasticsearchClient() {
        // 使用构建器来提供集群地址，设置默认值HttpHeaders或启用SSL。
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                // 设置连接地址
                .connectedTo("127.0.0.1:9200")
                // 可以设置多个地址
                // .connectedTo("127.0.0.1:9200", "127.0.0.1:9201")
                // 是否启用ssl
                // .usingSsl()
                // 设置连接超时时间
                .withConnectTimeout(Duration.ofSeconds(10))
                // 设置
                .withSocketTimeout(Duration.ofSeconds(30))
                // 设置用户名密码
                // .withBasicAuth("elastic", "123456")
                // 创建连接信息
                .build();

        // 创建RestHighLevelClient。
        return RestClients.create(clientConfiguration).rest();
    }
}
