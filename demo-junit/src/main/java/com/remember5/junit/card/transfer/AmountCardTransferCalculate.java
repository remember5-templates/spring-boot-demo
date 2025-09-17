package com.remember5.junit.card.transfer;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.BaseCard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.remember5.junit.card.category.BaseCard.MIN_TRANSFER_AMOUNT;

/**
 * 金额卡划拨计算工具类
 *
 * @author wangjiahao
 * @date 2025/9/14 14:40
 */
public class AmountCardTransferCalculate {

    /**
     * 验证卡片对象的有效性
     *
     * @param amountCard 卡
     * @throws IllegalArgumentException 卡存在异常
     */
    private static void validateCard(AmountCard amountCard) {
        Objects.requireNonNull(amountCard, "卡片对象不能为null");

        if (amountCard.getCardReserveAmount() == null || amountCard.getCardAvailableAmount() == null
                || amountCard.getCurrentReserveAmount() == null || amountCard.getCumulativeTransferAmount() == null) {
            throw new IllegalArgumentException("卡片属性不能为null");
        }

        // 核销金额不能小于0.01
        if (amountCard.getCurrentExpenseAmount().compareTo(MIN_TRANSFER_AMOUNT) < 0) {
            throw new IllegalStateException("预计划拨金额不得小于0.01");
        }

        // 判断留底资金需要大于0
        if (BigDecimal.ZERO.compareTo(amountCard.getCurrentReserveAmount()) >= 0) {
            throw new IllegalStateException("留底资金必须大于0");
        }
    }

    /**
     * 统一的卡片计算方法，支持所有继承自BaseCard的卡类型
     *
     * @param amountCard 卡
     * @return 实际划拨金额
     * @throws IllegalArgumentException 卡存在异常
     */
    public static BigDecimal calculate(AmountCard amountCard) {
        validateCard(amountCard);
        // 预计划拨金额
        BigDecimal planTransferAmount = getPlanTransferAmount(amountCard);
        // 新的累计划拨金额
        BigDecimal newCumulativeAmount = amountCard.getCumulativeTransferAmount().add(planTransferAmount);
        // 新的累计权益金额
        BigDecimal newCumulativeUsedEquityAmount = amountCard.getCumulativeUsedEquityAmount()
                .add(amountCard.getCurrentExpenseAmount());

        // 【新的累计权益金额】 不得大于 【卡权益金额】
        if (newCumulativeUsedEquityAmount.compareTo(amountCard.getEquityAmount()) > 0) {
            throw new IllegalArgumentException("核销已超出总权益数");
        }

        // 最后一次划拨 【新的累计权益金额】 == 【卡权益金额】
        boolean isLastTransfer = newCumulativeUsedEquityAmount.compareTo(amountCard.getEquityAmount()) == 0;

        // 【不是最后一次核销时】 && 【如果计划划拨金额 < 0.01(最小划拨金额)】
        if (!isLastTransfer && planTransferAmount.compareTo(MIN_TRANSFER_AMOUNT) < 0) {
            // 只做权益记录，不做划拨
            updateCumulativeUsedEquityAmount(amountCard, amountCard.getCurrentExpenseAmount());
            return BaseCard.ZERO_AMOUNT;
        }

        return getActualTransferAmount(amountCard, isLastTransfer, newCumulativeAmount, planTransferAmount);
    }

