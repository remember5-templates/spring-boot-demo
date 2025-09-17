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

import java.math.BigDecimal;

/**
 * @author wangjiahao
 * @date 2025/9/17 20:20
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class GeneralCard extends BaseCard {

    /**
     * 本次消费次数
     */
    private Integer currentExpenseCount;

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

    @Override
    public void printCardInfo() {

    }
}
