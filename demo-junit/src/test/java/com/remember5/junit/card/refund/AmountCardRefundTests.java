package com.remember5.junit.card.refund;

import com.remember5.junit.card.BatchTestCard;
import com.remember5.junit.card.calculate.AmountCardTransferCalculate;
import com.remember5.junit.card.calculate.ConsumerRefundCalculate;
import com.remember5.junit.card.category.AmountCard;
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
@DisplayName("金额卡退款测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AmountCardRefundTests {


    AmountCard amountCard1 = new AmountCard("10", "9.98", "0.7", "10");
    AmountCard amountCard2 = new AmountCard("10", "9.98", "0.4", "20");


    private void test(String scene, BatchTestCard testCard) {
        log.info("\n ===== {} ===== \n", scene);

        final List<String> expenseAmount = testCard.getExpenseAmount();
        testCard.getCard().printCardInfo();

        log.info("--------------【开始核销金额卡过程】--------------");

        expenseAmount.forEach(e -> {
            log.info("\n【验证条件: 【{}】", e);
            final String[] split = e.split(",");
            final AmountCard card = ((AmountCard) testCard.getCard()).clone();
            try {
                for (int i = 0; i < split.length; i++) {
                    final BigDecimal currentExpenseAmount = new BigDecimal(split[i]).setScale(2, RoundingMode.DOWN);
                    card.setCurrentExpenseAmount(currentExpenseAmount);
                    BigDecimal transferAmount = AmountCardTransferCalculate.calculate(card);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 累计权益金额: {}",
                            i + 1, currentExpenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getCumulativeUsedEquityAmount());
                }
                // 退款
                final BigDecimal refundAmount = ConsumerRefundCalculate.amountCard(card);
                log.info("退款金额: {}", refundAmount);

            } catch (Exception exception) {
                log.info(exception.getMessage());
            }

        });

        log.info("--------------【结束核销金额卡过程】--------------\n");
        log.info("\n\n\n");

    }

    @Test
    @DisplayName("【权益金额=订单金额】正常退款场景")
    @Order(1)
    void test1() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard1,
                Arrays.asList(
                        "10",
                        "2",
                        "2,2,2",
                        "4",
                        "9.98",
                        "4,2,2",
                        "1.98,0.01,0.01",
                        "4,1.98,0.01,2,0.01",
                        "4,2,2,1.99",
                        "4,2,2,1.98,0.01"
                ));
        test("【权益金额=订单金额】正常退款场景", testCard);
    }

    @Test
    @DisplayName("【权益金额=订单金额】异常退款场景")
    @Order(2)
    void test2() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard1,
                Arrays.asList(
                        "12",
                        "2,10",
                        "0",
                        "6,0",
                        "1.98,0.005,0.005,0.01",
                        "1.98,0.005,0.005",
                        "4,1.9,0.05,2",
                        "4,1.98,0.01,2,2,0.005"
                ));
        test("【权益金额=订单金额】异常退款场景", testCard);
    }

    @Test
    @DisplayName("【权益金额>订单金额】正常退款场景")
    @Order(3)
    void test3() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard2,
                Arrays.asList(
                        "20",
                        "6",
                        "2,2,2,2,2,2,2",
                        "9,0.01,0.01,5.98,4.99",
                        "9,0.01,0.01,5.98",
                        "14,2,2",
                        "3.98,0.01,0.01",
                        "8,1.98,0.01,5,0.01",
                        "4,5,1.99,0.01",
                        "4,7,1.98,0.01,0.01",
                        "4,7,1.97,0.01,0.01,0.01",
                        "4,7,1.98,0.02"
                ));
        test("【权益金额>订单金额】正常退款场景", testCard);
    }

    @Test
    @DisplayName("【权益金额>订单金额】异常退款场景")
    @Order(4)
    void test4() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard2,
                Arrays.asList(
                        "21",
                        "2,21",
                        "0",
                        "0,10",
                        "10,0",
                        "3.98,0.01,0.01,4",
                        "8,1.98,0.01,5,0.01",
                        "3.98,0.005,0.005,0.01,4",
                        "12,3.98,0.005,0.005,0.01",
                        "14,1.9,0.05,2,0.05",
                        "14,1.98,0.01,2,0.005,0.005"
                ));
        test("【权益金额>订单金额】异常退款场景", testCard);
    }


}
