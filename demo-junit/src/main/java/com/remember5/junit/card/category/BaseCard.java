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
     * 卡类型
     */
    private CardCategory cardCategory;

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
     * 开启留底转账
     */
    private Boolean triggerReserverTransfer;

    /**
     * 打印卡片信息的抽象方法，由子类实现具体逻辑
     */
    public abstract void printCardInfo();

}
