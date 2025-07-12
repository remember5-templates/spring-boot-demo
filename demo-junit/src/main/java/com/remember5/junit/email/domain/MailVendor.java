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
package com.remember5.junit.email.domain;


import lombok.Getter;


/**
 * @author wangjiahao
 * @date 2025/5/15 16:28
 */
@Getter
public enum MailVendor {
    /**
     * 126邮箱
     */
    _126("imap.126.com", 143),
    /**
     * 163邮箱
     */
    _163("imap.163.com", 143),
    /**
     * 189邮箱
     */
    _189("imap.189.cn", 143),
    /**
     * 新浪邮箱
     */
    SINA("imap.sina.com.cn", 143),
    /**
     * qq邮箱
     */
    QQ("imap.qq.com", 143);
    /**
     * 自定义类型标记
     */

    private final String host;
    private final Integer port;

    MailVendor(String host, Integer port) {
        this.host = host;
        this.port = port;
    }
}
