package com.remember5.junit.card;

import com.remember5.junit.card.category.*;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 卡片划拨计算工具类
 *
 * @author wangjiahao
 * @date 2025/9/14 14:40
 */
public class TransferCalculate {

    /**
     * 统一的卡片计算方法，支持所有继承自BaseCard的卡类型
     *
     * @param card 卡片对象
     * @return 实际划拨金额
     * @throws IllegalArgumentException 如果卡片对象无效
     */
    public static BigDecimal calculate(BaseCard card, Integer expenseCount) {
        validateCard(card, expenseCount);
        return calculateTransfer(card, expenseCount);
    }

    /**
     * 金额卡计算
     */
    public static BigDecimal amountCard(AmountCard card) {
        validateCard(card, 1);
        // 预计划拨金额 = 单次划拨金额(eachAmount) * 核销次数(expenseCount)
        BigDecimal planTransferAmount = getPlanTransferAmount(card, 1);
        // 新的累计划拨
        BigDecimal newCumulativeAmount = card.getCumulativeTransferAmount().add(planTransferAmount);

        // 预计划拨为0的话,直接返回 异常,
        if (BigDecimal.ZERO.compareTo(planTransferAmount) >= 0) {
            throw new IllegalStateException("预计划拨金额需要大于0");
        }

        // 判断是否为最后一次 预计划拨金额 > 当前留底
        boolean isLastTransfer = planTransferAmount.compareTo(card.getCurrentReserveAmount()) >= 0;

        return getActualTransferAmount(card, isLastTransfer, newCumulativeAmount, planTransferAmount);
    }

    /**
     * 验证卡片对象的有效性
     */
    private static void validateCard(BaseCard card, Integer expenseCount) {
        Objects.requireNonNull(card, "卡片对象不能为null");

        if (card.getCurrentReserveAmount() == null || card.getCardReserveAmount() == null || card.getCardAvailableAmount() == null || card.getCumulativeTransferAmount() == null || card.getRemainingCount() == null || card.getTotalCount() == null) {
            throw new IllegalArgumentException("卡片属性不能为null");
        }

        if (BigDecimal.ZERO.compareTo(card.getCurrentReserveAmount()) >= 0) {
            throw new IllegalStateException("留底资金必须大于0");
        }

        // 非金额卡判断
        if (card.getCardCategory() != CardCategory.AMOUNT) {
            if (card.getRemainingCount() - expenseCount < 0) {
                throw new IllegalStateException("本次核销次数不可以大于剩余权益数");
            }

            if (card.getRemainingCount() == 0) {
                throw new IllegalStateException("核销次数已达到上限");
            }

        }

    }

    /**
     * 统一的卡片划拨计算逻辑
     * 支持所有类型的BaseCard（次卡、时长卡等）
     *
     * @param card 卡对象
     * @return 实际划拨金额
     */
    private static BigDecimal calculateTransfer(BaseCard card, Integer expenseCount) {
        // 预计划拨金额 = 单次划拨金额(eachAmount) * 核销次数(expenseCount)
        BigDecimal planTransferAmount = getPlanTransferAmount(card, expenseCount);
        // 新的累计划拨
        BigDecimal newCumulativeAmount = card.getCumulativeTransferAmount().add(planTransferAmount);

        // 预计划拨为0的话,直接返回 异常,
        if (BigDecimal.ZERO.compareTo(planTransferAmount) >= 0) {
            throw new IllegalStateException("预计划拨金额需要大于0");
        }

        // 判断是否为最后一次
        boolean isLastTransfer = card.getRemainingCount() - expenseCount <= 0;
        // 更新剩余核销次数
        card.setRemainingCount(card.getRemainingCount() - expenseCount);

        return getActualTransferAmount(card, isLastTransfer, newCumulativeAmount, planTransferAmount);

    }

    private static BigDecimal getActualTransferAmount(BaseCard card, boolean isLastTransfer, BigDecimal newCumulativeAmount, BigDecimal planTransferAmount) {
        if (isLastTransfer) {
            // 实际划拨金额 = 卡的实收 - 卡的已划拨
            BigDecimal actualTransferAmount = card.getArrivalAmount().subtract(card.getCumulativeTransferAmount());

            if(actualTransferAmount.compareTo(card.getCardReserveAmount()) >= 0) {
                actualTransferAmount = card.getCardReserveAmount();
            }
            // 更新留底资金
            card.setCurrentReserveAmount(BigDecimal.ZERO);
            updateCumulativeAmount(card, actualTransferAmount);
            return actualTransferAmount;
        }

        // 新的累计划拨资金 <= 卡的初始可支用
        if (newCumulativeAmount.compareTo(card.getCardAvailableAmount()) <= 0) {
            // 可支用资金充足，不划拨
            updateCumulativeAmount(card, planTransferAmount);
            return BigDecimal.ZERO;
        } else {
            // 可支用资金不足，需要动用留底资金
            BigDecimal actualTransferAmount = card.getTriggerReserverTransfer() ? planTransferAmount : newCumulativeAmount.subtract(card.getCardAvailableAmount());
            card.setTriggerReserverTransfer(true);
            // 如果划拨金额大于卡的所有留底资金
            if (actualTransferAmount.compareTo(card.getCurrentReserveAmount()) >= 0) {
                actualTransferAmount = card.getCardReserveAmount();
                // 开启留底转账
                card.setTriggerReserverTransfer(true);
            }
            card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(actualTransferAmount));
            updateCumulativeAmount(card, planTransferAmount);
            return actualTransferAmount;
        }
    }

    /**
     * 获取计划划拨金额
     */
    private static BigDecimal getPlanTransferAmount(BaseCard card, Integer expenseCount) {
        if (card instanceof AmountCard amountCard) {
            return amountCard.getCurrentExpenseAmount();
        }
        return card.getEachAmount().multiply(new BigDecimal(expenseCount));
    }

    /**
     * 更新累计划拨金额
     */
    private static void updateCumulativeAmount(BaseCard card, BigDecimal transferAmount) {
        card.setCumulativeTransferAmount(card.getCumulativeTransferAmount().add(transferAmount));
    }

}
