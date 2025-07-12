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
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
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
import java.util.Map;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Elasticsearch高级功能测试
 * 测试索引管理、别名管理、映射管理、性能优化等高级功能
 */
@Slf4j
@DisplayName("ES高级功能测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("advanced")
@Tag("management")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ESAdvancedFeatureTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final String INDEX_NAME = "business_info_advanced";
    private static final String ALIAS_NAME = "business_info_alias";
    private static final IndexCoordinates INDEX_COORDINATES = IndexCoordinates.of(INDEX_NAME);

    @Test
    @Order(1)
    @DisplayName("创建索引和测试数据")
    void testSetup() {
        // 创建索引
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES);
        if (indexOperations.exists()) {
            indexOperations.delete();
        }
        indexOperations.create();

        // 创建测试数据
        List<EsBusinessInfo> businesses = Arrays.asList(
            createBusiness(1, "北京烤鸭店", "餐饮服务", "北京市朝阳区", 4.8, 1000L, true,
                Arrays.asList("烤鸭", "京菜"), new GeoPoint(39.916527, 116.397128)),
            createBusiness(2, "上海小笼包", "餐饮服务", "上海市黄浦区", 4.6, 800L, false,
                Arrays.asList("小笼包", "沪菜"), new GeoPoint(31.230416, 121.473701)),
            createBusiness(3, "广州茶餐厅", "餐饮服务", "广州市天河区", 4.5, 1200L, true,
                Arrays.asList("茶点", "粤菜"), new GeoPoint(23.129163, 113.264435))
        );

        elasticsearchRestTemplate.save(businesses, INDEX_COORDINATES);
        log.info("成功创建测试数据，共 {} 个商家", businesses.size());
    }

    @Test
    @Order(2)
    @DisplayName("索引管理")
    void testIndexManagement() throws Exception {
        log.info("=== 索引管理测试 ===");

        // 1. 检查索引是否存在
        boolean indexExists = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES).exists();
        assertTrue(indexExists, "索引应该存在");
        log.info("索引 {} 存在: {}", INDEX_NAME, indexExists);

        // 2. 获取索引设置
        GetSettingsRequest getSettingsRequest = new GetSettingsRequest().indices(INDEX_NAME);
        GetSettingsResponse getSettingsResponse = restHighLevelClient.indices().getSettings(getSettingsRequest, RequestOptions.DEFAULT);

        Settings indexSettings = getSettingsResponse.getIndexToSettings().get(INDEX_NAME);
        assertNotNull(indexSettings);
        log.info("索引设置: {}", indexSettings);

        // 3. 获取索引统计信息
        long documentCount = elasticsearchRestTemplate.count(new NativeSearchQueryBuilder().build(), INDEX_COORDINATES);
        assertEquals(3, documentCount);
        log.info("索引文档数量: {}", documentCount);

        // 4. 刷新索引
        elasticsearchRestTemplate.indexOps(INDEX_COORDINATES).refresh();
        log.info("索引刷新成功");

        // 5. 强制合并索引（通过REST API）
        try {
            restHighLevelClient.indices().forceMerge(
                new org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest(INDEX_NAME),
                RequestOptions.DEFAULT
            );
            log.info("索引强制合并成功");
        } catch (Exception e) {
            log.warn("索引强制合并失败: {}", e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("别名管理")
    void testAliasManagement() throws Exception {
        log.info("=== 别名管理测试 ===");

        // 1. 创建索引别名
        IndicesAliasesRequest aliasRequest = new IndicesAliasesRequest();
        aliasRequest.addAliasAction(IndicesAliasesRequest.AliasActions.add()
                .index(INDEX_NAME)
                .alias(ALIAS_NAME));

        boolean aliasCreated = restHighLevelClient.indices().updateAliases(aliasRequest, RequestOptions.DEFAULT).isAcknowledged();
        assertTrue(aliasCreated, "别名创建失败");
        log.info("索引别名创建成功: {}", ALIAS_NAME);

        // 2. 检查别名是否存在
        GetAliasesRequest getAliasesRequest = new GetAliasesRequest(ALIAS_NAME);
        boolean aliasExists = restHighLevelClient.indices().existsAlias(getAliasesRequest, RequestOptions.DEFAULT);
        assertTrue(aliasExists, "别名应该存在");
        log.info("别名 {} 存在: {}", ALIAS_NAME, aliasExists);

        // 3. 通过别名查询
        NativeSearchQuery aliasQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .build();

        SearchHits<EsBusinessInfo> aliasResults = elasticsearchRestTemplate.search(aliasQuery, EsBusinessInfo.class, IndexCoordinates.of(ALIAS_NAME));
        assertEquals(3, aliasResults.getTotalHits());
        log.info("通过别名查询结果数量: {}", aliasResults.getTotalHits());

        // 4. 删除别名
        IndicesAliasesRequest deleteAliasRequest = new IndicesAliasesRequest();
        deleteAliasRequest.addAliasAction(IndicesAliasesRequest.AliasActions.remove()
                .index(INDEX_NAME)
                .alias(ALIAS_NAME));

        boolean aliasDeleted = restHighLevelClient.indices().updateAliases(deleteAliasRequest, RequestOptions.DEFAULT).isAcknowledged();
        assertTrue(aliasDeleted, "别名删除失败");
        log.info("索引别名删除成功: {}", ALIAS_NAME);
    }

    @Test
    @Order(4)
    @DisplayName("映射管理")
    void testMappingManagement() throws Exception {
        log.info("=== 映射管理测试 ===");

        // 1. 获取索引映射
        GetMappingsRequest getMappingsRequest = new GetMappingsRequest().indices(INDEX_NAME);
        GetMappingsResponse getMappingsResponse = restHighLevelClient.indices().getMapping(getMappingsRequest, RequestOptions.DEFAULT);

        assertNotNull(getMappingsResponse);
        assertTrue(getMappingsResponse.mappings().containsKey(INDEX_NAME));
        log.info("索引映射获取成功");

        // 2. 验证索引结构
        Map<String, Object> mapping = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES).getMapping();
        assertNotNull(mapping);
        assertTrue(mapping.containsKey("properties"), "索引映射应该包含properties");
        log.info("索引字段验证成功");
    }

    @Test
    @Order(5)
    @DisplayName("性能优化")
    void testPerformanceOptimization() {
        log.info("=== 性能优化测试 ===");

        // 1. 使用_source过滤减少网络传输
        NativeSearchQuery sourceFilterQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .withSourceFilter(new org.springframework.data.elasticsearch.core.query.SourceFilter() {
                @Override
                public String[] getIncludes() {
                    return new String[]{"name", "rating", "salesVolume"};
                }

                @Override
                public String[] getExcludes() {
                    return new String[]{};
                }
            })
            .build();

        SearchHits<EsBusinessInfo> sourceFilterResults = elasticsearchRestTemplate.search(sourceFilterQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(3, sourceFilterResults.getTotalHits());
        log.info("Source过滤查询结果数量: {}", sourceFilterResults.getTotalHits());

        // 2. 使用路由优化查询性能
        NativeSearchQuery routedQuery = new NativeSearchQueryBuilder()
            .withQuery(termQuery("businessType", "餐饮服务"))
            .withMaxResults(10)
            .build();

        SearchHits<EsBusinessInfo> routedResults = elasticsearchRestTemplate.search(routedQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(3, routedResults.getTotalHits());
        log.info("路由查询结果数量: {}", routedResults.getTotalHits());

        // 3. 使用缓存优化
        NativeSearchQuery cachedQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery()
                .must(termQuery("businessType", "餐饮服务"))
                .filter(rangeQuery("rating").gte(4.0)))
            .build();

        SearchHits<EsBusinessInfo> cachedResults = elasticsearchRestTemplate.search(cachedQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(cachedResults.getTotalHits() > 0);
        log.info("缓存优化查询结果数量: {}", cachedResults.getTotalHits());

        // 4. 批量操作优化
        List<EsBusinessInfo> batchData = Arrays.asList(
            createBusiness(4, "批量商家1", "餐饮服务", "北京市海淀区", 4.7, 900L, false,
                Arrays.asList("快餐"), new GeoPoint(39.916527, 116.397128)),
            createBusiness(5, "批量商家2", "餐饮服务", "北京市西城区", 4.6, 850L, true,
                Arrays.asList("火锅"), new GeoPoint(39.916527, 116.397128))
        );

        Iterable<EsBusinessInfo> batchResults = elasticsearchRestTemplate.save(batchData, INDEX_COORDINATES);
        int batchCount = 0;
        for (EsBusinessInfo business : batchResults) {
            batchCount++;
        }
        assertEquals(2, batchCount);
        log.info("批量操作成功，处理 {} 个文档", batchCount);
    }

    @Test
    @Order(6)
    @DisplayName("监控和诊断")
    void testMonitoringAndDiagnostics() {
        log.info("=== 监控和诊断测试 ===");

        // 1. 索引健康状态检查
        boolean indexExists = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES).exists();
        assertTrue(indexExists, "索引应该存在");
        log.info("索引健康状态: 正常");

        // 2. 查询性能分析
        long startTime = System.currentTimeMillis();
        NativeSearchQuery performanceQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .withMaxResults(100)
            .build();

        SearchHits<EsBusinessInfo> performanceResults = elasticsearchRestTemplate.search(performanceQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        long endTime = System.currentTimeMillis();
        long queryTime = endTime - startTime;

        assertTrue(queryTime < 1000, "查询时间应该小于1秒");
        log.info("查询性能: {}ms, 结果数量: {}", queryTime, performanceResults.getTotalHits());

        // 3. 内存使用情况
        long totalDocuments = elasticsearchRestTemplate.count(new NativeSearchQueryBuilder().build(), INDEX_COORDINATES);
        log.info("索引文档总数: {}", totalDocuments);

        // 4. 查询统计
        NativeSearchQuery statsQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery()
                .must(termQuery("businessType", "餐饮服务"))
                .should(termQuery("isVip", true)))
            .build();

        SearchHits<EsBusinessInfo> statsResults = elasticsearchRestTemplate.search(statsQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("复杂查询统计: 匹配文档数 {}", statsResults.getTotalHits());
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
