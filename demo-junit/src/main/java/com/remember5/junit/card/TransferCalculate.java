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

/**
 * @author wangjiahao
 * @date 2025/9/14 14:40
 */
public class TransferCalculate {

    /**
     * 统一的卡片计算方法，支持所有继承自BaseCard的卡类型
     * @param card 卡片对象（次卡或时长卡）
     * @return 本次划拨金额
     */
    public static BigDecimal calculate(BaseCard card) {
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
     * 计算次卡
     * <p>
     * 次卡的计算规则和业务逻辑是: 平台属于监管平台
     * 商家收了订单金额(OrderAmount),扣完手续费然后实际到账(ArrivalAmount),
     * 平台会把AA分为两个类目，
     * 1.留底资金(ReserveAmount) 属于平台暂时冻结的金额, 留底资金 = 订单金额 * 监管比例 (保留两位小数向下取)
     * 2.可支用资金(AvailableAmount) 属于商家可自由支配的金额,可支用资金 = 到账金额 - 留底资金
     * 每次核销金额 = 订单金额(OrderAmount) / 总次数(totalCount) (保留两位小数向下取)
     * <p>
     * <p>
     * 如: 一个商品订单金额OrderAmount=10元,ArrivalAmount=9.98,reservePrecent=0.7,totalCount=10
     * 那么 卡的留底资金 = 10 * 0.7 = 7元, 可支用资金 = 9.98 - 7 = 2.98元
     * 每次核销金额为 10/10 = 1元
     *
     * @param card 次卡信息
     */
    private static BigDecimal calculateTransfer(BaseCard card) {

        if(card.getCurrentReserveAmount().compareTo(BigDecimal.ZERO) == 0) {
            System.err.println("[核销错误] 留底资金为0");
            return BigDecimal.ZERO;
        }
        // 如果是最后一次，就代表不能核销了
        if (card.getCurrentCount().compareTo(card.getTotalCount()) == 0) {
            System.err.println("[核销错误] 核销次数已经超过权益次数");
            return BigDecimal.ZERO;
        }

        // 增加核销次数
        card.setCurrentCount(card.getCurrentCount() + 1);

        BigDecimal transferAmount = BigDecimal.ZERO;
        BigDecimal eachAmount = card.getEachAmount();

        // 判断当前处于哪个阶段
        if (card.getCardReserveAmount().compareTo(card.getCurrentReserveAmount()) == 0) {
            // 可支用阶段：留底资金未动用 (卡的留底资金 = 当前的留底资金)

            // 累计划拨金额 (加上当前这次划拨)
            BigDecimal newCumulativeAmount = card.getCumulativeTransferAmount().add(eachAmount);

            if (newCumulativeAmount.compareTo(card.getCardAvailableAmount()) <= 0) {
                // 累计划拨 <= 可支用资金，直接从可支用资金划拨(不动留底)
                transferAmount = eachAmount;
                // 更新累计划拨金额
                card.setCumulativeTransferAmount(
                        card.getCumulativeTransferAmount().add(transferAmount)
                );
            } else {
                // 累计划拨 > 可支用资金，需要动用留底资金,(累计划拨-可支用=需要划拨的钱)
                transferAmount = newCumulativeAmount.subtract(card.getCardAvailableAmount());

                // 更新留底资金
                card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(transferAmount));
                // 更新累计划拨金额
                card.setCumulativeTransferAmount(
                        card.getCumulativeTransferAmount().add(eachAmount)
                );
            }

            return transferAmount;

        }

        // 动用留底阶段：留底资金已经开始被使用

        // 判断是否为最后一次
        if (card.getCurrentCount().equals(card.getTotalCount())) {
            // 划拨剩余所有的留底
            transferAmount = card.getCurrentReserveAmount();
            // 更新累计划拨金额
            card.setCumulativeTransferAmount(
                    card.getCumulativeTransferAmount().add(transferAmount));
            // 更新当前留底资金
            card.setCurrentReserveAmount(
                    card.getCurrentReserveAmount().subtract(transferAmount)
            );
            return transferAmount;
        }

        if (card.getCurrentReserveAmount().compareTo(eachAmount) >= 0) {
            // 留底资金充足，正常划拨
            transferAmount = eachAmount;
            card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(eachAmount));
        } else {
            // 留底资金不足（最后一次），划拨所有剩余留底资金
            transferAmount = card.getCurrentReserveAmount();
            card.setCurrentReserveAmount(BigDecimal.ZERO);
        }


        // 更新累计划拨金额
        card.setCumulativeTransferAmount(card.getCumulativeTransferAmount().add(transferAmount));

        return transferAmount;
    }

    /**
     * 金额卡计算
     */
    public static BigDecimal amountCard(AmountCard card) {
        if(card.getCurrentReserveAmount().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // 增加核销次数
        card.setCurrentCount(card.getCurrentCount() + 1);
        // 本次划拨金额
        BigDecimal transferAmount = card.getCurrentTransferAmount();

        // 判断当前处于哪个阶段
        if (card.getCardReserveAmount().compareTo(card.getCurrentReserveAmount()) == 0) {
            // 可支用阶段：留底资金未动用 (卡的留底资金 = 当前的留底资金)

            // 累计划拨金额 (加上当前这次划拨)
            BigDecimal newCumulativeAmount = card.getCumulativeTransferAmount().add(transferAmount);

            if (newCumulativeAmount.compareTo(card.getCardAvailableAmount()) <= 0) {
                // 累计划拨 <= 可支用资金，直接从可支用资金划拨(不动留底)
                // 更新累计划拨金额
                card.setCumulativeTransferAmount(
                        card.getCumulativeTransferAmount().add(transferAmount)
                );
            } else {
                // 累计划拨 > 可支用资金，需要动用留底资金,(累计划拨-可支用=需要划拨的钱)
                transferAmount = newCumulativeAmount.subtract(card.getCardAvailableAmount());
                // 更新留底资金
                card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(transferAmount));
                // 更新累计划拨金额
                card.setCumulativeTransferAmount(
                        card.getCumulativeTransferAmount().add(card.getCurrentTransferAmount())
                );
            }
            return transferAmount;
        }

        // 动用留底阶段：留底资金已经开始被使用

        // 如果当前留底 < 当次划拨
        if (card.getCurrentReserveAmount().compareTo(card.getCurrentTransferAmount()) < 0) {
            // 划拨剩余所有的留底
            transferAmount = card.getCurrentReserveAmount();
            // 更新累计划拨金额
            card.setCumulativeTransferAmount(
                    card.getCumulativeTransferAmount().add(transferAmount)
            );
            // 更新当前留底资金
            card.setCurrentReserveAmount(
                    card.getCurrentReserveAmount().subtract(transferAmount)
            );
            return transferAmount;
        }

        if (card.getCurrentReserveAmount().compareTo(transferAmount) >= 0) {
            // 留底资金充足，正常划拨
            card.setCurrentReserveAmount(card.getCurrentReserveAmount().subtract(transferAmount));
        } else {
            // 留底资金不足（最后一次），划拨所有剩余留底资金
            transferAmount = card.getCurrentReserveAmount();
            card.setCurrentReserveAmount(BigDecimal.ZERO);
        }
        // 更新累计划拨金额
        card.setCumulativeTransferAmount(card.getCumulativeTransferAmount().add(transferAmount));

        return transferAmount;
    }


}
