package com.remember5.demo.springboot3.es7.service;

import com.remember5.demo.springboot3.es7.entity.ESBusinessInfo;
import com.remember5.demo.springboot3.es7.event.BusinessInfoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * ES商家信息同步服务
 * 处理数据库与ES的数据同步
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@Service
public class ESBusinessInfoSyncService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    /**
     * 异步处理商家信息事件
     * 使用@Async注解实现异步处理，避免阻塞主业务
     */
    @Async("esSyncTaskExecutor")
    @EventListener
    public CompletableFuture<Void> handleBusinessInfoEvent(BusinessInfoEvent event) {
        try {
            log.info("开始处理商家信息事件: {} - ID: {}", event.getEventType(), event.getBusinessId());
            
            switch (event.getEventType()) {
                case CREATE:
                case UPDATE:
                    handleCreateOrUpdate(event);
                    break;
                case DELETE:
                    handleDelete(event);
                    break;
                default:
                    log.warn("未知的事件类型: {}", event.getEventType());
            }
            
            log.info("商家信息事件处理完成: {} - ID: {}", event.getEventType(), event.getBusinessId());
            return CompletableFuture.completedFuture(null);
            
        } catch (Exception e) {
            log.error("处理商家信息事件失败: {} - ID: {}", event.getEventType(), event.getBusinessId(), e);
            // 可以在这里实现重试机制或发送告警
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 处理创建或更新事件
     */
    private void handleCreateOrUpdate(BusinessInfoEvent event) {
        if (event.getBusinessInfo() == null) {
            log.warn("创建/更新事件中商家信息为空，跳过处理");
            return;
        }
        
        try {
            // 保存或更新ES数据
            ESBusinessInfo saved = elasticsearchTemplate.save(event.getBusinessInfo());
            log.info("ES数据同步成功: {} - ID: {}", event.getEventType(), saved.getId());
            
        } catch (Exception e) {
            log.error("ES数据同步失败: {} - ID: {}", event.getEventType(), event.getBusinessId(), e);
            throw e;
        }
    }

    /**
     * 处理删除事件
     */
    private void handleDelete(BusinessInfoEvent event) {
        if (event.getBusinessId() == null) {
            log.warn("删除事件中商家ID为空，跳过处理");
            return;
        }
        
        try {
            // 删除ES数据
            String deletedId = elasticsearchTemplate.delete(event.getBusinessId().toString(), 
                    IndexCoordinates.of("business_info"));
            log.info("ES数据删除成功: ID: {} - 删除结果: {}", event.getBusinessId(), deletedId);
            
        } catch (Exception e) {
            log.error("ES数据删除失败: ID: {}", event.getBusinessId(), e);
            throw e;
        }
    }

    /**
     * 批量同步方法（用于数据初始化或批量更新）
     */
    public void batchSync(Iterable<ESBusinessInfo> businessInfos) {
        try {
            log.info("开始批量同步商家数据到ES");
            Iterable<ESBusinessInfo> saved = elasticsearchTemplate.save(businessInfos);
            int count = 0;
            for (ESBusinessInfo info : saved) {
                count++;
            }
            log.info("批量同步完成，共同步 {} 条数据", count);
            
        } catch (Exception e) {
            log.error("批量同步失败", e);
            throw e;
        }
    }

    /**
     * 手动同步单个商家数据
     */
    public void syncSingleBusiness(ESBusinessInfo businessInfo) {
        try {
            ESBusinessInfo saved = elasticsearchTemplate.save(businessInfo);
            log.info("手动同步成功: ID: {}", saved.getId());
            
        } catch (Exception e) {
            log.error("手动同步失败: ID: {}", businessInfo.getId(), e);
            throw e;
        }
    }
} 