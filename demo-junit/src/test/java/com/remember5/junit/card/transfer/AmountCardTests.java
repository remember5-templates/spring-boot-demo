package com.remember5.junit.card.transfer;

import com.remember5.junit.card.BatchTestCard;
import com.remember5.junit.card.calculate.AmountCardTransferCalculate;
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
@DisplayName("金额卡核销测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AmountCardTests {


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
    @DisplayName("【权益金额=订单金额】正常核销场景")
    @Order(1)
    void test1() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard1,
                Arrays.asList(
                        "10",
                        "2,8",
                        "2,2,2,2,2",
                        "4,6",
                        "9.98,0.02",
                        "4,2,2,2",
                        "1.98,0.01,0.01,4,2,2",
                        "4,1.98,0.01,2,0.01,2",
                        "4,2,2,1.99,0.01",
                        "4,2,2,1.98,0.01,0.01",
                        "4,2,2,1.98,0.02"
                )
        );
        test("【权益金额=订单金额】正常核销场景", testCard);
    }

    @Test
    @DisplayName("【权益金额=订单金额】异常核销场景")
    @Order(2)
    void test2() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard1,
                Arrays.asList(
                        "4,8",
                        "2,10",
                        "2,2,2,2,4",
                        "4,8",
                        "4,2,2,4",
                        "0,10",
                        "6,0,4",
                        "1.98,0.005,0.005,0.01,4,2,2",
                        "4,1.98,0.005,0.005,2,2,0.01",
                        "4,1.9,0.05,2,2,0.05",
                        "4,1.98,0.01,2,2,0.005,0.005"
                ));
        test("【权益金额=订单金额】异常核销场景",testCard);
    }

    @Test
    @DisplayName("【权益金额>订单金额】正常核销场景")
    @Order(3)
    void test3() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard2,
                Arrays.asList(
                        "20",
                        "6,14",
                        "2,2,2,2,2,2,2,2,2,2",
                        "9,0.01,0.01,5.98,4.99,0.01",
                        "13,7",
                        "14,2,2,2",
                        "3.98,0.01,0.01,4,6,6",
                        "8,1.98,0.01,5,0.01,5",
                        "4,5,5,4,1.99,0.01",
                        "4,7,7,1.98,0.01,0.01",
                        "4,7,7,1.97,0.01,0.01,0.01",
                        "4,7,7,1.98,0.02"
                ));
        test("【权益金额>订单金额】正常核销场景",testCard);
    }


    @Test
    @DisplayName("【权益金额>订单金额】异常核销场景")
    @Order(4)
    void test4() {
        BatchTestCard testCard = new BatchTestCard(
                amountCard2,
                Arrays.asList(
                        "21",
                        "2,21",
                        "2,2,2,2,18",
                        "13,10",
                        "15,2,2,4",
                        "0,10",
                        "10,0",
                        "3.98,0.01,0.01,4,6,6",
                        "8,1.98,0.01,5,0.01,5",
                        "3.98,0.005,0.005,0.01,4,6,6",
                        "12,3.98,0.005,0.005,0.01,4",
                        "14,1.9,0.05,2,2,0.05",
                        "14,1.98,0.01,2,2,0.005,0.005"
                ));
        test("【权益金额>订单金额】异常核销场景",testCard);
    }

    @Test
    @DisplayName("其他场景5")
    @Order(5)
    void test5() {
        BatchTestCard testCard = new BatchTestCard(
                new AmountCard("0.1","0.1","0.3","0.1"),
                Arrays.asList(
                        "0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01",
                        "0.09,0.01",
                        "0.01,0.09"
                ));
        test("【其他场景5】",testCard);
    }

    @Test
    @DisplayName("其他场景6")
    @Order(6)
    void test6() {
        BatchTestCard testCard = new BatchTestCard(
                new AmountCard("1000", "998", "0.7", "1000"),
                Arrays.asList(
                        "500,498,2",
                        "500,499,1",
                        "500,499,0.9,0.01,0.05,0.04",
                        "500,0.01,499,0.05,0.9,0.04"
                ));
        test("其他场景6", testCard);
    }

    @Test
    @DisplayName("其他场景7")
    @Order(7)
    void test7() {
        BatchTestCard testCard = new BatchTestCard(
                new AmountCard("1000","998","0.7","2000"),
                Arrays.asList(
                        "1500,498,2",
                        "1500,499,1",
                        "1500,499,0.9,0.01,0.05,0.04",
                        "1500,0.01,499,0.05,0.9,0.04"
                ));
        test("其他场景7", testCard);
    }

    @Test
    @DisplayName("其他场景8")
    @Order(8)
    void test8() {
        BatchTestCard testCard = new BatchTestCard(
                new AmountCard("10","9.98","0.4","20"),
                Arrays.asList(
                        "13,10,2,6,3,3,2",
                        "14,1.98,0.01,2,2,0.005,0.005"
                ));
        test("其他场景8", testCard);
    }

    @Test
    @DisplayName("其他场景9")
    @Order(9)
    void test9() {
        BatchTestCard testCard = new BatchTestCard(
                new AmountCard("0.1","0.1","0.3","0.2"),
                Arrays.asList(
                        "0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01"
                ));
        test("其他场景9", testCard);
    }

    @Test
    @DisplayName("其他场景10")
    @Order(10)
    void test10() {
        BatchTestCard testCard = new BatchTestCard(
                new AmountCard("10000","9980","0.7","10000"),
                Arrays.asList(
                        "5000,4980,20",
                        "5000,4998,2"
                ));
        test("其他场景10", testCard);
    }


}
