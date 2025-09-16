package com.remember5.junit.card;

import cn.hutool.core.util.StrUtil;
import com.remember5.junit.card.category.AmountCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author wangjiahao
 * @date 2025/9/16 16:11
 */
@Slf4j
@DisplayName("金额卡测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AmountCardTests {

    final AmountCard amountCard1 = new AmountCard("10", "9.98", "0.7", "10");
    final AmountCard amountCard2 = new AmountCard("10", "9.98", "0.4", "20");


    @Test
    @DisplayName("【权益金额=订单金额】正常核销场景")
    @Order(1)
    void test1() {
        amountCard1.printCardInfo();
        log.info("【权益金额=订单金额】正常核销场景");
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("10")).build());

        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("2", "8")).build());

        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("2", "2", "2", "2", "2")).build());

        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("4", "6")).build());

        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("4", "2", "2", "2")).build());

        for (BatchTestCard item : list) {
            try {
                int index = 1;
                log.info("--------------【开始核销金额卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final AmountCard card = (AmountCard) item.getCard();

                for (String expenseAmount : item.getExpenseAmount()) {
                    card.setCurrentExpenseAmount(new BigDecimal(expenseAmount));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, 1);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 累计权益金额: {}",
                            index, expenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getCumulativeUsedEquityAmount());
                    ++index;
                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }

            log.info("--------------【结束核销金额卡过程】--------------\n");

        }
        log.info("\n\n\n");

    }

    @Test
    @DisplayName("【权益金额=订单金额】异常核销场景")
    @Order(2)
    void test2() {
        amountCard1.printCardInfo();
        log.info("【权益金额=订单金额】异常核销场景");
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("4", "8")).build());
        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("2", "10")).build());
        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("2", "2", "2", "2", "4")).build());
        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("4", "8")).build());
        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("4", "2", "2", "4")).build());
        list.add(BatchTestCard.builder().card(amountCard1.clone())
                .expenseAmount(Arrays.asList("0")).build());

        for (BatchTestCard item : list) {
            try {
                int index = 1;
                log.info("--------------【开始核销金额卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final AmountCard card = (AmountCard) item.getCard();

                for (String expenseAmount : item.getExpenseAmount()) {
                    card.setCurrentExpenseAmount(new BigDecimal(expenseAmount));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, 1);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 累计权益金额: {}",
                            index, expenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getCumulativeUsedEquityAmount());
                    ++index;
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                continue;
            }
            log.info("--------------【结束核销金额卡过程】--------------\n");

        }
        log.info("\n\n\n");

    }

    @Test
    @DisplayName("【权益金额>订单金额】正常核销场景")
    @Order(3)
    void test3() {
        amountCard2.printCardInfo();
        log.info("【权益金额>订单金额】正常核销场景");
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        // 创建金额卡
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("20")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("6", "14")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("2", "2", "2", "2", "2", "2", "2", "2", "2", "2")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("9", "0.01", "0.01", "5.98", "4.99", "0.01")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("13", "7")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("14", "2", "2", "2")).build());


        for (BatchTestCard item : list) {
            try {
                int index = 1;
                log.info("--------------【开始核销金额卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final AmountCard card = (AmountCard) item.getCard();

                for (String expenseAmount : item.getExpenseAmount()) {
                    card.setCurrentExpenseAmount(new BigDecimal(expenseAmount));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, 1);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 累计权益金额: {}",
                            index, expenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getCumulativeUsedEquityAmount());
                    ++index;
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                continue;
            }
            log.info("--------------【结束核销金额卡过程】--------------\n");

        }
        log.info("\n\n\n");

    }


    @Test
    @DisplayName("【权益金额>订单金额】异常核销场景")
    @Order(4)
    void test4() {
        amountCard2.printCardInfo();
        log.info("【权益金额>订单金额】异常核销场景");
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("21")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("2", "21")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("2", "2", "2", "2", "18")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("13", "10")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("15", "2", "2", "4")).build());
        list.add(BatchTestCard.builder().card(amountCard2.clone())
                .expenseAmount(Arrays.asList("0")).build());


        for (BatchTestCard item : list) {
            try {
                int index = 1;
                log.info("--------------【开始核销金额卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final AmountCard card = (AmountCard) item.getCard();

                for (String expenseAmount : item.getExpenseAmount()) {
                    card.setCurrentExpenseAmount(new BigDecimal(expenseAmount));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, 1);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 累计权益金额: {}",
                            index, expenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getCumulativeUsedEquityAmount());
                    ++index;
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                continue;
            }
            log.info("--------------【结束核销金额卡过程】--------------\n");

        }
        log.info("\n\n\n");

    }

}
