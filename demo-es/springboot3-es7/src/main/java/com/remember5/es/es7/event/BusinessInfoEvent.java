package com.remember5.es.es7.event;

import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 商家信息事件
 * 用于数据库与ES数据同步
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Getter
public class BusinessInfoEvent extends ApplicationEvent {

    private final BusinessInfoEventType eventType;
    private final EsBusinessInfo businessInfo;
    private final Long businessId;

    public BusinessInfoEvent(Object source, BusinessInfoEventType eventType, EsBusinessInfo businessInfo) {
        super(source);
        this.eventType = eventType;
        this.businessInfo = businessInfo;
        this.businessId = businessInfo != null ? businessInfo.getId().longValue() : null;
    }

    public BusinessInfoEvent(Object source, BusinessInfoEventType eventType, Long businessId) {
        super(source);
        this.eventType = eventType;
        this.businessInfo = null;
        this.businessId = businessId;
    }

    /**
     * 事件类型
     */
    public enum BusinessInfoEventType {
        CREATE,     // 创建
        UPDATE,     // 更新
        DELETE      // 删除
    }
}
