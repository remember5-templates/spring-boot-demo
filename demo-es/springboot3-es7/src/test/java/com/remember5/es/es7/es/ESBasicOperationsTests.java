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
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.Arrays;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Elasticsearch基础操作测试
 * 测试文档的CRUD操作和批量操作
 */
@Slf4j
@DisplayName("ES基础操作测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("basic")
@Tag("crud")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ESBasicOperationsTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final String INDEX_NAME = "business_info_basic";
    private static final IndexCoordinates INDEX_COORDINATES = IndexCoordinates.of(INDEX_NAME);

    @Test
    @Order(1)
    @DisplayName("创建索引")
    void testCreateIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES);

        if (indexOperations.exists()) {
            indexOperations.delete();
            log.info("删除已存在的索引: {}", INDEX_NAME);
        }

        boolean created = indexOperations.create();
        assertTrue(created, "索引创建失败");
        log.info("成功创建索引: {}", INDEX_NAME);
    }

    @Test
    @Order(2)
    @DisplayName("创建文档")
    void testCreateDocument() {
        EsBusinessInfo business = createTestBusiness(1, "测试商家1");

        EsBusinessInfo saved = elasticsearchRestTemplate.save(business, INDEX_COORDINATES);
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals(1, saved.getId());
        log.info("成功创建文档: {}", saved.getId());
    }

    @Test
    @Order(3)
    @DisplayName("读取文档")
    void testReadDocument() {
        EsBusinessInfo found = elasticsearchRestTemplate.get("1", EsBusinessInfo.class, INDEX_COORDINATES);

        assertNotNull(found);
        assertEquals(1, found.getId());
        assertEquals("测试商家1", found.getName());
        log.info("成功读取文档: {}", found.getId());
    }

    @Test
    @Order(4)
    @DisplayName("更新文档")
    void testUpdateDocument() {
        // 先创建文档
        EsBusinessInfo business = createTestBusiness(2, "原始商家");
        elasticsearchRestTemplate.save(business, INDEX_COORDINATES);

        // 更新文档
        business.setName("更新后的商家");
        business.setDescription("这是更新后的描述");
        business.setRating(4.8);

        EsBusinessInfo updated = elasticsearchRestTemplate.save(business, INDEX_COORDINATES);
        assertNotNull(updated);
        assertEquals("更新后的商家", updated.getName());
        assertEquals(4.8, updated.getRating());
        log.info("成功更新文档: {}", updated.getId());
    }

    @Test
    @Order(5)
    @DisplayName("删除文档")
    void testDeleteDocument() {
        // 先创建文档
        EsBusinessInfo business = createTestBusiness(3, "待删除商家");
        elasticsearchRestTemplate.save(business, INDEX_COORDINATES);

        // 验证文档存在
        EsBusinessInfo found = elasticsearchRestTemplate.get("3", EsBusinessInfo.class, INDEX_COORDINATES);
        assertNotNull(found);

        // 删除文档
        String deletedId = elasticsearchRestTemplate.delete("3", INDEX_COORDINATES);
        assertEquals("3", deletedId);

        // 验证文档已删除
        EsBusinessInfo deleted = elasticsearchRestTemplate.get("3", EsBusinessInfo.class, INDEX_COORDINATES);
        assertNull(deleted);
        log.info("成功删除文档: {}", deletedId);
    }

    @Test
    @Order(6)
    @DisplayName("批量操作")
    void testBulkOperations() {
        // 准备批量数据
        List<EsBusinessInfo> businesses = Arrays.asList(
            createTestBusiness(101, "批量商家1"),
            createTestBusiness(102, "批量商家2"),
            createTestBusiness(103, "批量商家3"),
            createTestBusiness(104, "批量商家4"),
            createTestBusiness(105, "批量商家5")
        );

        // 批量保存
        Iterable<EsBusinessInfo> saved = elasticsearchRestTemplate.save(businesses, INDEX_COORDINATES);
        assertNotNull(saved);

        // 验证批量保存结果
        int count = 0;
        for (EsBusinessInfo savedBusiness : saved) {
            assertNotNull(savedBusiness.getId());
            count++;
        }
        assertEquals(5, count);
        log.info("成功批量保存 {} 个文档", count);

        // 批量查询验证
        Query query = new NativeSearchQueryBuilder()
            .withQuery(termsQuery("id", Arrays.asList(101, 102, 103, 104, 105)))
            .build();

        SearchHits<EsBusinessInfo> searchHits = elasticsearchRestTemplate.search(query, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(5, searchHits.getTotalHits());
        log.info("批量查询验证成功，找到 {} 个文档", searchHits.getTotalHits());
    }

    @Test
    @Order(7)
    @DisplayName("清理测试数据")
    void testCleanup() {
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
    private EsBusinessInfo createTestBusiness(Integer id, String name) {
        EsBusinessInfo business = new EsBusinessInfo();
        business.setId(id);
        business.setName(name);
        business.setDescription("这是一个测试商家");
        business.setAddress("测试地址");
        business.setContactInfo("13800138000");
        business.setRating(4.5);
        business.setSalesVolume(1000L);
        business.setReviewCount(50);
        business.setFavoriteCount(20L);
        business.setMinOrderAmount(20.0);
        business.setIsVip(true);
        business.setDeliverySupport(true);
        business.setArea("测试区域");
        business.setMainProducts(Arrays.asList("测试产品"));
        business.setTags(Arrays.asList("测试", "示例"));
        business.setCreateTime(new java.util.Date());
        business.setUpdateTime(new java.util.Date());
        business.setCoordinate(new org.springframework.data.elasticsearch.core.geo.GeoPoint(39.916527, 116.397128)); // 北京坐标
        return business;
    }
}
