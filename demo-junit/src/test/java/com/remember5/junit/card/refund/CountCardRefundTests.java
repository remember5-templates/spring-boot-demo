package com.remember5.junit.card.refund;

import com.remember5.junit.card.BatchTestCard;
import com.remember5.junit.card.calculate.ConsumerRefundCalculate;
import com.remember5.junit.card.calculate.GeneralCardTransferCalculate;
import com.remember5.junit.card.category.CountCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2025/9/19 10:58
 */
@Slf4j
@DisplayName("次卡退款测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CountCardRefundTests {


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
            try {
                for (int i = 0; i < split.length; i++) {
                    card.setCurrentExpenseCount(Integer.parseInt(split[i]));
                    BigDecimal transferAmount = GeneralCardTransferCalculate.calculate(card, Integer.parseInt(split[i]));
                    log.info("当前核销次数: {}次, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 剩余核销次数: {}",
                            split[i], card.getEachAmount().multiply(new BigDecimal(split[i])), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getRemainingCount());
                }
                // 退款
                final BigDecimal refundAmount = ConsumerRefundCalculate.countCard(card);
                log.info("退款金额: {}", refundAmount);
            } catch (Exception exception) {
                log.info(exception.getMessage());
            }
        });

        log.info("--------------【结束核销次卡过程】--------------\n");
        log.info("\n\n\n");
    }

    @Test
    @DisplayName("【次卡】正常退款场景")
    @Order(1)
    void test1() {
        BatchTestCard testCard = new BatchTestCard(
                countCard1,
                Arrays.asList(
                        "5",
                        "3",
                        "2,2,2",
                        "6",
                        "5,1",
                        "1,1,1,1,1,1"
                )
        );
        test("【次卡】正常退款场景", testCard);
    }


    @Test
    @DisplayName("【次卡】异常退款场景")
    @Order(2)
    void test2() {
        BatchTestCard testCard = new BatchTestCard(
                countCard1,
                Arrays.asList(
                        "10",
                        "2,9",
                        "0"
                )
        );
        test("【次卡】异常退款场景", testCard);
    }

    @Test
    @DisplayName("【次卡】正常退款场景")
    @Order(3)
    void test3() {
        BatchTestCard testCard = new BatchTestCard(
                countCard2,
                Arrays.asList(
                        "10",
                        "10",
                        "10,2,2",
                        "14",
                        "19",
                        "14,2,2",
                        "1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1"
                )
        );
        test("【次卡】正常退款场景", testCard);
    }


    @Test
    @DisplayName("【次卡】异常退款场景")
    @Order(4)
    void test4() {
        BatchTestCard testCard = new BatchTestCard(
                countCard2,
                Arrays.asList(
                        "21",
                        "9,13",
                        "0"
                )
        );
        test("【次卡】异常退款场景", testCard);
    }

}
