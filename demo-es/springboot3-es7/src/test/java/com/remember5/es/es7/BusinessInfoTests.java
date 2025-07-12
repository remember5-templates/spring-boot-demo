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
package com.remember5.es.es7;

import com.remember5.es.es7.entity.CdEsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2025/7/11 16:15
 */
@Slf4j
@DisplayName("测试es")
@SpringBootTest(classes = Springboot3Es7Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusinessInfoTests {

    @Autowired
    private ElasticsearchRestTemplate template;


    @Test
    @Order(1)
    @DisplayName("创建索引")
    void createIndex() {
        IndexOperations indexOperations = template.indexOps(CdEsBusinessInfo.class);
        if (!indexOperations.exists()) {
            log.info("创建索引");
            final boolean b = indexOperations.createWithMapping();
            Assertions.assertTrue(b);
        }
    }


    @Test
    @Order(2)
    @DisplayName("写入商家数据")
    void insert() {
        final List<CdEsBusinessInfo> list = Arrays.asList(
                new CdEsBusinessInfo(1, "四川省", "成都市", "高新区", "", "高新区锦城大道666号奥克斯广场C座8层811", new GeoPoint(30.58, 104.06), "筋骨堂热敷推拿按摩（锦城万达店）", "18911111111", "体育培训", "开业"),
                new CdEsBusinessInfo(2, "四川省", "成都市", "高新区", "", "天府大道北端的1736号洲际酒店大堂左侧3楼", new GeoPoint(30.57, 104.06), "泊悦SPA·巴厘式芳香水疗", "18922222222", "体育培训", "开业"),
                new CdEsBusinessInfo(3, "四川省", "成都市", "高新区", "", "新园南三路66号", new GeoPoint(30.59, 104.04), "速达汽车贴膜·隐形车衣·改色膜", "18933333333", "艺术培训", "开业"),
                new CdEsBusinessInfo(4, "四川省", "成都市", "金牛区", "", "交大路246号", new GeoPoint(30.71, 104.04), "V8潮牌·接发烫染连锁（交大店）", "18944444444", "养老服务", "开业"),
                new CdEsBusinessInfo(5, "四川省", "成都市", "金牛区", "", "同友街9号5幢1楼1号", new GeoPoint(30.68, 104.02), "名匠总店（同友路店）", "18955555555", "托育服务", "开业"),
                new CdEsBusinessInfo(6, "四川省", "成都市", "锦江区", "", "狮子山街道菱安路266号附131号一层", new GeoPoint(30.61, 104.12), "听秋美甲美瞳", "18966666666", "艺术培训", "开业"),
                new CdEsBusinessInfo(7, "四川省", "成都市", "锦江区", "", "水杉街126号1层", new GeoPoint(30.61, 104.13), "辰辉造型·护肤（蓝谷地店）", "18977777777", "托育服务", "开业")
        );
        final Iterable<CdEsBusinessInfo> save = template.save(list);
        Assertions.assertNotNull(save);
    }


    @Test
    @Order(3)
    @DisplayName("查询商家数据-所有字段的模糊查询")
    void find() {
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("高新区");
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);
        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);
        search.forEach(hit -> {
            log.info("biz:{}", hit);
        });
    }

    @Test
    @Order(4)
    @DisplayName("查询商家数据-单个字段的模糊查询")
    void find2() {
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("area.keyword", "金牛区");
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);
        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);
        search.forEach(hit -> {
            log.info("biz:{}", hit);
        });
    }

    @Test
    @Order(5)
    @DisplayName("按id升序排序查询")
    void findAndSortById() {
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("*"); // 查询所有
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);
        query.addSort(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Order.asc("id")));
        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);
        search.forEach(hit -> log.info("按id升序排序: {}", hit));
    }

    @Test
    @Order(6)
    @DisplayName("id区间查询（id在3-5之间，包括3和5）")
    void findByIdRange() {
        NativeSearchQuery query = new NativeSearchQuery(QueryBuilders.rangeQuery("id").gte(3).lte(5));
        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);
        search.forEach(hit -> log.info("id区间查询: {}", hit));
    }

    @Test
    @Order(7)
    @DisplayName("按area字段聚合统计数量")
    void aggByArea() {
        NativeSearchQuery query = new NativeSearchQuery(QueryBuilders.matchAllQuery());
        // terms聚合，按area分组统计数量
        query.addAggregation(org.elasticsearch.search.aggregations.AggregationBuilders.terms("area_count").field("area.keyword"));
        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);
        // 获取聚合结果
        org.elasticsearch.search.aggregations.Aggregations aggregations = search.getAggregations();
        if (aggregations != null) {
            org.elasticsearch.search.aggregations.bucket.terms.Terms areaAgg = aggregations.get("area_count");
            if (areaAgg != null) {
                areaAgg.getBuckets().forEach(bucket -> {
                    log.info("area: {} count: {}", bucket.getKeyAsString(), bucket.getDocCount());
                });
            }
        }
    }

    @Test
    @Order(8)
    @DisplayName("高亮查询-匹配关键字高亮显示")
    void highlightQuery() {
        // 创建查询条件
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("高新区");

        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);

        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);

        search.forEach(hit -> {
            log.info("查询结果: {}", hit.getContent());
            // 注意：高亮功能需要额外的配置，这里先展示基本查询
        });
    }

    @Test
    @Order(9)
    @DisplayName("地理距离查询-查询距离指定坐标5km内的商家")
    void geoDistanceQuery() {
        // 指定中心点坐标（成都市中心）
        GeoPoint centerPoint = new GeoPoint(30.58, 104.07);

        // 创建地理距离查询，查询5km内的商家
        NativeSearchQuery query = new NativeSearchQuery(
                QueryBuilders.geoDistanceQuery("coordinate")
                        .point(centerPoint.getLat(), centerPoint.getLon())
                        .distance("5km")
        );

        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);

        search.forEach(hit -> {
            CdEsBusinessInfo business = hit.getContent();
            // 计算实际距离
            double distance = calculateDistance(centerPoint, business.getCoordinate());
            log.info("商家: {}, 距离中心点: {}km", business.getName(), distance);
        });
    }

    @Test
    @Order(10)
    @DisplayName("地理距离排序-按距离排序")
    void geoDistanceSort() {
        GeoPoint centerPoint = new GeoPoint(30.58, 104.07);

        GeoDistanceSortBuilder geoSort = new GeoDistanceSortBuilder("coordinate", centerPoint.getLat(), centerPoint.getLon())
                .order(SortOrder.ASC)
                .unit(DistanceUnit.KILOMETERS);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(geoSort)
                .build();

        final SearchHits<CdEsBusinessInfo> search = template.search(query, CdEsBusinessInfo.class);
        Assertions.assertNotNull(search);

        search.forEach(hit -> {
            CdEsBusinessInfo business = hit.getContent();
            double distance = calculateDistance(centerPoint, business.getCoordinate());
            log.info("商家: {}, 距离: {}km", business.getName(), distance);
        });
    }

    /**
     * 计算两个地理坐标之间的距离（使用Haversine公式）
     *
     * @param point1 第一个坐标点
     * @param point2 第二个坐标点
     * @return 距离（公里）
     */
    private double calculateDistance(GeoPoint point1, GeoPoint point2) {
        final double R = 6371; // 地球半径（公里）

        double lat1 = Math.toRadians(point1.getLat());
        double lat2 = Math.toRadians(point2.getLat());
        double deltaLat = Math.toRadians(point2.getLat() - point1.getLat());
        double deltaLon = Math.toRadians(point2.getLon() - point1.getLon());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }


}
