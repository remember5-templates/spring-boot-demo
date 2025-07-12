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

import lombok.RequiredArgsConstructor;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2025/5/15 15:59
 */
@RequiredArgsConstructor
public class EmailProcessor {
    private final EmailClientFactory clientFactory;

    private static final String SUCCEEDED_DIR = "/path/to/succeeded";

    public void processAll() {

    }

    // 发件人过滤逻辑
    private boolean isTargetSender(Message msg, List<String> targets) throws MessagingException {
        Address[] from = msg.getFrom();
        return Arrays.stream(from)
                .anyMatch(addr -> targets.contains(((InternetAddress)addr).getAddress()));
    }

    // 示例保存逻辑
    private void parseAndSave(Message msg) {
        // 你的解析代码...
        Path path = Paths.get(SUCCEEDED_DIR, "mail_"+System.currentTimeMillis()+".txt");
        // 写入文件操作...
    }
}
