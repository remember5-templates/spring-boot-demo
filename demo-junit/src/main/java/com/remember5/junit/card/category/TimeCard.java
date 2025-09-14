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
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 时长卡
 *
 * @author wangjiahao
 * @date 2025/9/14 11:16
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TimeCard extends BaseCard {

    public TimeCard(String orderAmount, String arrivalAmount, String reservePrecent, Integer totalCount, Date startTime) {
        setOrderAmount(new BigDecimal(orderAmount));
        setArrivalAmount(new BigDecimal(arrivalAmount));
        setReservePrecent(new BigDecimal(reservePrecent));
        setTotalCount(totalCount);
        setStartTime(startTime);
        setEndTime(DateUtils.addDays(startTime, totalCount));
    }

    /**
     * 时长卡生效期
     */
    private Date startTime;

    /**
     * 时长卡失效期
     */
    private Date endTime;

    @Override
    public void calculateCardInfo() {
        setCardReserveAmount(getOrderAmount().multiply(getReservePrecent()).setScale(2, RoundingMode.DOWN));
        setCardAvailableAmount(getArrivalAmount().subtract(getCardReserveAmount()));
        setEachAmount(getOrderAmount().divide(new BigDecimal(getTotalCount()), 2, RoundingMode.DOWN));
        setCumulativeTransferAmount(BigDecimal.ZERO);
        setCurrentReserveAmount(getCardReserveAmount());
        setCurrentAvailableAmount(getCardAvailableAmount());
        setCurrentCount(0);
    }

    @Override
    public void printCardInfo() {
        System.err.println("=====================");
        System.err.println("时长卡订单金额: " + getOrderAmount());
        System.err.println("时长卡实际到账金额:" + getArrivalAmount());
        System.err.println("时长卡监管比例:" + getReservePrecent());
        System.err.println("时长卡留底资金: " + getCardReserveAmount());
        System.err.println("时长卡可支用资金: " + getCardAvailableAmount());
        System.err.println("时长卡总时长:" + getTotalCount());
        System.err.println("每次核销金额: " + getEachAmount());
        System.err.println("时长卡生效期:" + startTime);
        System.err.println("时长卡失效期:" + endTime);
        System.err.println("=====================");
    }


}
