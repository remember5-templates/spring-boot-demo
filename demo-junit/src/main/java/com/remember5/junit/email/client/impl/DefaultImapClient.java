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
package com.remember5.junit.email.client.impl;


import com.remember5.junit.email.config.EmailProperties;

import javax.mail.Message;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2025/5/15 16:09
 */
public class DefaultImapClient extends BaseImapClient {

    public DefaultImapClient(EmailProperties.ClientConfig config) {
        super(config);
    }

    @Override
    public List<Message> fetchMessages() throws Exception {
        return List.of();
    }

    @Override
    public void moveToSucceededFolder(Message message) throws Exception {

    }
}
