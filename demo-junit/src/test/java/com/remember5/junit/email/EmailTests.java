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
package com.remember5.junit.email;

import com.remember5.junit.SpringBootDemoJunitApplicationTests;
import com.remember5.junit.email.client.impl.EmailClient;
import com.remember5.junit.email.config.EmailProperties;
import com.remember5.junit.email.service.EmailClientFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wangjiahao
 * @date 2025/5/12 16:51
 */
public class EmailTests extends SpringBootDemoJunitApplicationTests {

    @Autowired
    EmailClientFactory clientFactory;

    @Autowired
    EmailProperties emailProperties;

    @Test
    public void testICBC() throws Exception {
        final EmailProperties.ClientConfig icbcConfig = emailProperties.getIcbc();
        final EmailClient client = clientFactory.getClient(icbcConfig.getVendor(), icbcConfig);

        client.connect();


    }

}
