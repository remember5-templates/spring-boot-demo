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
package com.remember5.junit.card.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author wangjiahao
 * @date 2025/9/15 10:10
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {

    /**
     * 当前核销次数
     */
    private Integer currentCount;

    /**
     * 本次核销金额
     */
    private BigDecimal currentExpense;

    /**
     * 本地划拨金额
     */
    private BigDecimal currentTransferAmount;

    /**
     * 当前留底资金
     */
    private BigDecimal currentReserveAmount;

    /**
     * 累计划拨金额
     */
    private BigDecimal cumulativeTransferAmount;

}
