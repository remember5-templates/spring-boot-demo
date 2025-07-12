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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @author wangjiahao
 * @date 2025/5/15 15:58
 */
public class Mail163Client extends BaseImapClient {

    public Mail163Client(EmailProperties.ClientConfig config) {
        super(config);
    }

    @Override
    public void connect() throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imap");
        props.setProperty("mail.imap.host", config.getVendor().getHost());
//        props.setProperty("mail.imap.ssl.enable", "true");
        props.setProperty("mail.imap.port", String.valueOf(config.getVendor().getPort()));

        HashMap IAM = new HashMap();
        //带上IMAP ID信息，由key和value组成，例如name，version，vendor，support-email等。
        IAM.put("name","myname");
        IAM.put("version","1.0.0");
        IAM.put("vendor","myclient");
        IAM.put("support-email","testmail@test.com");
        Session session = Session.getInstance(props);

        IMAPStore store = (IMAPStore) session.getStore("imap");
        //下方替换对应账号和授权码
        store.connect(config.getUser(), config.getAuthCode());

        store.id(IAM);

        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
    }

    @Override
    public List<Message> fetchMessages() throws Exception {
        return List.of();
    }

    @Override
    public void moveToSucceededFolder(Message message) throws Exception {

    }
}
