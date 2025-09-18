package com.remember5.junit.card.transfer;

import com.remember5.junit.card.BatchTestCard;
import com.remember5.junit.card.calculate.GeneralCardTransferCalculate;
import com.remember5.junit.card.category.CountCard;
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

    CountCard countCard1 = new CountCard("9", "8.98", "0.3", 7);
    CountCard countCard2 = new CountCard("10", "9.98", "0.4", 20);

    private void test(String scene, BatchTestCard testCard) {
        log.info("\n ===== {} ===== \n", scene);

        final List<String> expenseAmount = testCard.getExpenseAmount();
        testCard.getCard().printCardInfo();

        log.info("--------------【开始核销次卡过程】--------------");

        expenseAmount.forEach(e-> {
            log.info("\n验证条件: 【{}】", e);
            final String[] split = e.split(",");
            final CountCard card = ((CountCard) testCard.getCard()).clone();
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
    @DisplayName("【次卡】正常核销场景")
    @Order(1)
    void test1() {
        BatchTestCard testCard = new BatchTestCard(
                countCard1,
                Arrays.asList(
                        "7",
                        "3,4",
                        "2,2,2,1",
                        "5,2",
                        "6,1",
                        "5,1,1",
                        "1,1,1,1,1,1,1"
                )
        );
        test("【次卡】正常核销场景", testCard);
    }

    @Test
    @DisplayName("【次卡】异常核销场景")
    @Order(2)
    void test2() {
        BatchTestCard testCard = new BatchTestCard(
                countCard1,
                Arrays.asList(
                        "10",
                        "2,9",
                        "2,2,5",
                        "4,5,1,6,2,2",
                        "4,1,6,1,5,2,1",
                        "0,7"
                )
        );
        test("【次卡】异常核销场景", testCard);
    }

    @Test
    @DisplayName("【次卡】正常核销场景")
    @Order(3)
    void test3() {
        BatchTestCard testCard = new BatchTestCard(
                countCard2,
                Arrays.asList(
                        "20",
                        "10,10",
                        "10,2,2,6",
                        "14,6",
                        "19,1",
                        "14,2,2,2",
                        "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1"
                )
        );
        test("【次卡】正常核销场景", testCard);
    }


    @Test
    @DisplayName("【次卡】异常核销场景")
    @Order(4)
    void test4() {
        BatchTestCard testCard = new BatchTestCard(
                countCard2,
                Arrays.asList(
                        "21",
                        "9,13",
                        "9,4,10,2,9,2,7,3",
                        "16,5,2,4,1,3,1",
                        "10,3,2,7,2,5,3",
                        "0,20"
                )
        );
        test("【次卡】异常核销场景", testCard);
    }

}
