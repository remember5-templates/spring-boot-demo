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
 * 金额卡
 *
 * @author wangjiahao
 * @date 2025/9/14 01:04
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class AmountCard extends BaseCard implements Serializable, Cloneable {


    /**
     * 卡的权益金额
     */
    private BigDecimal equityAmount;

    /**
     * 本次消费金额
     */
    private BigDecimal currentExpenseAmount;

    /**
     * 累计使用权益金额
     */
    private BigDecimal cumulativeUsedEquityAmount;

    /**
     * 划拨计算比例 = 卡的订单金额 / 卡的权益金额
     */
    private BigDecimal transferRatio;

    public AmountCard(String payAmount, String arrivalAmount, String reservePrecent, String equityAmount) {
        // 卡的基本信息
        setPayAmount(new BigDecimal(payAmount).setScale(2, RoundingMode.DOWN));
        setArrivalAmount(new BigDecimal(arrivalAmount).setScale(2, RoundingMode.DOWN));
        setReservePrecent(new BigDecimal(reservePrecent).setScale(2, RoundingMode.DOWN));
        setEquityAmount(new BigDecimal(equityAmount).setScale(2, RoundingMode.DOWN));

        // 计算留底和可支用
        setCardReserveAmount(getArrivalAmount().multiply(getReservePrecent()).setScale(2, RoundingMode.UP));
        setCardAvailableAmount(getArrivalAmount().subtract(getCardReserveAmount()));
        // 计算划拨系数
        setTransferRatio(getArrivalAmount().divide(getEquityAmount(), 10, RoundingMode.DOWN));

        // 当前情况
        setCurrentReserveAmount(getCardReserveAmount());
        setCurrentAvailableAmount(getCardAvailableAmount());

        // 累计情况
        setCumulativeTransferAmount(BaseCard.ZERO_AMOUNT);
        setCumulativeUsedEquityAmount(BaseCard.ZERO_AMOUNT);
    }

    @Override
    public void printCardInfo() {
        log.info("=====================");
        log.info("金额卡权益金额: {}", getEquityAmount());
        log.info("金额卡实际到账金额: {}", getArrivalAmount());
        log.info("金额卡划拨计算比例(实际到账金额 / 卡的权益金额): {}", getTransferRatio());
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
