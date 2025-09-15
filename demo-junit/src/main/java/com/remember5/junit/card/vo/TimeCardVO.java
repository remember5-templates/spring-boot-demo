package com.remember5.junit.card.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wangjiahao
 * @date 2025/9/15 10:17
 */
@Getter
@Setter
public class TimeCardVO {

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
     * 总次数
     */
    @NotNull
    private Integer totalCount;

    /**
     * 留底百分比
     */
    @NotBlank
    private String reservePrecent;

    /**
     * 卡的生效日期
     */
    @NotNull
    private Date startTime;

}
