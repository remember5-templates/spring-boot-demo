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

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 卡的基础属性
 *
 * @author wangjiahao
 * @date 2025/9/14 11:22
 */
@Data
@NoArgsConstructor
public abstract class BaseCard {

    /**
     * 订单金额(元)
     */
    private BigDecimal orderAmount;

    /**
     * 到账金额(元) 留底资金+可支用资金
     */
    private BigDecimal arrivalAmount;

    /**
     * 监管比例(0.3 <= N <= 1)
     */
    private BigDecimal reservePrecent;

    /**
     * 当前留底资金(元)
     */
    private BigDecimal currentReserveAmount;

    /**
     * 当前可支用资金(元)
     */
    private BigDecimal currentAvailableAmount;

    /**
     * 预付卡留底资金(元), 订单金额(orderAmount) * 监管比例(reservePrecent) 保留两位小数，计算方式=舍弃法
     */
    private BigDecimal cardReserveAmount;

    /**
     * 预付卡可支用资金(元), 实际到账金额(arrivalAmount) - 预付卡留底资金(cardReserveAmount)
     */
    private BigDecimal cardAvailableAmount;

    /**
     * 累计划拨金额(元)
     */
    private BigDecimal cumulativeTransferAmount;

    /**
     * 总次数/总天数
     */
    private Integer totalCount;

    /**
     * 每次的划拨金额(元)
     */
    private BigDecimal eachAmount;

    /**
     * 剩余核销次数/天数
     */
    private Integer remainingCount;

    /**
     * 打印卡片信息的抽象方法，由子类实现具体逻辑
     */
    public abstract void printCardInfo();

}
