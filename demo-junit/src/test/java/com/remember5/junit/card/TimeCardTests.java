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
package com.remember5.junit.card;

import cn.hutool.core.util.StrUtil;
import com.remember5.junit.card.category.TimeCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * @author wangjiahao
 * @date 2025/9/16 16:12
 */
@Slf4j
@DisplayName("时长卡测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimeCardTests {

    @Test
    @DisplayName("时长卡验证-1")
    @Disabled
    void testTimeCardCalculation() {
        final ArrayList<BatchTestCard> list = new ArrayList<>();

        Date startTime = new Date();
        TimeCard timeCard = new TimeCard("10", "9.98", "0.7", 10, startTime);

        // 单次核销
        list.add(BatchTestCard.builder().card(timeCard.clone())
                .expenseAmount(Arrays.asList("1", "1", "1", "1", "1", "1", "1", "1", "1", "1")).build());
        // 4,4,2
        list.add(BatchTestCard.builder().card(timeCard.clone())
                .expenseAmount(Arrays.asList("4", "4", "2")).build());

        for (BatchTestCard item : list) {
            try {
                log.info("--------------【开始核销时长卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final TimeCard card = (TimeCard) item.getCard();
                item.getExpenseAmount().forEach(e -> {
                    card.setCurrentExpenseCount(Integer.parseInt(e));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, Integer.parseInt(e));
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}",
                            e, card.getEachAmount().multiply(new BigDecimal(e)), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
                });

                log.info("--------------【结束核销时长卡过程】--------------\n");
            } catch (Exception e) {
                log.info(e.getMessage());
            }
        }
        log.info("\n\n\n");

    }
}
