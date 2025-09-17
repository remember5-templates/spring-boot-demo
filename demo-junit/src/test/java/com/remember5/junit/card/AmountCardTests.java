package com.remember5.junit.card;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.transfer.AmountCardTransferCalculate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2025/9/16 16:11
 */
@Slf4j
@DisplayName("金额卡测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AmountCardTests {

    BatchTestCard card1 = new BatchTestCard(
            new AmountCard("10", "9.98", "0.7", "10"),
            Arrays.asList("2,8", "2,2,2,2,2", "4,6", "4,2,2,2", "9.98,0.02"));

    BatchTestCard card2 = new BatchTestCard(
            new AmountCard("10", "9.98", "0.4", "20"),
            Arrays.asList());


    @Test
    @DisplayName("【权益金额=订单金额】正常核销场景")
    @Order(1)
    void test1() {
        final List<String> expenseAmount = card1.getExpenseAmount();
        card1.getCard().printCardInfo();

        log.info("【权益金额=订单金额】正常核销场景\n");
        log.info("--------------【开始核销金额卡过程】--------------");

        expenseAmount.forEach(e -> {
            log.info("验证条件: 【{}】", e);
            final String[] split = e.split(",");
            final AmountCard card = ((AmountCard) card1.getCard()).clone();
            for (int i = 0; i < split.length; i++) {
                try {
                    final BigDecimal currentExpenseAmount = new BigDecimal(split[i]).setScale(2, RoundingMode.DOWN);
                    card.setCurrentExpenseAmount(currentExpenseAmount);
                    BigDecimal transferAmount = AmountCardTransferCalculate.calculate(card);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 累计权益金额: {}",
                            i + 1, currentExpenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getCumulativeUsedEquityAmount());
                } catch (Exception exception) {
                    log.info(exception.getMessage());
                }
            }
        });

        log.info("--------------【结束核销金额卡过程】--------------\n");
        log.info("\n\n\n");

    }

    @Test
    @DisplayName("【权益金额=订单金额】异常核销场景")
    @Order(2)
    void test2() {

    }

    @Test
    @DisplayName("【权益金额>订单金额】正常核销场景")
    @Order(3)
    void test3() {


    }


    @Test
    @DisplayName("【权益金额>订单金额】异常核销场景")
    @Order(4)
    void test4() {

    }

}
