package com.remember5.junit.card;

import cn.hutool.core.util.StrUtil;
import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.category.TimeCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * 转账计算测试类
 *
 * @author wangjiahao
 * @date 2025/9/14
 */
@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransferCalculateTest {

    @Test
    @DisplayName("次卡验证-1")
    @Order(1)
    void testCountCardCalculation1() {
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        final CountCard countCard = new CountCard("9", "8.98", "0.3", 7);
        // 1 * 7
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("1", "1", "1", "1", "1", "1", "1")).build());
        // 4,3
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("4", "3")).build());
        // 5,4
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("5", "4")).build());
        // 6,1
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("6", "1")).build());
        // 7
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("7")).build());
        // 1,2,4
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("1", "2", "4")).build());
        // 0
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("0")).build());
        // 10
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("10")).build());

        for (BatchTestCard item : list) {
            try {
                log.info("--------------【开始核销次卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final CountCard card = (CountCard) item.getCard();
                item.getExpenseAmount().forEach(e -> {
                    card.setCurrentExpenseCount(Integer.parseInt(e));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, Integer.parseInt(e));
                    log.info("当前核销次数: {}次, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 剩余核销次数: {}",
                            e, card.getEachAmount().multiply(new BigDecimal(e)), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getRemainingCount());
                });
                log.info("--------------【结束核销次卡过程】--------------\n");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        log.info("\n\n\n");

    }

    @Test
    @DisplayName("次卡验证-2")
    @Order(2)
    void testCountCardCalculation2() {
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        final CountCard countCard = new CountCard("15", "14.97", "0.4", 9);
        // 1 * 9
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("1", "1", "1", "1", "1", "1", "1", "1", "1")).build());
        // 5,4
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("5", "4")).build());
        // 6,3
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("6", "3")).build());
        // 8,1
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("8", "1")).build());
        // 9
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("9")).build());
        // 3,3,2,2
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("3", "3", "2", "2")).build());
        // 0
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("0")).build());
        // 12
        list.add(BatchTestCard.builder().card(countCard.clone())
                .expenseAmount(Arrays.asList("12")).build());

        for (BatchTestCard item : list) {
            try {
                log.info("--------------【开始核销次卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final CountCard card = (CountCard) item.getCard();
                item.getExpenseAmount().forEach(e -> {
                    card.setCurrentExpenseCount(Integer.parseInt(e));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, Integer.parseInt(e));
                    log.info("当前核销次数: {}次, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}, 剩余核销次数: {}",
                            e, card.getEachAmount().multiply(new BigDecimal(e)), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount(), card.getRemainingCount());
                });
                log.info("--------------【结束核销次卡过程】--------------\n");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        log.info("\n\n\n");

    }


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
                continue;
            }
        }
        log.info("\n\n\n");

    }

    @Test
    @DisplayName("金额卡验证-1")
    @Disabled
    void testAmountCardCalculation1() {
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        // 创建金额卡
        AmountCard amountCard = new AmountCard("10", "9.98", "0.7");
        list.add(BatchTestCard.builder().card(amountCard.clone())
                .expenseAmount(Arrays.asList("1", "1", "1", "1", "1", "1", "1", "1", "1", "1")).build());

        list.add(BatchTestCard.builder().card(amountCard.clone())
                .expenseAmount(Arrays.asList("4.31", "9", "4.3", "5.5", "0.1")).build());


        list.add(BatchTestCard.builder().card(amountCard.clone())
                .expenseAmount(Arrays.asList("30")).build());


        for (BatchTestCard item : list) {
            try {
                int index = 1;
                log.info("--------------【开始核销金额卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final AmountCard card = (AmountCard) item.getCard();

                for (String expenseAmount : item.getExpenseAmount()) {
                    card.setCurrentExpenseAmount(new BigDecimal(expenseAmount));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, 1);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}",
                            index, expenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
                    ++index;
                }
            } catch (Exception e) {
                continue;
            }

            log.info("--------------【结束核销金额卡过程】--------------\n");

        }
        log.info("\n\n\n");

    }

    @Test
    @DisplayName("金额卡验证-2")
    @Order(3)
    void testAmountCardCalculation2() {
        final ArrayList<BatchTestCard> list = new ArrayList<>();
        // 创建金额卡
        AmountCard amountCard = new AmountCard("15", "14.97", "0.4");
        list.add(BatchTestCard.builder().card(amountCard.clone())
                .expenseAmount(Arrays.asList("3", "5.97", "4", "2.03")).build());

        list.add(BatchTestCard.builder().card(amountCard.clone())
                .expenseAmount(Arrays.asList("1.67", "8.93", "1.11", "15")).build());


        list.add(BatchTestCard.builder().card(amountCard.clone())
                .expenseAmount(Arrays.asList("1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11", "1.11")).build());


        for (BatchTestCard item : list) {
            try {
                int index = 1;
                log.info("--------------【开始核销金额卡过程】--------------");
                log.info("验证条件: 【{}】", StrUtil.join(",", item.getExpenseAmount()));
                final AmountCard card = (AmountCard) item.getCard();

                for (String expenseAmount : item.getExpenseAmount()) {
                    card.setCurrentExpenseAmount(new BigDecimal(expenseAmount));
                    BigDecimal transferAmount = TransferCalculate.calculate(card, 1);
                    log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}",
                            index, expenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
                    ++index;
                }
            } catch (Exception e) {
                continue;
            }

            log.info("--------------【结束核销金额卡过程】--------------\n");

        }
        log.info("\n\n\n");

    }

}
