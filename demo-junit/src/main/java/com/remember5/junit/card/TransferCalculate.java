package com.remember5.junit.card;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.BaseCard;
import com.remember5.junit.card.category.CardCategory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
     * @param card         卡
     * @param expenseCount 消费次数
     * @return 实际划拨金额
     * @throws IllegalArgumentException 卡存在异常
     */
    public static BigDecimal calculate(BaseCard card, Integer expenseCount) {
        validateCard(card, expenseCount);
        BigDecimal planTransferAmount = getPlanTransferAmount(card, expenseCount);
        // 新的累计划拨
        BigDecimal newCumulativeAmount = card.getCumulativeTransferAmount().add(planTransferAmount);
        // 最后一次划拨
        boolean isLastTransfer;

        if (card.getCardCategory() == CardCategory.AMOUNT) {
            AmountCard amountCard = (AmountCard) card;
            // 新的累计权益金额
            BigDecimal newCumulativeUsedEquityAmount = amountCard.getCumulativeUsedEquityAmount().add(amountCard.getCurrentExpenseAmount());
            // 【新的累计权益金额】 不得大于 【卡权益金额】
            if (newCumulativeUsedEquityAmount.compareTo(amountCard.getEquityAmount()) > 0) {
                throw new IllegalArgumentException("已超出权益数");
            }
            isLastTransfer =
                    // 新的累计权益金额 >= 权益金额
                    newCumulativeUsedEquityAmount.compareTo(amountCard.getEquityAmount()) == 0 ||
                            //  此次划拨的总金额 > 订单实收金额
                            newCumulativeAmount.compareTo(card.getArrivalAmount()) == 0;
        } else {
            // 卡的剩余核销次数 <= 0
            final int remainingCount = card.getRemainingCount() - expenseCount;
            isLastTransfer = remainingCount <= 0;
            // 更新剩余核销次数
            card.setRemainingCount(remainingCount);
        }
        return getActualTransferAmount(card, isLastTransfer, newCumulativeAmount, planTransferAmount);
    }

    /**
     * 验证卡片对象的有效性
     *
     * @param card         卡
     * @param expenseCount 消费次数
     * @throws IllegalArgumentException 卡存在异常
     */
    private static void validateCard(BaseCard card, Integer expenseCount) {
        Objects.requireNonNull(card, "卡片对象不能为null");

        if (card.getCurrentReserveAmount() == null || card.getCardReserveAmount() == null || card.getCardAvailableAmount() == null || card.getCumulativeTransferAmount() == null || card.getRemainingCount() == null || card.getTotalCount() == null) {
            throw new IllegalArgumentException("卡片属性不能为null");
        }

        if (java.math.BigDecimal.ZERO.compareTo(card.getCurrentReserveAmount()) >= 0) {
            throw new IllegalStateException("留底资金必须大于0");
        }

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
     * 获取划拨金额
     *
     * @param card                卡
     * @param isLastTransfer      是否最后一次核销
     * @param newCumulativeAmount 新的累计划拨
     * @param planTransferAmount  计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal getActualTransferAmount(BaseCard card, boolean isLastTransfer, BigDecimal newCumulativeAmount, BigDecimal planTransferAmount) {
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
     * 记账阶段，不划拨
     *
     * @param card               卡
     * @param planTransferAmount 计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal notTransfer(BaseCard card, BigDecimal planTransferAmount) {
        // 可支用资金充足，不划拨
        updateCumulativeAmount(card, planTransferAmount);
        if (CardCategory.AMOUNT == card.getCardCategory()) {
            updateCumulativeUsedEquityAmount(card, ((AmountCard) card).getCurrentExpenseAmount());
        }
        return BigDecimal.ZERO;
    }

    /**
     * 【留底阶段】可支用资金不足，需要动用留底资金
     *
     * @param card                卡
     * @param newCumulativeAmount 新的累计划拨
     * @param planTransferAmount  计划划拨金额
     * @return 实际划拨金额
     */
    private static BigDecimal realTransfer(BaseCard card, BigDecimal newCumulativeAmount, BigDecimal planTransferAmount) {
        // 新的累计划拨资金 <= 卡的总可支用
        BigDecimal actualTransferAmount = Boolean.TRUE.equals(card.getTriggerReserverTransfer()) ? planTransferAmount : newCumulativeAmount.subtract(card.getCardAvailableAmount());
        card.setTriggerReserverTransfer(true);
        // 如果划拨金额大于卡的所有留底资金
        if (actualTransferAmount.compareTo(card.getCurrentReserveAmount()) >= 0) {
            actualTransferAmount = card.getCardReserveAmount();
            // 开启留底转账
            card.setTriggerReserverTransfer(true);
        }
        card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(actualTransferAmount));
        updateCumulativeAmount(card, planTransferAmount);
        if (CardCategory.AMOUNT == card.getCardCategory()) {
            updateCumulativeUsedEquityAmount(card, ((AmountCard) card).getCurrentExpenseAmount());
        }
        return actualTransferAmount;
    }


    /**
     * 最后一次划拨,划拨剩余的全部金额
     *
     * @param card 卡
     * @return 实际划拨金额
     */
    private static BigDecimal doLastTransfer(BaseCard card) {
        // 预计划拨金额 = 卡的实收 - 卡的已划拨
        BigDecimal planTransferAmount = card.getArrivalAmount().subtract(card.getCumulativeTransferAmount());
        // 实际划拨金额
        BigDecimal actualTransferAmount;
        // 预计划拨金额 > 卡初始留底资金
        if (planTransferAmount.compareTo(card.getCardReserveAmount()) >= 0) {
            // 实际划拨金额 = 当前所有的留底
            actualTransferAmount = card.getCardReserveAmount();
            updateCumulativeAmount(card, card.getArrivalAmount().subtract(card.getCumulativeTransferAmount()));

            if (CardCategory.AMOUNT == card.getCardCategory()) {
                AmountCard amountCard = (AmountCard) card;
                updateCumulativeUsedEquityAmount(card, amountCard.getEquityAmount().subtract(amountCard.getCumulativeUsedEquityAmount()));
            }
        } else {
            // 预计划拨金额 < 卡初始留底资金
            actualTransferAmount = planTransferAmount;
            updateCumulativeAmount(card, planTransferAmount);
            if (CardCategory.AMOUNT == card.getCardCategory()) {
                updateCumulativeUsedEquityAmount(card, ((AmountCard) card).getCurrentExpenseAmount());
            }
        }
        // 更新留底资金=0
        card.setCurrentReserveAmount(java.math.BigDecimal.ZERO);
        return actualTransferAmount;
    }

    /**
     * 获取计划划拨金额
     *
     * @param card         卡
     * @param expenseCount 消费次数
     * @return 预计划拨金额
     */
    private static BigDecimal getPlanTransferAmount(BaseCard card, Integer expenseCount) {
        BigDecimal planTransferAmount;
        if (CardCategory.AMOUNT == card.getCardCategory()) {
            AmountCard amountCard = (AmountCard) card;
            // 权益金额 != 订单金额
            if (amountCard.getOrderAmount().compareTo(amountCard.getEquityAmount()) != 0) {
                // 预计划拨金额 = 本次消费金额 * 划拨计算比例
                planTransferAmount = amountCard.getCurrentExpenseAmount().multiply(amountCard.getTransferRatio(), new MathContext(2, RoundingMode.DOWN));
            } else {
                // 预计划拨金额 = 本次消费金额
                planTransferAmount = amountCard.getCurrentExpenseAmount();
            }
        } else {
            // 预计划拨金额 = 单次划拨金额(eachAmount) * 核销次数(expenseCount)
            planTransferAmount = card.getEachAmount().multiply(new BigDecimal(expenseCount));
        }

        if (java.math.BigDecimal.ZERO.compareTo(planTransferAmount) == 0) {
            throw new IllegalStateException("预计划拨金额不得为0");
        }

        return planTransferAmount;
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
