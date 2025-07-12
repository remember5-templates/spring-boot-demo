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
package com.remember5.springboot3.es8.es;

import com.remember5.springboot3.es8.entity.BurgerKingMerchant;
import com.remember5.springboot3.es8.repository.BurgerKingMerchantRepository;
import com.remember5.springboot3.es8.util.EnhancedCsvLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author wangjiahao
 * @date 2025/7/12 21:41
 */
@Slf4j
@DisplayName("BaseElasticsearchTest")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BaseElasticsearchTest {

    @Autowired
    private BurgerKingMerchantRepository burgerKingMerchantRepository;
    @Test
    void insert() {
        final List<BurgerKingMerchant> burgerKingMerchants = EnhancedCsvLoader.loadCsvData("src/main/resources/bk_stores.csv", BurgerKingMerchant.class);
        burgerKingMerchantRepository.saveAll(burgerKingMerchants);
    }



}
