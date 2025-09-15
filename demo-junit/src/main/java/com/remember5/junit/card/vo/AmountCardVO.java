package com.remember5.junit.card.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2025/9/15 10:17
 */
@Getter
@Setter
public class AmountCardVO {

    /**
     * 订单金额
     */
    @NotBlank
    private String orderAmount;

    /**
     * 实际到账金额
     */
    @NotBlank
    private String arrivalAmount;

    /**
     * 留底百分比
     */
    @NotBlank
    private String reservePrecent;

    /**
     * 消费金额
     */
    private List<BigDecimal> expenseAmount;

}
