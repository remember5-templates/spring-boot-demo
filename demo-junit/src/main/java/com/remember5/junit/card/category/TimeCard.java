package com.remember5.junit.card.category;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * 时长卡
 *
 * @author wangjiahao
 * @date 2025/9/14 11:16
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TimeCard extends GeneralCard implements Serializable, Cloneable {

    public TimeCard(String payAmount, String arrivalAmount, String reservePrecent, Integer totalCount, Date startTime) {
        // 卡的基本信息
        setPayAmount(new BigDecimal(payAmount).setScale(2, RoundingMode.DOWN));
        setArrivalAmount(new BigDecimal(arrivalAmount).setScale(2, RoundingMode.DOWN));
        setReservePrecent(new BigDecimal(reservePrecent).setScale(2, RoundingMode.DOWN));
        setTotalCount(totalCount);
        setRemainingCount(totalCount);
        setStartTime(startTime);
        setEndTime(DateUtils.addDays(startTime, totalCount));

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

    /**
     * 时长卡生效期
     */
    private Date startTime;

    /**
     * 时长卡失效期
     */
    private Date endTime;


    @Override
    public void printCardInfo() {
        log.info("=====================");
        log.info("时长卡实际到账金额: {}", getArrivalAmount());
        log.info("时长卡监管比例: {}", getReservePrecent());
        log.info("时长卡留底资金: {}", getCardReserveAmount());
        log.info("时长卡可支用资金: {}", getCardAvailableAmount());
        log.info("时长卡总时长: {}", getTotalCount());
        log.info("每次核销金额: {}", getEachAmount());
        log.info("时长卡生效期: {}", startTime);
        log.info("时长卡失效期: {}", endTime);
        log.info("=====================");
    }

    @Override
    public TimeCard clone() {
        try {
            return (TimeCard) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone CountCard", e);
        }
    }
}