    /**
     * 获取划拨金额
     *
     * @param amountCard          卡
     * @param isLastTransfer      是否最后一次核销
     * @param newCumulativeAmount 新的累计划拨
     * @param planTransferAmount  计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal getActualTransferAmount(AmountCard amountCard, boolean isLastTransfer, BigDecimal newCumulativeAmount, BigDecimal planTransferAmount) {
        // 判断是否为最后一次
        if (isLastTransfer) {
            return doLastTransfer(amountCard);
        }
        // 新的累计划拨资金 <= 卡的初始可支用
        if (newCumulativeAmount.compareTo(amountCard.getCardAvailableAmount()) <= 0) {
            return notTransfer(amountCard, planTransferAmount);
        }
        return realTransfer(amountCard, newCumulativeAmount, planTransferAmount);
    }

    /**
     * 【记账阶段】不划拨
     *
     * @param card               卡
     * @param planTransferAmount 计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal notTransfer(BaseCard card, BigDecimal planTransferAmount) {
        // 可支用资金充足，不划拨
        updateCumulativeAmount(card, planTransferAmount);
        updateCumulativeUsedEquityAmount(card, ((AmountCard) card).getCurrentExpenseAmount());
        return BaseCard.ZERO_AMOUNT;
    }

    /**
     * 【留底阶段】可支用资金不足，需要动用留底资金
     *
     * @param amountCard          卡
     * @param newCumulativeAmount 新的累计划拨
     * @param planTransferAmount  计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal realTransfer(AmountCard amountCard, BigDecimal newCumulativeAmount, BigDecimal planTransferAmount) {
        // 新的累计划拨资金 <= 卡的总可支用
        BigDecimal actualTransferAmount = Boolean.TRUE.equals(amountCard.getTriggerReserverTransfer())
                ? planTransferAmount :
                newCumulativeAmount.subtract(amountCard.getCardAvailableAmount());
        amountCard.setTriggerReserverTransfer(true);

        // 如果计划划拨金额 < 0.01(最小划拨金额), 只做权益记录，不做划拨
        if (actualTransferAmount.compareTo(MIN_TRANSFER_AMOUNT) < 0) {
            updateCumulativeUsedEquityAmount(amountCard, amountCard.getCurrentExpenseAmount());
            return BaseCard.ZERO_AMOUNT;
        }
        // 更新累计权益
        updateCumulativeUsedEquityAmount(amountCard, amountCard.getCurrentExpenseAmount());
        // 更新累计划拨
        updateCumulativeAmount(amountCard, planTransferAmount);
        // 更新当前留底
        amountCard.setCurrentReserveAmount(amountCard.getCurrentReserveAmount().subtract(actualTransferAmount));
        return actualTransferAmount;
    }


    /**
     * 最后一次划拨,划拨剩余的全部金额
     *
     * @param amountCard 卡
     * @return 实际划拨金额
     */
    private static BigDecimal doLastTransfer(AmountCard amountCard) {
        // 预计划拨金额 = 卡的实收 - 卡的已划拨
        BigDecimal planTransferAmount = amountCard.getArrivalAmount().subtract(amountCard.getCumulativeTransferAmount());
        // 实际划拨金额
        BigDecimal actualTransferAmount;
        // 预计划拨金额 >= 卡初始留底资金
        if (planTransferAmount.compareTo(amountCard.getCardReserveAmount()) >= 0) {
            // 实际划拨金额 = 当前所有的留底
            actualTransferAmount = amountCard.getCardReserveAmount();
            updateCumulativeUsedEquityAmount(amountCard, amountCard.getEquityAmount().subtract(amountCard.getCumulativeUsedEquityAmount()));
            updateCumulativeAmount(amountCard, amountCard.getArrivalAmount().subtract(amountCard.getCumulativeTransferAmount()));
        } else {
            // 预计划拨金额 < 卡初始留底资金
            actualTransferAmount = planTransferAmount;
            updateCumulativeUsedEquityAmount(amountCard, amountCard.getCurrentExpenseAmount());
            updateCumulativeAmount(amountCard, planTransferAmount);
        }
        // 更新留底资金=0
        amountCard.setCurrentReserveAmount(BaseCard.ZERO_AMOUNT);
        return actualTransferAmount;
    }

    /**
     * 获取计划划拨金额
     *
     * @param amountCard 卡
     * @return 预计划拨金额
     */
    private static BigDecimal getPlanTransferAmount(AmountCard amountCard) {
        // 预计划拨金额 = 本次消费金额 * 划拨计算比例
        BigDecimal planTransferAmount = amountCard.getCurrentExpenseAmount()
                .multiply(amountCard.getTransferRatio());

        // 预计划拨金额小于0.01时,预计划拨为0
        if (planTransferAmount.compareTo(MIN_TRANSFER_AMOUNT) < 0) {
            return BaseCard.ZERO_AMOUNT;
        }

        return planTransferAmount.setScale(2, RoundingMode.DOWN);
    }

    /**
     * 更新累计划拨金额
     */
    private static void updateCumulativeAmount(BaseCard card, BigDecimal transferAmount) {
        card.setCumulativeTransferAmount(card.getCumulativeTransferAmount().add(transferAmount));
    }

    /**
     * 更新累计权益金额
     */
    private static void updateCumulativeUsedEquityAmount(BaseCard card, BigDecimal currentExpenseAmount) {
        if (card instanceof AmountCard amountCard) {
            amountCard.setCumulativeUsedEquityAmount(amountCard.getCumulativeUsedEquityAmount().add(currentExpenseAmount));
        }
    }


}
