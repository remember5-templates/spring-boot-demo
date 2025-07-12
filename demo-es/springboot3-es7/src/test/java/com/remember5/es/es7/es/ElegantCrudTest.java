package com.remember5.es.es7.es;

import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 优雅的CRUD测试示例
 * 继承BaseElasticsearchTest，享受自动的索引生命周期管理
 */
@Slf4j
@DisplayName("优雅的CRUD测试")
@Tag("elegant-crud")
class ElegantCrudTest extends BaseElasticsearchTest {

    @Test
    @DisplayName("创建文档测试")
    void testCreateDocument() {
        log.info("=== 测试创建文档 ===");

        // 1. 创建测试数据
        EsBusinessInfo business = createTestBusiness(1, "优雅测试商家");

        // 2. 保存文档
        EsBusinessInfo saved = elasticsearchRestTemplate.save(business, indexCoordinates);

        // 3. 验证结果
        assertNotNull(saved);
        assertEquals(1, saved.getId());
        assertEquals("优雅测试商家", saved.getName());

        log.info("文档创建成功: {}", saved.getName());
        logTestStats(); // 输出测试统计信息
    }

    @Test
    @DisplayName("读取文档测试")
    void testReadDocument() {
        log.info("=== 测试读取文档 ===");

        // 1. 先创建文档
        EsBusinessInfo business = createTestBusiness(2, "读取测试商家");
        elasticsearchRestTemplate.save(business, indexCoordinates);
        waitForIndexRefresh(); // 等待索引刷新

        // 2. 读取文档
        EsBusinessInfo found = elasticsearchRestTemplate.get("2", EsBusinessInfo.class, indexCoordinates);

        // 3. 验证结果
        assertNotNull(found);
        assertEquals(2, found.getId());
        assertEquals("读取测试商家", found.getName());

        log.info("文档读取成功: {}", found.getName());
    }

    @Test
    @DisplayName("更新文档测试")
    void testUpdateDocument() {
        log.info("=== 测试更新文档 ===");

        // 1. 先创建文档
        EsBusinessInfo business = createTestBusiness(3, "原始商家");
        elasticsearchRestTemplate.save(business, indexCoordinates);
        waitForIndexRefresh();

        // 2. 更新文档
        business.setName("更新后的商家");
        business.setRating(4.8);
        business.setDescription("这是更新后的描述");

        EsBusinessInfo updated = elasticsearchRestTemplate.save(business, indexCoordinates);

        // 3. 验证结果
        assertNotNull(updated);
        assertEquals("更新后的商家", updated.getName());
        assertEquals(4.8, updated.getRating());

        log.info("文档更新成功: {}", updated.getName());
    }

    @Test
    @DisplayName("删除文档测试")
    void testDeleteDocument() {
        log.info("=== 测试删除文档 ===");

        // 1. 先创建文档
        EsBusinessInfo business = createTestBusiness(4, "待删除商家");
        elasticsearchRestTemplate.save(business, indexCoordinates);
        waitForIndexRefresh();

        // 2. 验证文档存在
        EsBusinessInfo found = elasticsearchRestTemplate.get("4", EsBusinessInfo.class, indexCoordinates);
        assertNotNull(found);

        // 3. 删除文档
        String deletedId = elasticsearchRestTemplate.delete("4", indexCoordinates);
        assertEquals("4", deletedId);

        // 4. 验证文档已删除
        EsBusinessInfo deleted = elasticsearchRestTemplate.get("4", EsBusinessInfo.class, indexCoordinates);
        assertNull(deleted);

        log.info("文档删除成功: ID {}", deletedId);
    }

    @Test
    @DisplayName("批量操作测试")
    void testBulkOperations() {
        log.info("=== 测试批量操作 ===");

        // 1. 准备批量数据
        List<EsBusinessInfo> businesses = Arrays.asList(
            createTestBusiness(5, "批量商家1"),
            createTestBusiness(6, "批量商家2"),
            createTestBusiness(7, "批量商家3"),
            createTestBusiness(8, "批量商家4"),
            createTestBusiness(9, "批量商家5")
        );

        // 2. 批量保存
        long startTime = System.currentTimeMillis();
        Iterable<EsBusinessInfo> saved = elasticsearchRestTemplate.save(businesses, indexCoordinates);
        long endTime = System.currentTimeMillis();

        // 3. 验证批量保存结果
        int count = 0;
        for (EsBusinessInfo savedBusiness : saved) {
            assertNotNull(savedBusiness.getId());
            count++;
        }
        assertEquals(5, count);

        long batchTime = endTime - startTime;
        log.info("批量保存成功，处理 {} 个文档，耗时 {}ms", count, batchTime);

        // 4. 验证批量查询
        waitForIndexRefresh();
        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(termsQuery("id", Arrays.asList(5, 6, 7, 8, 9)))
            .build();

        SearchHits<EsBusinessInfo> results = elasticsearchRestTemplate.search(query, EsBusinessInfo.class, indexCoordinates);
        assertEquals(5, results.getTotalHits());

        log.info("批量查询验证成功，找到 {} 个文档", results.getTotalHits());
    }

