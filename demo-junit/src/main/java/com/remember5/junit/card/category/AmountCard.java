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

    public AmountCard(String orderAmount, String arrivalAmount, String reservePrecent, String equityAmount) {
        // 卡的基本信息
        setCardCategory(CardCategory.AMOUNT);
        setOrderAmount(new BigDecimal(orderAmount));
        setArrivalAmount(new BigDecimal(arrivalAmount));
        setReservePrecent(new BigDecimal(reservePrecent));
        setEquityAmount(new BigDecimal(equityAmount));

        // 计算
        setCardReserveAmount(getOrderAmount().multiply(getReservePrecent()).setScale(2, RoundingMode.DOWN));
        setCardAvailableAmount(getArrivalAmount().subtract(getCardReserveAmount()));
        setCumulativeTransferAmount(BigDecimal.ZERO);
        setCurrentReserveAmount(getCardReserveAmount());
        setCurrentAvailableAmount(getCardAvailableAmount());
        setEachAmount(BigDecimal.ZERO);
        setRemainingCount(1);
        setTotalCount(1);
        setTriggerReserverTransfer(false);
        setCumulativeUsedEquityAmount(BigDecimal.ZERO);
        setTransferRatio(getOrderAmount().divide(getEquityAmount(), 2, RoundingMode.DOWN));

    }

    /**
     * 卡的权益金额
     */
    private BigDecimal equityAmount;

    /**
     * 累计使用权益金额
     */
    private BigDecimal cumulativeUsedEquityAmount;

    /**
     * 划拨计算比例 = 卡的订单金额 / 卡的权益金额
     */
    private BigDecimal transferRatio;

    /**
     * 本次消费金额
     */
    private BigDecimal currentExpenseAmount;

    @Override
    public void printCardInfo() {
        log.info("=====================");
        log.info("金额卡权益金额: {}", getEquityAmount());
        log.info("金额卡订单金额: {} ", getOrderAmount());
        log.info("金额卡划拨计算比例(卡的订单金额 / 卡的权益金额): {}", getTransferRatio());
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
