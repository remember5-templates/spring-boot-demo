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
package com.remember5.junit.card.calculate;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.category.TimeCard;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 退款计算
 *
 * @author wangjiahao
 * @date 2025/9/16 15:44
 */
public class RefundCalculate {


    /**
     * 消费者-次卡退款
     * 消费者退款金额 = 消费者支付金额 * 剩余权益数 / 总权益数 (避免两次精度取舍)
     *
     * @param countCard 卡
     * @return 预计退款金额
     */
    public static BigDecimal countCard(CountCard countCard) {
        if (countCard.getRemainingCount() <= 0) {
            throw new IllegalArgumentException("订单已使用完成");
        }
        final BigDecimal remainingCount = new BigDecimal(countCard.getRemainingCount());
        final BigDecimal totalCount = new BigDecimal(countCard.getTotalCount());
        return countCard.getPayAmount().multiply(remainingCount).divide(totalCount,2, RoundingMode.DOWN);
    }

    /**
     * 时长卡退款
     *
     * @param timeCard 卡
     * @return 预计退款金额
     */
    public static BigDecimal timeCard(TimeCard timeCard) {
        if (timeCard.getRemainingCount() <= 0) {
            throw new IllegalArgumentException("订单已使用完成");
        }
        // todo 其他日期的判断
        final BigDecimal refundRatio = new BigDecimal(timeCard.getRemainingCount()).divide(new BigDecimal(timeCard.getTotalCount()));
        return timeCard.getPayAmount().multiply(refundRatio).setScale(2, RoundingMode.DOWN);
    }

    /**
     * 金额卡退款
     *
     * @param amountCard 卡
     * @return 预计退款金额
     */
    public static BigDecimal amountCard(AmountCard amountCard) {
        if (amountCard.getCumulativeUsedEquityAmount().compareTo(amountCard.getEquityAmount()) == 0) {
            throw new IllegalArgumentException("订单已使用完成,无法退款");
        }
        //  支付金额 * 剩余权益比例
        // 支付金额 * ( (总权益 - 累计使用权益)/ 总权益 )
        final BigDecimal equityAmount = amountCard.getEquityAmount();
        final BigDecimal cumulativeUsedEquityAmount = amountCard.getCumulativeUsedEquityAmount();
        final BigDecimal ratio = equityAmount.subtract(cumulativeUsedEquityAmount).divide(equityAmount);
        return amountCard.getPayAmount().multiply(ratio).setScale(2, RoundingMode.DOWN);
    }
}
