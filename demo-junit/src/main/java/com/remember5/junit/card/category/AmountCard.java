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
package com.remember5.junit.card.category;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author wangjiahao
 * @date 2025/9/14 01:04
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AmountCard extends BaseCard {

    public AmountCard(String orderAmount, String arrivalAmount, String reservePrecent) {
        setOrderAmount(new BigDecimal(orderAmount));
        setArrivalAmount(new BigDecimal(arrivalAmount));
        setReservePrecent(new BigDecimal(reservePrecent));
    }

    /**
     * 本次划拨金额
     */
    private BigDecimal currentTransferAmount;

    @Override
    public void calculateCardInfo() {
        setCardReserveAmount(getOrderAmount().multiply(getReservePrecent()).setScale(2, RoundingMode.DOWN));
        setCardAvailableAmount(getArrivalAmount().subtract(getCardReserveAmount()));
        setCumulativeTransferAmount(BigDecimal.ZERO);
        setCurrentReserveAmount(getCardReserveAmount());
        setCurrentAvailableAmount(getCardAvailableAmount());
        setCurrentCount(0);
    }

    @Override
    public void printCardInfo() {
        System.err.println("=====================");
        System.err.println("金额卡订单金额: " + getOrderAmount());
        System.err.println("金额卡实际到账金额:" + getArrivalAmount());
        System.err.println("金额卡监管比例:" + getReservePrecent());
        System.err.println("金额卡留底资金: " + getCardReserveAmount());
        System.err.println("金额卡可支用资金: " + getCardAvailableAmount());
        System.err.println("=====================");
    }


}
