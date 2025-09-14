package com.remember5.junit.card.category;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 次卡
 *
 * @author wangjiahao
 * @date 2025/9/13 22:13
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CountCard extends BaseCard {


    public CountCard(String orderAmount, String arrivalAmount, Integer totalCount, String reservePrecent) {
        setOrderAmount(new BigDecimal(orderAmount));
        setArrivalAmount(new BigDecimal(arrivalAmount));
        setReservePrecent(new BigDecimal(reservePrecent));
        this.totalCount = totalCount;
    }

    /**
     * 总次数
     */
    private Integer totalCount;

    /**
     * 每次的划拨金额
     */
    private BigDecimal eachAmount;

    /**
     * 剩余次数
     */
    @Deprecated
    private Integer remainingCount;

    /**
     * 当前核销次数
     */
    private Integer currentCount;

    public void calculateCardInfo() {
        setCardReserveAmount(getOrderAmount().multiply(getReservePrecent()).setScale(2, RoundingMode.DOWN));
        setCardAvailableAmount(getArrivalAmount().subtract(getCardReserveAmount()));
        setEachAmount(getOrderAmount().divide(new BigDecimal(totalCount), 2, RoundingMode.DOWN));
        setCumulativeTransferAmount(BigDecimal.ZERO);
        setCurrentReserveAmount(getCardReserveAmount());
        setCurrentAvailableAmount(getCardAvailableAmount());
        setCurrentCount(0);
    }

    public void printCardInfo() {
        System.err.println("=====================");
        System.err.println("次卡订单金额: " + getOrderAmount());
        System.err.println("次卡实际到账金额:" + getArrivalAmount());
        System.err.println("次卡监管比例:" + getReservePrecent());
        System.err.println("次卡留底资金: " + getCardReserveAmount());
        System.err.println("次卡可支用资金: " + getCardAvailableAmount());
        System.err.println("次卡总次数:" + totalCount);
        System.err.println("每次核销金额: " + eachAmount);
        System.err.println("=====================");
    }

}