    @Test
    @DisplayName("临时索引测试")
    void testTempIndex() {
        log.info("=== 测试临时索引 ===");

        // 1. 创建临时索引
        IndexCoordinates tempIndex = createTempIndex();

        try {
            // 2. 在临时索引中操作
            EsBusinessInfo business = createTestBusiness(10, "临时索引商家");
            EsBusinessInfo saved = elasticsearchRestTemplate.save(business, tempIndex);

            assertNotNull(saved);
            assertEquals(10, saved.getId());

            // 3. 验证临时索引中的数据
            EsBusinessInfo found = elasticsearchRestTemplate.get("10", EsBusinessInfo.class, tempIndex);
            assertNotNull(found);
            assertEquals("临时索引商家", found.getName());

            log.info("临时索引测试成功: {}", found.getName());

        } finally {
            // 4. 清理临时索引（确保执行）
            deleteTempIndex(tempIndex);
        }
    }

    @Test
    @DisplayName("复杂查询测试")
    void testComplexQuery() {
        log.info("=== 测试复杂查询 ===");

        // 1. 创建测试数据
        List<EsBusinessInfo> businesses = Arrays.asList(
            createTestBusiness(11, "北京烤鸭店", "北京市朝阳区", 4.8, true),
            createTestBusiness(12, "上海小笼包", "上海市黄浦区", 4.6, false),
            createTestBusiness(13, "广州茶餐厅", "广州市天河区", 4.5, true),
            createTestBusiness(14, "成都火锅店", "成都市锦江区", 4.9, true),
            createTestBusiness(15, "西安面馆", "西安市雁塔区", 4.4, false)
        );

        elasticsearchRestTemplate.save(businesses, indexCoordinates);
        waitForIndexRefresh();

        // 2. 复杂查询：VIP商家且评分大于4.5
        NativeSearchQuery complexQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery()
                .must(termQuery("isVip", true))
                .must(rangeQuery("rating").gte(4.5)))
            .build();

        SearchHits<EsBusinessInfo> results = elasticsearchRestTemplate.search(complexQuery, EsBusinessInfo.class, indexCoordinates);

        // 3. 验证结果
        assertTrue(results.getTotalHits() > 0);
        log.info("复杂查询成功，找到 {} 个符合条件的商家", results.getTotalHits());

        // 4. 输出结果详情
        results.getSearchHits().forEach(hit -> {
            EsBusinessInfo business = hit.getContent();
            log.info("符合条件的商家: {} (评分: {}, VIP: {})",
                business.getName(), business.getRating(), business.getIsVip());
        });
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建基础测试商家数据
     */
    private EsBusinessInfo createTestBusiness(Integer id, String name) {
        return createTestBusiness(id, name, "测试区域", 4.5, false);
    }

    /**
     * 创建带参数的测试商家数据
     */
    private EsBusinessInfo createTestBusiness(Integer id, String name, String area, Double rating, Boolean isVip) {
        EsBusinessInfo business = new EsBusinessInfo();
        business.setId(id);
        business.setName(name);
        business.setDescription("这是一个测试商家");
        business.setAddress("测试地址");
        business.setContactInfo("13800138000");
        business.setRating(rating);
        business.setSalesVolume(1000L);
        business.setReviewCount(100);
        business.setFavoriteCount(50L);
        business.setIsVip(isVip);
        business.setDeliverySupport(true);
        business.setMinOrderAmount(20.0);
        business.setArea(area);
        business.setBusinessType("餐饮服务");
        business.setMainProducts(Arrays.asList("测试产品"));
        business.setTags(Arrays.asList("测试", "示例"));
        business.setCreateTime(new Date());
        business.setUpdateTime(new Date());
        business.setCoordinate(new GeoPoint(39.916527, 116.397128));
        return business;
    }
}
