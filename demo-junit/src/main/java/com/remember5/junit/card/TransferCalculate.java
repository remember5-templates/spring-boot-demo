/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.junit.card;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.BaseCard;
import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.category.TimeCard;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 卡片划拨计算工具类
 * 优化版本：
 * 1. 消除代码重复
 * 2. 统一计算逻辑
 * 3. 提高代码可读性和维护性
 * 4. 改进错误处理和边界条件检查
 */

/**
 * @author wangjiahao
 * @date 2025/9/14 14:40
 */
public class TransferCalculate {

    /**
     * 统一的卡片计算方法，支持所有继承自BaseCard的卡类型
     * @param card 卡片对象
     * @return 本次划拨金额
     * @throws IllegalArgumentException 如果卡片对象无效
     */
    public static BigDecimal calculate(BaseCard card) {
        validateCard(card);
        return calculateTransfer(card);
    }

    /**
     * 兼容性方法 - 次卡计算
     * @deprecated 建议直接使用 calculate(BaseCard card) 方法
     */
    @Deprecated
    public static BigDecimal countCard(CountCard card) {
        return calculate(card);
    }

    /**
     * 兼容性方法 - 时长卡计算
     * @deprecated 建议直接使用 calculate(BaseCard card) 方法
     */
    @Deprecated
    public static BigDecimal timeCard(TimeCard card) {
        return calculate(card);
    }


    /**
     * 金额卡计算
     * 重构后使用统一的计算逻辑，消除代码重复
     */
    public static BigDecimal amountCard(AmountCard card) {
        return calculate(card);
    }

    /**
     * 验证卡片对象的有效性
     */
    private static void validateCard(BaseCard card) {
        Objects.requireNonNull(card, "卡片对象不能为null");

        if (card.getCurrentReserveAmount() == null || card.getCardReserveAmount() == null ||
            card.getCardAvailableAmount() == null || card.getCumulativeTransferAmount() == null ||
            card.getCurrentCount() == null || card.getTotalCount() == null) {
            throw new IllegalArgumentException("卡片属性不能为null");
        }

        if (card.getCurrentReserveAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("留底资金必须大于0");
        }

        if (!(card instanceof AmountCard) &&
                card.getCurrentCount().compareTo(card.getTotalCount()) >= 0) {
            throw new IllegalStateException("核销次数已达到上限");
        }
    }

    /**
     * 统一的卡片划拨计算逻辑
     * 支持所有类型的BaseCard（次卡、时长卡等）
     */
    private static BigDecimal calculateTransfer(BaseCard card) {
        BigDecimal eachAmount = getEachAmount(card);
        boolean isLastTransfer = card.getCurrentCount() + 1 == card.getTotalCount();

        // 增加核销次数
        card.setCurrentCount(card.getCurrentCount() + 1);

        // 判断当前处于哪个阶段
        if (isInAvailableStage(card)) {
            return calculateAvailableStage(card, eachAmount);
        } else {
            return calculateReserveStage(card, eachAmount, isLastTransfer);
        }
    }

    /**
     * 获取每次划拨金额
     */
    private static BigDecimal getEachAmount(BaseCard card) {
        if (card instanceof AmountCard) {
            return ((AmountCard) card).getCurrentExpenseAmount();
        }
        return card.getEachAmount();
    }

    /**
     * 判断是否在可支用阶段
     */
    private static boolean isInAvailableStage(BaseCard card) {
        return card.getCardReserveAmount().compareTo(card.getCurrentReserveAmount()) == 0;
    }

    /**
     * 计算可支用阶段的划拨金额
     * 可支用阶段：留底资金未动用，优先使用可支用资金
     */
    private static BigDecimal calculateAvailableStage(BaseCard card, BigDecimal eachAmount) {
        BigDecimal newCumulativeAmount = card.getCumulativeTransferAmount().add(eachAmount);

        if (newCumulativeAmount.compareTo(card.getCardAvailableAmount()) <= 0) {
            // 可支用资金充足，直接从可支用资金划拨
            updateCumulativeAmount(card, eachAmount);
            return eachAmount;
        } else {
            // 可支用资金不足，需要动用留底资金
            BigDecimal transferAmount = newCumulativeAmount.subtract(card.getCardAvailableAmount());
            // 如果划拨金额大于卡的所有留底资金
            if(transferAmount.compareTo(card.getCurrentReserveAmount()) >= 0) {
                transferAmount = card.getCardReserveAmount();
                eachAmount = card.getArrivalAmount().subtract(card.getCumulativeTransferAmount());
            }
            card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(transferAmount));
            updateCumulativeAmount(card, eachAmount);
            return transferAmount;
        }
    }

    /**
     * 计算留底资金阶段的划拨金额
     * 留底资金阶段：留底资金已经开始被使用
     */
    private static BigDecimal calculateReserveStage(BaseCard card, BigDecimal eachAmount, boolean isLastTransfer) {
        BigDecimal transferAmount;

        if (isLastTransfer) {
            // 最后一次，划拨所有剩余留底资金
            transferAmount = card.getCurrentReserveAmount();
            card.setCurrentReserveAmount(BigDecimal.ZERO);
        } else if (card.getCurrentReserveAmount().compareTo(eachAmount) >= 0) {
            // 留底资金充足，正常划拨
            transferAmount = eachAmount;
            card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(eachAmount));
        } else {
            // 留底资金不足，划拨所有剩余留底资金
            transferAmount = card.getCurrentReserveAmount();
            card.setCurrentReserveAmount(BigDecimal.ZERO);
        }

        updateCumulativeAmount(card, transferAmount);
        return transferAmount;
    }

    /**
     * 更新累计划拨金额
     */
    private static void updateCumulativeAmount(BaseCard card, BigDecimal transferAmount) {
        card.setCumulativeTransferAmount(card.getCumulativeTransferAmount().add(transferAmount));
    }


}
