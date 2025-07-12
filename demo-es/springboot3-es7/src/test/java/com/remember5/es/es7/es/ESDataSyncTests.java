/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.es.es7.es;

import com.remember5.es.es7.Springboot3Es7Application;
import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Elasticsearch数据同步测试
 * 测试事件驱动同步、批量同步、强制同步等数据同步功能
 */
@Slf4j
@DisplayName("ES数据同步测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("sync")
@Tag("data")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ESDataSyncTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final String INDEX_NAME = "business_info_sync";
    private static final IndexCoordinates INDEX_COORDINATES = IndexCoordinates.of(INDEX_NAME);

    @Test
    @Order(1)
    @DisplayName("创建索引和初始数据")
    void testSetup() {
        // 创建索引
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
        indexOperations.create();

        // 创建初始数据
        List<EsBusinessInfo> initialData = Arrays.asList(
            createBusiness(1, "初始商家1", "餐饮服务", "北京市朝阳区", 4.5, 800L, false,
                Arrays.asList("快餐"), new GeoPoint(39.916527, 116.397128)),
            createBusiness(2, "初始商家2", "餐饮服务", "上海市黄浦区", 4.6, 900L, true,
                Arrays.asList("火锅"), new GeoPoint(31.230416, 121.473701))
        );

        elasticsearchRestTemplate.save(initialData, INDEX_COORDINATES);
        log.info("成功创建初始数据，共 {} 个商家", initialData.size());
    }

    @Test
    @Order(2)
    @DisplayName("事件驱动同步")
    void testEventDrivenSync() throws Exception {
        log.info("=== 事件驱动同步测试 ===");

        // 1. 模拟业务事件：新增商家
        EsBusinessInfo newBusiness = createBusiness(3, "事件驱动商家", "餐饮服务", "广州市天河区", 4.7, 1000L, true,
            Arrays.asList("粤菜"), new GeoPoint(23.129163, 113.264435));

        // 2. 异步保存（模拟事件驱动）
        CompletableFuture<EsBusinessInfo> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(100); // 模拟异步处理时间
                return elasticsearchRestTemplate.save(newBusiness, INDEX_COORDINATES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });

        // 3. 等待异步操作完成
        EsBusinessInfo savedBusiness = future.get(5, TimeUnit.SECONDS);
        assertNotNull(savedBusiness);
        assertEquals(3, savedBusiness.getId());
        log.info("事件驱动同步成功: {}", savedBusiness.getName());

        // 4. 验证数据已同步
        NativeSearchQuery verifyQuery = new NativeSearchQueryBuilder()
            .withQuery(termQuery("id", 3))
            .build();

        SearchHits<EsBusinessInfo> verifyResults = elasticsearchRestTemplate.search(verifyQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(1, verifyResults.getTotalHits());
        log.info("事件驱动同步验证成功，找到 {} 个文档", verifyResults.getTotalHits());
    }

    @Test
    @Order(3)
    @DisplayName("批量同步")
    void testBatchSync() {
        log.info("=== 批量同步测试 ===");

        // 1. 准备批量数据
        List<EsBusinessInfo> batchData = Arrays.asList(
            createBusiness(4, "批量商家1", "餐饮服务", "深圳市南山区", 4.8, 1200L, true,
                Arrays.asList("川菜"), new GeoPoint(22.543099, 114.057868)),
            createBusiness(5, "批量商家2", "餐饮服务", "杭州市西湖区", 4.6, 950L, false,
                Arrays.asList("浙菜"), new GeoPoint(30.274085, 120.155070)),
            createBusiness(6, "批量商家3", "餐饮服务", "南京市鼓楼区", 4.4, 750L, false,
                Arrays.asList("苏菜"), new GeoPoint(32.060255, 118.796877)),
            createBusiness(7, "批量商家4", "餐饮服务", "武汉市江汉区", 4.5, 850L, true,
                Arrays.asList("鄂菜"), new GeoPoint(30.592850, 114.305542)),
            createBusiness(8, "批量商家5", "餐饮服务", "成都市锦江区", 4.9, 1500L, true,
                Arrays.asList("川菜"), new GeoPoint(30.572815, 104.066803))
        );

        // 2. 执行批量保存
        long startTime = System.currentTimeMillis();
        Iterable<EsBusinessInfo> batchResults = elasticsearchRestTemplate.save(batchData, INDEX_COORDINATES);
        long endTime = System.currentTimeMillis();

        // 3. 验证批量操作结果
        int batchCount = 0;
        for (EsBusinessInfo business : batchResults) {
            assertNotNull(business.getId());
            batchCount++;
        }
        assertEquals(5, batchCount);

        long batchTime = endTime - startTime;
        log.info("批量同步成功，处理 {} 个文档，耗时 {}ms", batchCount, batchTime);

        // 4. 验证批量数据已同步
        NativeSearchQuery batchVerifyQuery = new NativeSearchQueryBuilder()
            .withQuery(termsQuery("id", Arrays.asList(4, 5, 6, 7, 8)))
            .build();

        SearchHits<EsBusinessInfo> batchVerifyResults = elasticsearchRestTemplate.search(batchVerifyQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(5, batchVerifyResults.getTotalHits());
        log.info("批量同步验证成功，找到 {} 个文档", batchVerifyResults.getTotalHits());
    }

    @Test
    @Order(4)
    @DisplayName("强制同步")
    void testForceSync() {
        log.info("=== 强制同步测试 ===");

        // 1. 强制刷新索引
        elasticsearchRestTemplate.indexOps(INDEX_COORDINATES).refresh();
        log.info("索引强制刷新完成");

        // 2. 验证所有数据已同步
        NativeSearchQuery allDataQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .build();

        SearchHits<EsBusinessInfo> allDataResults = elasticsearchRestTemplate.search(allDataQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(8, allDataResults.getTotalHits());
        log.info("强制同步验证成功，索引中共有 {} 个文档", allDataResults.getTotalHits());

        // 3. 验证数据完整性
        for (int i = 1; i <= 8; i++) {
            NativeSearchQuery singleQuery = new NativeSearchQueryBuilder()
                .withQuery(termQuery("id", i))
                .build();

            SearchHits<EsBusinessInfo> singleResults = elasticsearchRestTemplate.search(singleQuery, EsBusinessInfo.class, INDEX_COORDINATES);
            assertEquals(1, singleResults.getTotalHits(), "ID为 " + i + " 的文档应该存在");
        }
        log.info("数据完整性验证成功，所有文档都已正确同步");
    }

    @Test
    @Order(5)
    @DisplayName("增量同步")
    void testIncrementalSync() {
        log.info("=== 增量同步测试 ===");

        // 1. 获取当前数据状态
        long currentCount = elasticsearchRestTemplate.count(new NativeSearchQueryBuilder().build(), INDEX_COORDINATES);
        log.info("当前索引文档数量: {}", currentCount);

        // 2. 模拟增量数据
        List<EsBusinessInfo> incrementalData = Arrays.asList(
            createBusiness(9, "增量商家1", "餐饮服务", "西安市雁塔区", 4.3, 600L, false,
                Arrays.asList("陕菜"), new GeoPoint(34.341568, 108.939840)),
            createBusiness(10, "增量商家2", "餐饮服务", "重庆市渝中区", 4.7, 1100L, true,
                Arrays.asList("渝菜"), new GeoPoint(29.564710, 106.550464))
        );

        // 3. 执行增量同步
        Iterable<EsBusinessInfo> incrementalResults = elasticsearchRestTemplate.save(incrementalData, INDEX_COORDINATES);

        int incrementalCount = 0;
        for (EsBusinessInfo business : incrementalResults) {
            incrementalCount++;
        }
        assertEquals(2, incrementalCount);
        log.info("增量同步成功，新增 {} 个文档", incrementalCount);

        // 4. 验证增量同步结果
        long newCount = elasticsearchRestTemplate.count(new NativeSearchQueryBuilder().build(), INDEX_COORDINATES);
        assertEquals(currentCount + 2, newCount);
        log.info("增量同步验证成功，文档总数从 {} 增加到 {}", currentCount, newCount);
    }

    @Test
    @Order(6)
    @DisplayName("同步性能测试")
    void testSyncPerformance() {
        log.info("=== 同步性能测试 ===");

        // 1. 小批量同步性能测试
        List<EsBusinessInfo> smallBatch = Arrays.asList(
            createBusiness(11, "性能测试商家1", "餐饮服务", "北京市海淀区", 4.5, 800L, false,
                Arrays.asList("快餐"), new GeoPoint(39.916527, 116.397128))
        );

        long smallBatchStart = System.currentTimeMillis();
        elasticsearchRestTemplate.save(smallBatch, INDEX_COORDINATES);
        long smallBatchEnd = System.currentTimeMillis();
        long smallBatchTime = smallBatchEnd - smallBatchStart;

        log.info("小批量同步性能: {}ms", smallBatchTime);
        assertTrue(smallBatchTime < 1000, "小批量同步应该在1秒内完成");

        // 2. 大批量同步性能测试
        List<EsBusinessInfo> largeBatch = Arrays.asList();
        for (int i = 12; i <= 21; i++) {
            largeBatch.add(createBusiness(i, "大批量商家" + (i - 11), "餐饮服务", "测试区域", 4.0 + (i % 10) * 0.1,
                500L + i * 50, i % 2 == 0, Arrays.asList("测试菜品"), new GeoPoint(39.916527, 116.397128)));
        }

        long largeBatchStart = System.currentTimeMillis();
        elasticsearchRestTemplate.save(largeBatch, INDEX_COORDINATES);
        long largeBatchEnd = System.currentTimeMillis();
        long largeBatchTime = largeBatchEnd - largeBatchStart;

        log.info("大批量同步性能: {}ms, 处理 {} 个文档", largeBatchTime, largeBatch.size());
        assertTrue(largeBatchTime < 3000, "大批量同步应该在3秒内完成");

        // 3. 查询性能测试
        long queryStart = System.currentTimeMillis();
        long totalCount = elasticsearchRestTemplate.count(new NativeSearchQueryBuilder().build(), INDEX_COORDINATES);
        long queryEnd = System.currentTimeMillis();
        long queryTime = queryEnd - queryStart;

        log.info("查询性能: {}ms, 总文档数: {}", queryTime, totalCount);
        assertTrue(queryTime < 500, "查询应该在500ms内完成");
    }

    @Test
    @Order(7)
    @DisplayName("清理测试数据")
    void testCleanup() {
        log.info("=== 清理测试数据 ===");

        // 删除测试索引
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES);
        if (indexOperations.exists()) {
            boolean deleted = indexOperations.delete();
            assertTrue(deleted, "索引删除失败");
            log.info("成功删除测试索引: {}", INDEX_NAME);
        }
    }

    /**
     * 创建测试商家数据
     */
    private EsBusinessInfo createBusiness(Integer id, String name, String businessType, String area,
                                          Double rating, Long salesVolume, Boolean isVip,
                                          List<String> mainProducts, GeoPoint coordinate) {
        EsBusinessInfo business = new EsBusinessInfo();
        business.setId(id);
        business.setName(name);
        business.setBusinessType(businessType);
        business.setArea(area);
        business.setRating(rating);
        business.setSalesVolume(salesVolume);
        business.setIsVip(isVip);
        business.setMainProducts(mainProducts);
        business.setCoordinate(coordinate);
        business.setCreateTime(new Date());
        business.setUpdateTime(new Date());
        business.setReviewCount(100);
        business.setFavoriteCount(50L);
        business.setDeliverySupport(true);
        business.setMinOrderAmount(20.0);
        business.setDescription("这是一个测试商家");
        business.setAddress("测试地址");
        business.setTags(Arrays.asList("测试", "示例"));
        return business;
    }
}
