package com.remember5.junit.card;

import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.transfer.GeneralCardTransferCalculate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 转账计算测试类
 *
 * @author wangjiahao
 * @date 2025/9/14
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CountCardTests {

    BatchTestCard card1 = new BatchTestCard(
            new CountCard("9", "8.98", "0.3", 1),
            Arrays.asList("2, 2, 2, 1", "1,1,1,1,1,1,1", "4,2,2,2"));

    @Test
    @DisplayName("【次卡】正常核销场景")
    @Order(1)
    void testCountCardCalculation1() {
        final List<String> expenseAmount = card1.getExpenseAmount();
        card1.getCard().printCardInfo();

        log.info("【次卡】正常核销场景\n");
        log.info("--------------【开始核销次卡过程】--------------");

            expenseAmount.forEach(e-> {
                log.info("验证条件: 【{}】", e);
                final String[] split = e.split(",");
                final CountCard card = ((CountCard) card1.getCard()).clone();
                for (int i = 0; i < split.length; i++) {
                    try {
                        card.setCurrentExpenseCount(Integer.parseInt(split[i]));
                        BigDecimal transferAmount = GeneralCardTransferCalculate.calculate(card, Integer.parseInt(split[i]));
                        log.info("当前核销次数: {}次, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 剩余核销次数: {}",
                                split[i], card.getEachAmount().multiply(new BigDecimal(split[i])), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getRemainingCount());
                    } catch (Exception exception) {
                        log.info(exception.getMessage());
                    }
                }
            });


        log.info("--------------【结束核销次卡过程】--------------\n");
        log.info("\n\n\n");


    }

    @Test
    @DisplayName("【次卡】异常核销场景")
    @Order(2)
    void testCountCardCalculation2() {

    }

}
