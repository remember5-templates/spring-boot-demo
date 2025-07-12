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
package com.remember5.junit.email.config;

import com.remember5.junit.email.domain.MailVendor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 读取配置文件
 *
 * @author wangjiahao
 * @date 2025/5/15 15:53
 */
@Data
@Configuration
@ConfigurationProperties(prefix = EmailProperties.PREFIX_KEY)
public class EmailProperties {
    public static final String PREFIX_KEY = "my-email";

    @NestedConfigurationProperty
    private ClientConfig icbc;

    @NestedConfigurationProperty
    private ClientConfig ccb;

    @NestedConfigurationProperty
    private ClientConfig boc;

    @NestedConfigurationProperty
    private ClientConfig psbc;

    @NestedConfigurationProperty
    private ClientConfig bocd;

    @NestedConfigurationProperty
    private ClientConfig cdrcb;


    /**
     * 嵌套配置对象
     */
    @Data
    public static class ClientConfig {
        private MailVendor vendor;
        private String user;
        private String authCode;
        private List<String> targetSenders;
    }
}


