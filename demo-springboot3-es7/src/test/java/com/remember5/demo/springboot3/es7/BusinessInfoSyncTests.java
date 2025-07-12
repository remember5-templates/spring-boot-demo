package com.remember5.demo.springboot3.es7;

import com.remember5.demo.springboot3.es7.entity.ESBusinessInfo;
import com.remember5.demo.springboot3.es7.service.BusinessInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * 商家信息同步测试
 * 演示数据库与ES的数据同步方案
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@DisplayName("商家信息同步测试")
@SpringBootTest(classes = DemoSpringboot3Es7Application.class)
class BusinessInfoSyncTests {

    @Autowired
    private BusinessInfoService businessInfoService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Test
    @DisplayName("测试事件驱动同步-创建商家")
    void testEventDrivenSyncCreate() throws InterruptedException {
        // 创建测试数据
        ESBusinessInfo businessInfo = ESBusinessInfo.builder()
                .id(100)
                .province("四川省")
                .city("成都市")
                .area("高新区")
                .address("天府大道")
                .name("测试商家-事件驱动")
                .contactInfo("13800138000")
                .businessType("餐饮服务")
                .status("开业")
                .salesVolume(100L)
                .rating(4.5)
                .reviewCount(50)
                .isVip(false)
                .favoriteCount(20L)
                .tags(Arrays.asList("测试", "新店"))
                .mainProducts(Arrays.asList("快餐", "饮品"))
                .deliverySupport(true)
                .minOrderAmount(15.0)
                .avgDeliveryTime(25)
                .createTime(new Date())
                .build();

        // 调用业务服务创建商家（会触发事件驱动同步）
        businessInfoService.createBusinessInfo(businessInfo);

        // 等待异步处理完成
        Thread.sleep(2000);

        // 验证ES中是否有数据
        SearchHits<ESBusinessInfo> searchHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(termQuery("id", 100))
                        .build(),
                ESBusinessInfo.class
        );

        log.info("ES查询结果数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("ES中的商家信息: {}", hit.getContent()));
    }

    @Test
    @DisplayName("测试事件驱动同步-更新商家")
    void testEventDrivenSyncUpdate() throws InterruptedException {
        // 先创建一个商家
        ESBusinessInfo businessInfo = ESBusinessInfo.builder()
                .id(101)
                .province("四川省")
                .city("成都市")
                .area("锦江区")
                .address("春熙路")
                .name("测试商家-更新前")
                .contactInfo("13800138001")
                .businessType("零售服务")
                .status("开业")
                .salesVolume(200L)
                .rating(4.2)
                .reviewCount(30)
                .isVip(true)
                .favoriteCount(100L)
                .tags(Arrays.asList("测试", "连锁"))
                .mainProducts(Arrays.asList("服装", "饰品"))
                .deliverySupport(false)
                .minOrderAmount(0.0)
                .avgDeliveryTime(0)
                .createTime(new Date())
                .build();

        // 创建商家
        businessInfoService.createBusinessInfo(businessInfo);
        Thread.sleep(1000);

        // 更新商家信息
        businessInfo.setName("测试商家-更新后");
        businessInfo.setSalesVolume(300L);
        businessInfo.setRating(4.8);
        businessInfoService.updateBusinessInfo(businessInfo);

        // 等待异步处理完成
        Thread.sleep(2000);

        // 验证ES中的数据是否已更新
        SearchHits<ESBusinessInfo> searchHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(termQuery("id", 101))
                        .build(),
                ESBusinessInfo.class
        );

        log.info("ES查询结果数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("ES中的更新后商家信息: {}", hit.getContent()));
    }

