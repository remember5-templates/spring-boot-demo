package com.remember5.junit.card.transfer;

import com.remember5.junit.card.category.BaseCard;
import com.remember5.junit.card.category.GeneralCard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.remember5.junit.card.category.BaseCard.MIN_TRANSFER_AMOUNT;

/**
 * 次卡/时长卡的划拨计算
 *
 * @author wangjiahao
 * @date 2025/9/14 14:40
 */
public class GeneralCardTransferCalculate {

    /**
     * 验证卡片对象的有效性
     *
     * @param card         卡
     * @param expenseCount 消费次数
     * @throws IllegalArgumentException 卡存在异常
     */
    private static void validateCard(GeneralCard card, Integer expenseCount) {
        Objects.requireNonNull(card, "卡片对象不能为null");

        if (card.getCardReserveAmount() == null || card.getCardAvailableAmount() == null ||
                card.getCurrentReserveAmount() == null || card.getCumulativeTransferAmount() == null ||
                card.getRemainingCount() == null || card.getTotalCount() == null) {
            throw new IllegalArgumentException("卡片属性不能为null");
        }
        if (BigDecimal.ZERO.compareTo(card.getCurrentReserveAmount()) >= 0) {
            throw new IllegalStateException("留底资金必须大于0");
        }
        if (card.getRemainingCount() - expenseCount < 0) {
            throw new IllegalStateException("本次核销次数不可以大于剩余权益数");
        }
        if (card.getRemainingCount() == 0) {
            throw new IllegalStateException("核销次数已达到上限");
        }

    }


    /**
     * 统一的卡片计算方法，支持所有继承自BaseCard的卡类型
     *
     * @param card    次卡
     * @param expenseCount 消费次数
     * @return 实际划拨金额
     * @throws IllegalArgumentException 卡存在异常
     */
    public static BigDecimal calculate(GeneralCard card, Integer expenseCount) {
        validateCard(card, expenseCount);
        // 卡的剩余核销次数
        int remainingCount = card.getRemainingCount() - expenseCount;
        // 更新剩余核销次数
        card.setRemainingCount(remainingCount);

        BigDecimal planTransferAmount = getPlanTransferAmount(card.getEachAmount(), expenseCount);
        // 新的累计划拨
        BigDecimal newCumulativeAmount = card.getCumulativeTransferAmount().add(planTransferAmount);
        // 最后一次划拨
        boolean isLastTransfer = remainingCount <= 0;
        return getActualTransferAmount(card, isLastTransfer, newCumulativeAmount, planTransferAmount);
    }

    /**
     * 获取划拨金额
     *
     * @param card                卡
     * @param isLastTransfer      是否最后一次核销
     * @param newCumulativeAmount 新的累计划拨
     * @param planTransferAmount  计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal getActualTransferAmount(GeneralCard card, boolean isLastTransfer, BigDecimal newCumulativeAmount, BigDecimal planTransferAmount) {
        // 判断是否为最后一次
        if (isLastTransfer) {
            return doLastTransfer(card);
        }
        // 新的累计划拨资金 <= 卡的初始可支用
        if (newCumulativeAmount.compareTo(card.getCardAvailableAmount()) <= 0) {
            return notTransfer(card, planTransferAmount);
        }
        return realTransfer(card, newCumulativeAmount, planTransferAmount);
    }

    /**
     * 【记账阶段】，不划拨
     *
     * @param card               卡
     * @param planTransferAmount 计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal notTransfer(GeneralCard card, BigDecimal planTransferAmount) {
        updateCumulativeAmount(card, planTransferAmount);
        return BaseCard.ZERO_AMOUNT;
    }

    /**
     * 【留底阶段】可支用资金不足，需要动用留底资金
     *
     * @param card                卡
     * @param newCumulativeAmount 新的累计划拨
     * @param planTransferAmount  计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal realTransfer(GeneralCard card, BigDecimal newCumulativeAmount, BigDecimal planTransferAmount) {
        // 新的累计划拨资金 <= 卡的总可支用
        BigDecimal actualTransferAmount = Boolean.TRUE.equals(card.getTriggerReserverTransfer())
                ? planTransferAmount :
                newCumulativeAmount.subtract(card.getCardAvailableAmount());
        card.setTriggerReserverTransfer(true);
        // 更新累计划拨金额
        updateCumulativeAmount(card, planTransferAmount);
        // 更新当前留底
        card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(actualTransferAmount));
        return actualTransferAmount;
    }


    /**
     * 最后一次划拨,划拨剩余的全部金额
     *
     * @param card 卡
     * @return 实际划拨金额
     */
    private static BigDecimal doLastTransfer(GeneralCard card) {
        // 预计划拨金额 = 卡的实收 - 卡的已划拨
        BigDecimal planTransferAmount = card.getArrivalAmount().subtract(card.getCumulativeTransferAmount());
        // 实际划拨金额
        BigDecimal actualTransferAmount;
        // 预计划拨金额 > 卡初始留底资金
        if (planTransferAmount.compareTo(card.getCardReserveAmount()) >= 0) {
            // 实际划拨金额 = 当前所有的留底
            actualTransferAmount = card.getCardReserveAmount();
            updateCumulativeAmount(card, card.getArrivalAmount().subtract(card.getCumulativeTransferAmount()));
        } else {
            // 预计划拨金额 < 卡初始留底资金
            actualTransferAmount = planTransferAmount;
            updateCumulativeAmount(card, planTransferAmount);
        }
        // 更新留底资金=0
        card.setCurrentReserveAmount(BaseCard.ZERO_AMOUNT);
        return actualTransferAmount;
    }

    /**
     * 获取计划划拨金额
     *
     * @param eachAmount   每次核销的金额
     * @param expenseCount 消费次数
     * @return 预计划拨金额
     */
    private static BigDecimal getPlanTransferAmount(BigDecimal eachAmount, Integer expenseCount) {
        // 预计划拨金额 = 单次划拨金额(eachAmount) * 核销次数(expenseCount)
        BigDecimal planTransferAmount = eachAmount.multiply(new BigDecimal(expenseCount));

        // 预计划拨金额小于0.01时,预计划拨为0
        if (planTransferAmount.compareTo(MIN_TRANSFER_AMOUNT) < 0) {
            return BaseCard.ZERO_AMOUNT;
        }

        if (BigDecimal.ZERO.compareTo(planTransferAmount) == 0) {
            throw new IllegalStateException("预计划拨金额不得为0");
        }
        return planTransferAmount.setScale(2, RoundingMode.DOWN);
    }

    /**
     * 更新累计划拨金额
     */
    private static void updateCumulativeAmount(GeneralCard card, BigDecimal transferAmount) {
        card.setCumulativeTransferAmount(card.getCumulativeTransferAmount().add(transferAmount));
    }

}
