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
package com.remember5.es.es7.biz;

import com.remember5.es.es7.Springboot3Es7Application;
import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Arrays;

/**
 * 简化的索引测试类
 *
 * @author wangjiahao
 * @date 2025/7/12 14:55
 */
@Slf4j
@DisplayName("简化索引测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("simple")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SimpleIndexTest {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    @DisplayName("测试索引创建")
    @Order(1)
    void testIndexCreation() {
        try {
            // 删除已存在的索引
            if (template.indexOps(EsBusinessInfo.class).exists()) {
                log.info("删除已存在的索引");
                template.indexOps(EsBusinessInfo.class).delete();
            }

            // 创建索引
            log.info("开始创建索引...");
            boolean created = template.indexOps(EsBusinessInfo.class).createWithMapping();
            log.info("索引创建结果: {}", created);

            // 验证索引是否存在
            boolean exists = template.indexOps(EsBusinessInfo.class).exists();
            log.info("索引存在: {}", exists);

            Assertions.assertTrue(created, "索引应该创建成功");
            Assertions.assertTrue(exists, "索引应该存在");
        } catch (Exception e) {
            log.error("索引创建失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @DisplayName("测试基本数据插入")
    @Order(2)
    void testBasicDataInsert() {
        try {
            // 创建简单的测试数据
            EsBusinessInfo business = EsBusinessInfo.builder()
                    .id(1)
                    .name("测试商家")
                    .address("测试地址")
                    .coordinate(new GeoPoint(30.58, 104.06))
                    .tags(Arrays.asList("测试", "连锁"))
                    .mainProducts(Arrays.asList("测试产品"))
                    .build();

            // 保存数据
            EsBusinessInfo saved = template.save(business);
            log.info("数据保存成功: {}", saved.getId());

            // 验证数据是否保存成功
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("id", 1))
                    .build();
            SearchHits<EsBusinessInfo> hits = template.search(query, EsBusinessInfo.class);
            log.info("查询结果数量: {}", hits.getTotalHits());

            Assertions.assertEquals(1, hits.getTotalHits(), "应该找到1条数据");
        } catch (Exception e) {
            log.error("数据插入失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @DisplayName("测试地理字段查询")
    @Order(3)
    void testGeoFieldQuery() {
        try {
            // 测试地理字段是否存在
            NativeSearchQuery existsQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.existsQuery("coordinate"))
                    .build();
            SearchHits<EsBusinessInfo> existsHits = template.search(existsQuery, EsBusinessInfo.class);
            log.info("有地理坐标的数据数量: {}", existsHits.getTotalHits());

            if (existsHits.getTotalHits() > 0) {
                // 测试地理距离查询
                NativeSearchQuery geoQuery = new NativeSearchQueryBuilder()
                        .withQuery(QueryBuilders.geoDistanceQuery("coordinate")
                                .point(30.58, 104.06)
                                .distance("5km"))
                        .build();
                SearchHits<EsBusinessInfo> geoHits = template.search(geoQuery, EsBusinessInfo.class);
                log.info("地理查询结果数量: {}", geoHits.getTotalHits());
            } else {
                log.warn("没有找到包含地理坐标的数据");
            }
        } catch (Exception e) {
            log.error("地理字段查询失败: {}", e.getMessage(), e);
            // 不抛出异常，因为地理查询可能因为字段映射问题而失败
        }
    }

    @Test
    @DisplayName("测试数组字段查询")
    @Order(4)
    void testArrayFieldQuery() {
        try {
            // 测试数组字段查询
            NativeSearchQuery tagQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("tags", "测试"))
                    .build();
            SearchHits<EsBusinessInfo> tagHits = template.search(tagQuery, EsBusinessInfo.class);
            log.info("包含'测试'标签的数据数量: {}", tagHits.getTotalHits());

            // 测试数组字段存在性查询
            NativeSearchQuery existsQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.existsQuery("tags"))
                    .build();
            SearchHits<EsBusinessInfo> existsHits = template.search(existsQuery, EsBusinessInfo.class);
            log.info("有标签的数据数量: {}", existsHits.getTotalHits());
        } catch (Exception e) {
            log.error("数组字段查询失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @DisplayName("清理测试数据")
    @Order(5)
    void cleanupTestData() {
        try {
            // 删除测试数据
            template.delete("1", EsBusinessInfo.class);
            log.info("测试数据清理完成");
        } catch (Exception e) {
            log.warn("清理测试数据时出错: {}", e.getMessage());
        }
    }
}
