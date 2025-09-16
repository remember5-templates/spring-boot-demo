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
import com.remember5.junit.card.category.CardCategory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * 退款计算
 *
 * @author wangjiahao
 * @date 2025/9/16 15:44
 */
public class RefundCalculate {

    /**
     * 退款计算
     *
     * @param card 卡
     * @return 退款金额
     */
    public static BigDecimal calculate(BaseCard card) {
        validateCard(card);
        // 订单金额*((总权益-已使用权益)/总权益数)
        // 订单金额*(未核销权益数/总权益数)
        BigDecimal unusedRatio;

        if (CardCategory.AMOUNT ==  card.getCardCategory()) {
            AmountCard amountCard = (AmountCard) card;
            BigDecimal unusedAmount = amountCard.getEquityAmount().subtract(amountCard.getCumulativeUsedEquityAmount());
            unusedRatio = unusedAmount.divide(amountCard.getEquityAmount(), 2, RoundingMode.DOWN);
        } else {
            unusedRatio = new BigDecimal(card.getRemainingCount()).divide(new BigDecimal(card.getTotalCount()), 2, RoundingMode.DOWN);
        }
        return card.getOrderAmount().multiply(unusedRatio, new MathContext(2, RoundingMode.DOWN));
    }

    private static void validateCard(BaseCard card) {
        if (card.getRemainingCount() <= 0) {
            throw new IllegalArgumentException("订单已使用完成");
        }

        if (CardCategory.AMOUNT == card.getCardCategory()) {
            AmountCard amountCard = (AmountCard) card;

            // 累计划拨 = 订单实收金额
            if (amountCard.getCumulativeTransferAmount().compareTo(amountCard.getArrivalAmount()) == 0 ||
                    BigDecimal.ZERO.compareTo(amountCard.getCurrentReserveAmount()) == 0) {
                throw new IllegalArgumentException("订单已使用完成");
            }
        }

        if (card.getRemainingCount() <= 0) {
            throw new IllegalArgumentException("订单已使用完成");
        }

    }
}
