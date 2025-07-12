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
import com.sun.mail.imap.IMAPStore;
import lombok.RequiredArgsConstructor;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;
import java.util.Properties;

/**
 * IMAP客户端
 *
 * @author wangjiahao
 * @date 2025/5/15 15:58
 */
@RequiredArgsConstructor
public abstract class BaseImapClient implements EmailClient {

    protected final EmailProperties.ClientConfig config;
    protected IMAPStore store;
    protected Folder inbox;

    @Override
    public void connect() throws Exception {
        Properties props = new Properties();
        // 大多数厂商使用SSL
        props.put("mail.imap.ssl.enable", "true");
        Session session = Session.getInstance(props);

        URLName url = new URLName("imap", config.getVendor().getHost(), config.getVendor().getPort(), "", config.getUser(), config.getAuthCode());
        store = (IMAPStore) session.getStore(url);
        store.connect();

        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
    }

    /**
     * 公共关闭逻辑可在finally块调用
     *
     * @throws MessagingException 异常
     */
    public void close() throws MessagingException {
        if (inbox != null) {
            inbox.close(false);
        }
        if (store != null) {
            store.close();
        }
    }
}
