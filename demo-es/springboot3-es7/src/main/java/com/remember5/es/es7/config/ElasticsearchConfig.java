/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.es.es7.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.convert.MappingElasticsearchConverter;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;

/**
 * @author wangjiahao
 * @date 2025/7/11 23:58
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.rest.uris}")
    private String[] uris;

    @Value("${spring.elasticsearch.rest.username}")
    private String username;

    @Value("${spring.elasticsearch.rest.password}")
    private String password;

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public RestHighLevelClient elasticsearchClient() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(HttpHost.create(uris[0]))
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        return new RestHighLevelClient(builder);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ElasticsearchConverter elasticsearchConverter() {
        return new MappingElasticsearchConverter(new SimpleElasticsearchMappingContext());
    }



    @Bean
    @Primary
    @ConditionalOnMissingBean
    public ElasticsearchRestTemplate elasticsearchRestTemplate(RestHighLevelClient client, ElasticsearchConverter converter) {
        return new ElasticsearchRestTemplate(client, converter);
    }
}
