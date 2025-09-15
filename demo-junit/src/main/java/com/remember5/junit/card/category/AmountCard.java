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
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author wangjiahao
 * @date 2025/9/14 01:04
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AmountCard extends BaseCard implements Serializable, Cloneable{

    public AmountCard(String orderAmount, String arrivalAmount, String reservePrecent) {
        // 卡的基本信息
        setCardCategory(CardCategory.AMOUNT);
        setOrderAmount(new BigDecimal(orderAmount));
        setArrivalAmount(new BigDecimal(arrivalAmount));
        setReservePrecent(new BigDecimal(reservePrecent));

        // 计算
        setCardReserveAmount(getOrderAmount().multiply(getReservePrecent()).setScale(2, RoundingMode.DOWN));
        setCardAvailableAmount(getArrivalAmount().subtract(getCardReserveAmount()));
        setCumulativeTransferAmount(BigDecimal.ZERO);
        setCurrentReserveAmount(getCardReserveAmount());
        setCurrentAvailableAmount(getCardAvailableAmount());
        setRemainingCount(1);
        setTotalCount(1);
        setTriggerReserverTransfer(false);

        printCardInfo();
    }

    /**
     * 本次消费金额
     */
    private BigDecimal currentExpenseAmount;

    @Override
    public void printCardInfo() {
        log.info("=====================");
        log.info("金额卡订单金额: {} ", getOrderAmount());
        log.info("金额卡实际到账金额: {}", getArrivalAmount());
        log.info("金额卡监管比例: {}", getReservePrecent());
        log.info("金额卡留底资金: {} ", getCardReserveAmount());
        log.info("金额卡可支用资金: {} ", getCardAvailableAmount());
        log.info("=====================");
    }

    @Override
    public AmountCard clone() {
        try {
            return (AmountCard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone CountCard", e);
        }
    }

}
