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

    BatchTestCard right1 = new BatchTestCard(
            new AmountCard("10", "9.98", "0.7", "10"),
            Arrays.asList(
                    "10",
                    "2,8",
                    "2,2,2,2,2",
                    "4,6",
                    "9.98,0.02",
                    "4,2,2,2",
                    "1.98,0.01,0.01,4,2,2",
                    "4,1.98,0.01,2,0.01,2",
                    "4,2,2,1.9,0.1",
                    "4,2,2,1.8,0.1,0.1",
                    "4,2,2,1.8,0.2"
            ));

    BatchTestCard error1 = new BatchTestCard(
            new AmountCard("10", "9.98", "0.7", "10"),
            Arrays.asList(
                    "4,8",
                    "2,10",
                    "2,2,2,2,4",
                    "4,8",
                    "4,2,2,4",
                    "0,10",
                    "6,0,4",
                    "1.8,0.005,0.005,0.1,4,2,2",
                    "4,1.8,0.05,0.05,2,2,0.1",
                    "4,1.9,0.05,2,2,0.05",
                    "4,1.9,0.05,2,2,0.01,0.01"
            ));


    @Test
    @DisplayName("【权益金额=订单金额】正常核销场景")
    @Order(1)
    void test1() {
        final List<String> expenseAmount = right1.getExpenseAmount();
        right1.getCard().printCardInfo();

        log.info("【权益金额=订单金额】正常核销场景\n");
        log.info("--------------【开始核销金额卡过程】--------------");

        expenseAmount.forEach(e -> {
            log.info("验证条件: 【{}】", e);
            final String[] split = e.split(",");
            final AmountCard card = ((AmountCard) right1.getCard()).clone();
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
        final List<String> expenseAmount = error1.getExpenseAmount();
        error1.getCard().printCardInfo();

        log.info("【权益金额=订单金额】正常核销场景\n");
        log.info("--------------【开始核销金额卡过程】--------------");

        expenseAmount.forEach(e -> {
            log.info("验证条件: 【{}】", e);
            final String[] split = e.split(",");
            final AmountCard card = ((AmountCard) error1.getCard()).clone();
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
