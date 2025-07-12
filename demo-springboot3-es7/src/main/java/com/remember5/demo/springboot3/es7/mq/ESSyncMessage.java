package com.remember5.demo.springboot3.es7.mq;

import com.remember5.demo.springboot3.es7.entity.ESBusinessInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ES同步消息
 * 用于消息队列方案的数据同步
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Data
public class ESSyncMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 操作类型
     */
    private String operationType; // CREATE, UPDATE, DELETE
    
    /**
     * 商家ID
     */
    private Long businessId;
    
    /**
     * 商家信息（创建和更新时需要）
     */
    private ESBusinessInfo businessInfo;
    
    /**
     * 消息时间戳
     */
    private Date timestamp;
    
    /**
     * 重试次数
     */
    private Integer retryCount = 0;
    
    /**
     * 最大重试次数
     */
    private static final Integer MAX_RETRY_COUNT = 3;
    
    public ESSyncMessage() {
        this.timestamp = new Date();
    }
    
    public ESSyncMessage(String operationType, Long businessId, ESBusinessInfo businessInfo) {
        this();
        this.operationType = operationType;
        this.businessId = businessId;
        this.businessInfo = businessInfo;
    }
    
    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
    
    /**
     * 是否可以重试
     */
    public boolean canRetry() {
        return this.retryCount < MAX_RETRY_COUNT;
    }
    
    /**
     * 创建消息
     */
    public static ESSyncMessage createMessage(Long businessId, ESBusinessInfo businessInfo) {
        return new ESSyncMessage("CREATE", businessId, businessInfo);
    }
    
    /**
     * 更新消息
     */
    public static ESSyncMessage updateMessage(Long businessId, ESBusinessInfo businessInfo) {
        return new ESSyncMessage("UPDATE", businessId, businessInfo);
    }
    
    /**
     * 删除消息
     */
    public static ESSyncMessage deleteMessage(Long businessId) {
        return new ESSyncMessage("DELETE", businessId, null);
    }
} 