package com.remember5.junit.card;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.category.TimeCard;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 转账计算测试类
 *
 * @author wangjiahao
 * @date 2025/9/14
 */
class TransferCalculateTest {

    @Test
    void testCountCardCalculation() {
        // 创建次卡
        CountCard card = new CountCard("10", "9.98", 10, "0.7");
        card.calculateCardInfo();
        card.printCardInfo();

        System.out.println("开始核销过程：");
        int[] columnWidths = {10, 10, 10, 19};
        String[] headers = {"当前核销次数", "本次划拨金额", "当前留底资金", "累计划拨金额(含可支用)"};
        List<Object[]> data = new ArrayList<>();

        // 进行多次计算
        for (int i = 0; i < 10; i++) {
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            data.add(new Object[]{i, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount()});
        }

        // 打印表格
        System.out.println(TableFormatter.formatTableRow(headers, columnWidths));
        System.out.println(TableFormatter.generateSeparator(columnWidths));
        for (Object[] row : data) {
            System.out.println(TableFormatter.formatTableRow(row, columnWidths));
        }
    }

    @Test
    void testTimeCardCalculation() {
        // 创建时长卡
        Date startTime = new Date();
        TimeCard card = new TimeCard("10", "9.98", "0.7", 10, startTime);
        card.calculateCardInfo();
        card.printCardInfo();

        System.out.println("开始核销过程：");
        int[] columnWidths = {10, 10, 10, 19};
        String[] headers = {"当前核销次数", "本次划拨金额", "当前留底资金", "累计划拨金额(含可支用)"};
        List<Object[]> data = new ArrayList<>();

        // 进行多次计算
        for (int i = 0; i < 10; i++) {
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            data.add(new Object[]{i, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount()});
        }

        // 打印表格
        System.out.println(TableFormatter.formatTableRow(headers, columnWidths));
        System.out.println(TableFormatter.generateSeparator(columnWidths));
        for (Object[] row : data) {
            System.out.println(TableFormatter.formatTableRow(row, columnWidths));
        }
    }

    @Test
    void testAmountCardCalculation() {
        // 创建金额卡
        AmountCard card = new AmountCard("10", "9.98", "0.7");
        card.calculateCardInfo();
        card.printCardInfo();

        System.out.println("开始核销过程：");
        int[] columnWidths = {10, 10, 10, 19};
        String[] headers = {"当前核销次数", "本次划拨金额", "当前留底资金", "累计划拨金额(含可支用)"};
        List<Object[]> data = new ArrayList<>();

        // 进行多次计算
        for (int i = 0; i < 10; i++) {
            BigDecimal transferAmount = TransferCalculate.calculate(card);
            data.add(new Object[]{i, transferAmount, card.getCurrentReserveAmount(), card.getCumulativeTransferAmount()});
        }

        // 打印表格
        System.out.println(TableFormatter.formatTableRow(headers, columnWidths));
        System.out.println(TableFormatter.generateSeparator(columnWidths));
        for (Object[] row : data) {
            System.out.println(TableFormatter.formatTableRow(row, columnWidths));
        }
    }

}
