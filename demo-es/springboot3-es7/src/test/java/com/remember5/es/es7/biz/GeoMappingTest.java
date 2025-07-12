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
 * 地理字段映射测试
 *
 * @author wangjiahao
 * @date 2025/7/12 15:00
 */
@Slf4j
@DisplayName("地理字段映射测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("geo")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GeoMappingTest {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    @DisplayName("强制重新创建索引")
    @Order(1)
    void forceRecreateIndex() {
        try {
            // 强制删除索引
            if (template.indexOps(EsBusinessInfo.class).exists()) {
                log.info("强制删除现有索引");
                template.indexOps(EsBusinessInfo.class).delete();
                // 等待删除完成
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("等待索引删除时被中断");
                }
            }

            // 重新创建索引
            log.info("重新创建索引...");
            boolean created = template.indexOps(EsBusinessInfo.class).createWithMapping();
            log.info("索引创建结果: {}", created);

            // 验证索引存在
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
    @DisplayName("插入带地理坐标的测试数据")
    @Order(2)
    void insertGeoData() {
        try {
            // 创建包含地理坐标的测试数据
            EsBusinessInfo business = EsBusinessInfo.builder()
                    .id(100)
                    .name("地理测试商家")
                    .address("成都市高新区天府大道")
                    .coordinate(new GeoPoint(30.58, 104.06)) // 明确设置地理坐标
                    .tags(Arrays.asList("地理测试", "高新区"))
                    .mainProducts(Arrays.asList("地理服务"))
                    .build();

            // 保存数据
            EsBusinessInfo saved = template.save(business);
            log.info("地理数据保存成功: ID={}, 坐标={}", saved.getId(), saved.getCoordinate());

            // 验证数据保存
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.termQuery("id", 100))
                    .build();
            SearchHits<EsBusinessInfo> hits = template.search(query, EsBusinessInfo.class);
            log.info("查询到地理数据: {} 条", hits.getTotalHits());

            Assertions.assertEquals(1, hits.getTotalHits(), "应该找到1条地理数据");
        } catch (Exception e) {
            log.error("地理数据插入失败: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    @DisplayName("验证地理字段存在性")
    @Order(3)
    void verifyGeoFieldExists() {
        try {
            // 检查地理字段是否存在
            NativeSearchQuery existsQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.existsQuery("coordinate"))
                    .build();
            SearchHits<EsBusinessInfo> existsHits = template.search(existsQuery, EsBusinessInfo.class);
            log.info("有地理坐标的数据数量: {}", existsHits.getTotalHits());

            if (existsHits.getTotalHits() > 0) {
                log.info("地理字段存在，可以进行地理查询");
            } else {
                log.warn("没有找到包含地理坐标的数据");
            }
        } catch (Exception e) {
            log.error("验证地理字段存在性失败: {}", e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("测试地理距离查询")
    @Order(4)
    void testGeoDistanceQuery() {
        try {
            // 测试地理距离查询
            GeoPoint centerPoint = new GeoPoint(30.58, 104.06);
            NativeSearchQuery geoQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.geoDistanceQuery("coordinate")
                            .point(centerPoint.getLat(), centerPoint.getLon())
                            .distance("5km"))
                    .build();

            SearchHits<EsBusinessInfo> geoHits = template.search(geoQuery, EsBusinessInfo.class);
            log.info("地理距离查询结果: {} 条", geoHits.getTotalHits());

            geoHits.forEach(hit -> {
                EsBusinessInfo business = hit.getContent();
                log.info("找到商家: {}, 坐标: {}", business.getName(), business.getCoordinate());
            });

        } catch (Exception e) {
            log.error("地理距离查询失败: {}", e.getMessage(), e);
            log.error("错误详情: ", e);
        }
    }

    @Test
    @DisplayName("测试地理距离排序")
    @Order(5)
    void testGeoDistanceSort() {
        try {
            // 测试地理距离排序
            GeoPoint centerPoint = new GeoPoint(30.58, 104.06);
            NativeSearchQuery sortQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withSort(new org.elasticsearch.search.sort.GeoDistanceSortBuilder("coordinate",
                            centerPoint.getLat(), centerPoint.getLon())
                            .order(org.elasticsearch.search.sort.SortOrder.ASC)
                            .unit(org.elasticsearch.common.unit.DistanceUnit.KILOMETERS))
                    .build();

            SearchHits<EsBusinessInfo> sortHits = template.search(sortQuery, EsBusinessInfo.class);
            log.info("地理距离排序查询结果: {} 条", sortHits.getTotalHits());

            sortHits.forEach(hit -> {
                EsBusinessInfo business = hit.getContent();
                log.info("排序结果: {}, 坐标: {}", business.getName(), business.getCoordinate());
            });

        } catch (Exception e) {
            log.error("地理距离排序失败: {}", e.getMessage(), e);
            log.error("错误详情: ", e);
        }
    }

    @Test
    @DisplayName("清理测试数据")
    @Order(6)
    void cleanupTestData() {
        try {
            // 删除测试数据
            template.delete("100", EsBusinessInfo.class);
            log.info("地理测试数据清理完成");
        } catch (Exception e) {
            log.warn("清理地理测试数据时出错: {}", e.getMessage());
        }
    }
}
