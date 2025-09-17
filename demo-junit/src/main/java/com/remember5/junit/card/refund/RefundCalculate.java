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
package com.remember5.junit.card.refund;

import com.remember5.junit.card.category.AmountCard;
import com.remember5.junit.card.category.CountCard;
import com.remember5.junit.card.category.TimeCard;

import java.math.BigDecimal;

/**
 * 退款计算
 *
 * @author wangjiahao
 * @date 2025/9/16 15:44
 */
public class RefundCalculate {


    /**
     * 次卡退款
     *
     * @param countCard 卡
     * @return 预计退款金额
     */
    public static BigDecimal countCard(CountCard countCard) {
        if (countCard.getRemainingCount() <= 0) {
            throw new IllegalArgumentException("订单已使用完成");
        }
        return countCard.getPayAmount().subtract(countCard.getCumulativeTransferAmount());
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
        return timeCard.getPayAmount().subtract(timeCard.getCumulativeTransferAmount());
    }

    /**
     * 金额卡退款
     *
     * @param amountCard 卡
     * @return 预计退款金额
     */
    public static BigDecimal amountCard(AmountCard amountCard) {
        if (amountCard.getCumulativeUsedEquityAmount().compareTo(amountCard.getEquityAmount()) == 0) {
            throw new IllegalArgumentException("订单已使用完成");
        }
        return amountCard.getPayAmount().subtract(amountCard.getCumulativeTransferAmount());
    }
}
