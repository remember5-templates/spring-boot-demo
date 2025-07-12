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
package com.remember5.junit.email.service;

import com.remember5.junit.email.client.impl.*;
import com.remember5.junit.email.config.EmailProperties;
import com.remember5.junit.email.domain.MailVendor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮件客户端工厂
 *
 * @author wangjiahao
 * @date 2025/5/15 15:59
 */
@Component
@RequiredArgsConstructor
public class EmailClientFactory {

    private final Map<String, EmailClient> clients = new ConcurrentHashMap<>();

    /**
     * 获取客户端
     *
     * @param mailVendor 邮件提供商
     * @param config     邮箱配置
     * @return 客户端
     */
    public EmailClient getClient(MailVendor mailVendor, EmailProperties.ClientConfig config) {
        String clientId = mailVendor.name();
        return clients.computeIfAbsent(clientId, id -> {
            if (config == null) {
                throw new IllegalArgumentException("无效配置: " + id);
            }

            if (MailVendor._126.name().equals(id)) {
                return new Mail126Client(config);
            }

            if (MailVendor._163.name().equals(id)) {
                return new Mail163Client(config);
            }

            if (MailVendor._189.name().equals(id)) {
                return new Mail189Client(config);
            }

            if (MailVendor.QQ.name().equals(id)) {
                return new MailQQClient(config);
            }

            if (MailVendor.SINA.name().equals(id)) {
                return new MailSinaClient(config);
            }
            return new DefaultImapClient(config); // 使用具体实现类
        });
    }
}
