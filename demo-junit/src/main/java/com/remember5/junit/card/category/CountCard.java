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
 * 次卡
 *
 * @author wangjiahao
 * @date 2025/9/13 22:13
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CountCard extends GeneralCard implements Serializable, Cloneable {

    public CountCard(String payAmount, String arrivalAmount, String reservePrecent, Integer totalCount) {
        // 卡的基本信息
        setPayAmount(new BigDecimal(payAmount).setScale(2, RoundingMode.DOWN));
        setArrivalAmount(new BigDecimal(arrivalAmount).setScale(2, RoundingMode.DOWN));
        setReservePrecent(new BigDecimal(reservePrecent).setScale(2, RoundingMode.DOWN));
        setTotalCount(totalCount);
        setRemainingCount(totalCount);

        // 计算
        setCardReserveAmount(getArrivalAmount().multiply(getReservePrecent()).setScale(2, RoundingMode.UP));
        setCardAvailableAmount(getArrivalAmount().subtract(getCardReserveAmount()));
        setEachAmount(getArrivalAmount().divide(new BigDecimal(getTotalCount()), 2, RoundingMode.DOWN));

        // 当前情况
        setCurrentReserveAmount(getCardReserveAmount());
        setCurrentAvailableAmount(getCardAvailableAmount());

        // 累计情况
        setCumulativeTransferAmount(BaseCard.ZERO_AMOUNT);
    }

    @Override
    public void printCardInfo() {
        log.info("=====================");
        log.info("次卡实际到账金额: {}", getArrivalAmount());
        log.info("次卡监管比例: {}", getReservePrecent());
        log.info("次卡留底资金: {}", getCardReserveAmount());
        log.info("次卡可支用资金: {}", getCardAvailableAmount());
        log.info("次卡总次数: {}", getTotalCount());
        log.info("每次核销金额: {}", getEachAmount());
        log.info("=====================");
    }


    @Override
    public CountCard clone() {
        try {
            return (CountCard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone CountCard", e);
        }
    }
}