    @Test
    @DisplayName("测试事件驱动同步-删除商家")
    void testEventDrivenSyncDelete() throws InterruptedException {
        // 先创建一个商家
        ESBusinessInfo businessInfo = ESBusinessInfo.builder()
                .id(102)
                .province("四川省")
                .city("成都市")
                .area("武侯区")
                .address("武侯祠")
                .name("测试商家-待删除")
                .contactInfo("13800138002")
                .businessType("旅游服务")
                .status("开业")
                .salesVolume(150L)
                .rating(4.0)
                .reviewCount(20)
                .isVip(false)
                .favoriteCount(50L)
                .tags(Arrays.asList("测试", "旅游"))
                .mainProducts(Arrays.asList("门票", "导游"))
                .deliverySupport(false)
                .minOrderAmount(0.0)
                .avgDeliveryTime(0)
                .createTime(new Date())
                .build();

        // 创建商家
        businessInfoService.createBusinessInfo(businessInfo);
        Thread.sleep(1000);

        // 删除商家
        businessInfoService.deleteBusinessInfo(102L);

        // 等待异步处理完成
        Thread.sleep(2000);

        // 验证ES中的数据是否已删除
        SearchHits<ESBusinessInfo> searchHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(termQuery("id", 102))
                        .build(),
                ESBusinessInfo.class
        );

        log.info("ES查询结果数量: {}", searchHits.getTotalHits());
        if (searchHits.getTotalHits() == 0) {
            log.info("商家已成功从ES中删除");
        }
    }

    @Test
    @DisplayName("测试批量同步")
    void testBatchSync() throws InterruptedException {
        // 创建批量测试数据
        List<ESBusinessInfo> businessInfos = Arrays.asList(
                ESBusinessInfo.builder()
                        .id(201)
                        .province("四川省")
                        .city("成都市")
                        .area("高新区")
                        .name("批量测试商家1")
                        .contactInfo("13800138201")
                        .businessType("餐饮服务")
                        .status("开业")
                        .salesVolume(100L)
                        .rating(4.5)
                        .createTime(new Date())
                        .build(),
                ESBusinessInfo.builder()
                        .id(202)
                        .province("四川省")
                        .city("成都市")
                        .area("锦江区")
                        .name("批量测试商家2")
                        .contactInfo("13800138202")
                        .businessType("零售服务")
                        .status("开业")
                        .salesVolume(200L)
                        .rating(4.2)
                        .createTime(new Date())
                        .build(),
                ESBusinessInfo.builder()
                        .id(203)
                        .province("四川省")
                        .city("成都市")
                        .area("武侯区")
                        .name("批量测试商家3")
                        .contactInfo("13800138203")
                        .businessType("旅游服务")
                        .status("开业")
                        .salesVolume(150L)
                        .rating(4.0)
                        .createTime(new Date())
                        .build()
        );

        // 批量同步到ES
        businessInfoService.batchSyncToES(businessInfos);

        // 等待异步处理完成
        Thread.sleep(3000);

        // 验证ES中的数据
        for (ESBusinessInfo info : businessInfos) {
            SearchHits<ESBusinessInfo> searchHits = elasticsearchTemplate.search(
                    new NativeSearchQueryBuilder()
                            .withQuery(termQuery("id", info.getId()))
                            .build(),
                    ESBusinessInfo.class
            );
            log.info("商家ID: {}, ES查询结果数量: {}", info.getId(), searchHits.getTotalHits());
        }
    }

    @Test
    @DisplayName("测试强制同步")
    void testForceSync() throws InterruptedException {
        // 创建测试数据
        ESBusinessInfo businessInfo = ESBusinessInfo.builder()
                .id(301)
                .province("四川省")
                .city("成都市")
                .area("高新区")
                .address("天府软件园")
                .name("强制同步测试商家")
                .contactInfo("13800138301")
                .businessType("科技服务")
                .status("开业")
                .salesVolume(500L)
                .rating(4.9)
                .reviewCount(200)
                .isVip(true)
                .favoriteCount(500L)
                .tags(Arrays.asList("测试", "科技"))
                .mainProducts(Arrays.asList("软件开发", "技术咨询"))
                .deliverySupport(false)
                .minOrderAmount(0.0)
                .avgDeliveryTime(0)
                .createTime(new Date())
                .build();

        // 强制同步到ES
        businessInfoService.forceSyncToES(businessInfo);

        // 等待处理完成
        Thread.sleep(1000);

        // 验证ES中的数据
        SearchHits<ESBusinessInfo> searchHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(termQuery("id", 301))
                        .build(),
                ESBusinessInfo.class
        );

        log.info("强制同步后ES查询结果数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("ES中的商家信息: {}", hit.getContent()));
    }
} 