package com.remember5.junit.card;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.category.TimeCard;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 转账计算测试类
 *
 * @author wangjiahao
 * @date 2025/9/14
 */
@Slf4j
class TransferCalculateTest {

    @Test
    void testCountCardCalculation() {
        // 创建次卡
        CountCard card = new CountCard("10", "9.98", 10, "0.7");
        card.printCardInfo();

        log.info("开始核销过程：");
        // 进行多次计算
        for (int i = 1; i <= card.getTotalCount(); i++) {
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}", i, card.getEachAmount(), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
        }
    }

    @Test
    void testTimeCardCalculation() {
        // 创建时长卡
        Date startTime = new Date();
        TimeCard card = new TimeCard("10", "9.98", "0.7", 10, startTime);
        card.printCardInfo();

        log.info("开始核销过程：");
        // 进行多次计算
        for (int i = 1; i <= card.getTotalCount(); i++) {
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}", i, card.getEachAmount(), transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
        }

    }

    public static BigDecimal random0to10(int scale) {
        final Random RAND = new Random();
        // 0–1000 的随机整数 → 除以 100 → 0–10.00
        return BigDecimal.valueOf(RAND.nextInt(1001), scale).setScale(scale, RoundingMode.HALF_UP);
    }

    @Test
    void testAmountCardCalculation() {
        // 创建金额卡
        AmountCard card = new AmountCard("10", "9.98", "0.7");
        card.printCardInfo();

        log.info("开始核销过程：");

        final List<String> list = Arrays.asList("4.31", "2.3", "4.3", "5.5", "0.1");
        // 进行多次计算
        for (int i = 1; i <= 5; i++) {
            final BigDecimal currentExpenseAmount = new BigDecimal(list.get(i - 1));
            card.setCurrentExpenseAmount(currentExpenseAmount);
            BigDecimal transferAmount = TransferCalculate.amountCard(card);
            log.info("当前核销次数: {}, 本次核销金额: {} , 本次划拨金额: {}, 当前留底资金: {} , 累计划拨金额(含可支用): {}", i, currentExpenseAmount, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount());
        }

    }

}
