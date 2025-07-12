package com.remember5.demo.springboot3.es7.service;

import com.remember5.demo.springboot3.es7.entity.ESBusinessInfo;
import com.remember5.demo.springboot3.es7.event.BusinessInfoEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 商家信息服务
 * 演示如何在业务操作中集成ES同步
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@Service
public class BusinessInfoService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ESBusinessInfoSyncService esSyncService;

    /**
     * 创建商家信息
     * 1. 保存到数据库
     * 2. 发布创建事件，异步同步到ES
     */
    @Transactional
    public ESBusinessInfo createBusinessInfo(ESBusinessInfo businessInfo) {
        try {
            // 1. 设置创建时间
            businessInfo.setCreateTime(new Date());
            businessInfo.setUpdateTime(new Date());
            
            // 2. 保存到数据库（这里模拟，实际应该调用数据库服务）
            log.info("保存商家信息到数据库: {}", businessInfo.getName());
            // businessInfoRepository.save(businessInfo);
            
            // 3. 发布创建事件，异步同步到ES
            eventPublisher.publishEvent(new BusinessInfoEvent(this, 
                    BusinessInfoEvent.BusinessInfoEventType.CREATE, businessInfo));
            
            log.info("商家信息创建成功，已发布同步事件: {}", businessInfo.getId());
            return businessInfo;
            
        } catch (Exception e) {
            log.error("创建商家信息失败: {}", businessInfo.getName(), e);
            throw e;
        }
    }

    /**
     * 更新商家信息
     * 1. 更新数据库
     * 2. 发布更新事件，异步同步到ES
     */
    @Transactional
    public ESBusinessInfo updateBusinessInfo(ESBusinessInfo businessInfo) {
        try {
            // 1. 设置更新时间
            businessInfo.setUpdateTime(new Date());
            
            // 2. 更新数据库（这里模拟，实际应该调用数据库服务）
            log.info("更新商家信息到数据库: {}", businessInfo.getName());
            // businessInfoRepository.save(businessInfo);
            
            // 3. 发布更新事件，异步同步到ES
            eventPublisher.publishEvent(new BusinessInfoEvent(this, 
                    BusinessInfoEvent.BusinessInfoEventType.UPDATE, businessInfo));
            
            log.info("商家信息更新成功，已发布同步事件: {}", businessInfo.getId());
            return businessInfo;
            
        } catch (Exception e) {
            log.error("更新商家信息失败: {}", businessInfo.getName(), e);
            throw e;
        }
    }

    /**
     * 删除商家信息
     * 1. 从数据库删除
     * 2. 发布删除事件，异步从ES删除
     */
    @Transactional
    public void deleteBusinessInfo(Long businessId) {
        try {
            // 1. 从数据库删除（这里模拟，实际应该调用数据库服务）
            log.info("从数据库删除商家信息: {}", businessId);
            // businessInfoRepository.deleteById(businessId);
            
            // 2. 发布删除事件，异步从ES删除
            eventPublisher.publishEvent(new BusinessInfoEvent(this, 
                    BusinessInfoEvent.BusinessInfoEventType.DELETE, businessId));
            
            log.info("商家信息删除成功，已发布同步事件: {}", businessId);
            
        } catch (Exception e) {
            log.error("删除商家信息失败: {}", businessId, e);
            throw e;
        }
    }

    /**
     * 批量更新商家信息
     * 适用于批量操作场景
     */
    @Transactional
    public void batchUpdateBusinessInfo(List<ESBusinessInfo> businessInfos) {
        try {
            log.info("开始批量更新商家信息，数量: {}", businessInfos.size());
            
            for (ESBusinessInfo businessInfo : businessInfos) {
                // 1. 更新数据库
                businessInfo.setUpdateTime(new Date());
                // businessInfoRepository.save(businessInfo);
                
                // 2. 发布更新事件
                eventPublisher.publishEvent(new BusinessInfoEvent(this, 
                        BusinessInfoEvent.BusinessInfoEventType.UPDATE, businessInfo));
            }
            
            log.info("批量更新商家信息完成，数量: {}", businessInfos.size());
            
        } catch (Exception e) {
            log.error("批量更新商家信息失败", e);
            throw e;
        }
    }

    /**
     * 强制同步单个商家到ES
     * 用于数据修复或手动同步场景
     */
    public void forceSyncToES(ESBusinessInfo businessInfo) {
        try {
            log.info("强制同步商家信息到ES: {}", businessInfo.getId());
            esSyncService.syncSingleBusiness(businessInfo);
            log.info("强制同步完成: {}", businessInfo.getId());
            
        } catch (Exception e) {
            log.error("强制同步失败: {}", businessInfo.getId(), e);
            throw e;
        }
    }

    /**
     * 批量同步商家信息到ES
     * 用于数据初始化或全量同步场景
     */
    public void batchSyncToES(List<ESBusinessInfo> businessInfos) {
        try {
            log.info("开始批量同步商家信息到ES，数量: {}", businessInfos.size());
            esSyncService.batchSync(businessInfos);
            log.info("批量同步完成，数量: {}", businessInfos.size());
            
        } catch (Exception e) {
            log.error("批量同步失败", e);
            throw e;
        }
    }
} 