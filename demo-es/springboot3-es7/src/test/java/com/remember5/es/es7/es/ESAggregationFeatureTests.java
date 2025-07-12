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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.Stats;
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

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Elasticsearch聚合功能测试
 * 测试各种聚合功能：度量聚合、桶聚合、嵌套聚合、地理聚合
 */
@Slf4j
@DisplayName("ES聚合功能测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("aggregation")
@Tag("advanced")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ESAggregationFeatureTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final String INDEX_NAME = "business_info_agg";
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
                Arrays.asList("茶点", "粤菜"), new GeoPoint(23.129163, 113.264435)),
            createBusiness(4, "成都火锅店", "餐饮服务", "成都市锦江区", 4.9, 1500L, true,
                Arrays.asList("火锅", "川菜"), new GeoPoint(30.572815, 104.066803)),
            createBusiness(5, "西安面馆", "餐饮服务", "西安市雁塔区", 4.4, 600L, false,
                Arrays.asList("面食", "陕菜"), new GeoPoint(34.341568, 108.939840)),
            createBusiness(6, "杭州西湖醋鱼", "餐饮服务", "杭州市西湖区", 4.7, 900L, true,
                Arrays.asList("醋鱼", "浙菜"), new GeoPoint(30.274085, 120.155070)),
            createBusiness(7, "南京盐水鸭", "餐饮服务", "南京市鼓楼区", 4.3, 700L, false,
                Arrays.asList("盐水鸭", "苏菜"), new GeoPoint(32.060255, 118.796877)),
            createBusiness(8, "武汉热干面", "餐饮服务", "武汉市江汉区", 4.2, 500L, false,
                Arrays.asList("热干面", "鄂菜"), new GeoPoint(30.592850, 114.305542))
        );

        elasticsearchRestTemplate.save(businesses, INDEX_COORDINATES);
        log.info("成功创建测试数据，共 {} 个商家", businesses.size());
    }

    @Test
    @Order(2)
    @DisplayName("度量聚合")
    void testMetricsAggregations() {
        // 1. 平均值聚合
        NativeSearchQuery avgQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
            .addAggregation(AggregationBuilders.avg("avg_sales").field("salesVolume"))
            .build();

        SearchHits<EsBusinessInfo> avgResults = elasticsearchRestTemplate.search(avgQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        Aggregations aggregations = avgResults.getAggregations();

        Avg avgRating = aggregations.get("avg_rating");
        Avg avgSales = aggregations.get("avg_sales");

        assertNotNull(avgRating);
        assertNotNull(avgSales);
        log.info("平均评分: {}, 平均销量: {}", avgRating.getValue(), avgSales.getValue());

        // 2. 统计聚合
        NativeSearchQuery statsQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.stats("rating_stats").field("rating"))
            .build();

        SearchHits<EsBusinessInfo> statsResults = elasticsearchRestTemplate.search(statsQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        Stats ratingStats = statsResults.getAggregations().get("rating_stats");

        assertNotNull(ratingStats);
        log.info("评分统计 - 最小值: {}, 最大值: {}, 平均值: {}, 总和: {}, 数量: {}",
            ratingStats.getMin(), ratingStats.getMax(), ratingStats.getAvg(),
            ratingStats.getSum(), ratingStats.getCount());

        // 3. 基数聚合
        NativeSearchQuery cardinalityQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.cardinality("unique_areas").field("area"))
            .build();

        SearchHits<EsBusinessInfo> cardinalityResults = elasticsearchRestTemplate.search(cardinalityQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        Cardinality uniqueAreas = cardinalityResults.getAggregations().get("unique_areas");

        assertNotNull(uniqueAreas);
        log.info("唯一区域数量: {}", uniqueAreas.getValue());
    }

    @Test
    @Order(3)
    @DisplayName("桶聚合")
    void testBucketAggregations() {
        // 1. 术语聚合
        NativeSearchQuery termsQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.terms("business_types").field("businessType"))
            .addAggregation(AggregationBuilders.terms("vip_status").field("isVip"))
            .build();

        SearchHits<EsBusinessInfo> termsResults = elasticsearchRestTemplate.search(termsQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        Aggregations aggregations = termsResults.getAggregations();

        Terms businessTypes = aggregations.get("business_types");
        Terms vipStatus = aggregations.get("vip_status");

        assertNotNull(businessTypes);
        assertNotNull(vipStatus);
        log.info("业务类型分布: {}", businessTypes.getBuckets());
        log.info("VIP状态分布: {}", vipStatus.getBuckets());

        // 2. 范围聚合
        NativeSearchQuery rangeQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.range("rating_ranges")
                .field("rating")
                .addRange("低分", 0, 4.0)
                .addRange("中等", 4.0, 4.5)
                .addRange("高分", 4.5, 5.0))
            .build();

        SearchHits<EsBusinessInfo> rangeResults = elasticsearchRestTemplate.search(rangeQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        Range ratingRanges = rangeResults.getAggregations().get("rating_ranges");

        assertNotNull(ratingRanges);
        log.info("评分范围分布: {}", ratingRanges.getBuckets());

        // 3. 直方图聚合
        NativeSearchQuery histogramQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.histogram("sales_histogram")
                .field("salesVolume")
                .interval(500))
            .build();

        SearchHits<EsBusinessInfo> histogramResults = elasticsearchRestTemplate.search(histogramQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("销量直方图: {}", histogramResults.getAggregations().get("sales_histogram"));
    }

    @Test
    @Order(4)
    @DisplayName("嵌套聚合")
    void testNestedAggregations() {
        // 1. 创建包含嵌套标签的商家
        EsBusinessInfo businessWithTags = createBusinessWithNestedTags(9, "高级餐厅", "餐饮服务", "北京市海淀区", 4.9, 2000L, true);
        elasticsearchRestTemplate.save(businessWithTags, INDEX_COORDINATES);

        // 2. 嵌套聚合
        NativeSearchQuery nestedQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.nested("tags_nested", "tagsDetail")
                .subAggregation(AggregationBuilders.terms("tag_terms").field("tagsDetail.tag")
                    .subAggregation(AggregationBuilders.avg("avg_weight").field("tagsDetail.weight"))))
            .build();

        SearchHits<EsBusinessInfo> nestedResults = elasticsearchRestTemplate.search(nestedQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("嵌套聚合结果: {}", nestedResults.getAggregations().get("tags_nested"));

        // 3. 反向嵌套聚合
        NativeSearchQuery reverseNestedQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.nested("tags_nested", "tagsDetail")
                .subAggregation(AggregationBuilders.terms("tag_terms").field("tagsDetail.tag")
                    .subAggregation(AggregationBuilders.reverseNested("business_count"))))
            .build();

        SearchHits<EsBusinessInfo> reverseNestedResults = elasticsearchRestTemplate.search(reverseNestedQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("反向嵌套聚合结果: {}", reverseNestedResults.getAggregations().get("tags_nested"));
    }

    @Test
    @Order(5)
    @DisplayName("地理聚合")
    void testGeoAggregations() {
        // 1. GeoHash网格聚合
        NativeSearchQuery geoHashQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.geohashGrid("geo_grid")
                .field("coordinate")
                .precision(5))
            .build();

        SearchHits<EsBusinessInfo> geoHashResults = elasticsearchRestTemplate.search(geoHashQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("GeoHash网格聚合结果: {}", geoHashResults.getAggregations().get("geo_grid"));

        // 2. 地理距离聚合
        NativeSearchQuery geoDistanceQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.geoDistance("distance_from_beijing", new org.elasticsearch.common.geo.GeoPoint(39.916527, 116.397128))
                .field("coordinate")
                .unit(org.elasticsearch.common.unit.DistanceUnit.KILOMETERS)
                .addRange("near", 0, 1000)
                .addRange("far", 1000, 2000))
            .build();

        SearchHits<EsBusinessInfo> geoDistanceResults = elasticsearchRestTemplate.search(geoDistanceQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("地理距离聚合结果: {}", geoDistanceResults.getAggregations().get("distance_from_beijing"));
    }

    @Test
    @Order(6)
    @DisplayName("复杂聚合组合")
    void testComplexAggregationCombinations() {
        // 1. 多层级聚合
        NativeSearchQuery complexQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.terms("business_types")
                .field("businessType")
                .subAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                .subAggregation(AggregationBuilders.sum("total_sales").field("salesVolume"))
                .subAggregation(AggregationBuilders.terms("vip_count").field("isVip")))
            .build();

        SearchHits<EsBusinessInfo> complexResults = elasticsearchRestTemplate.search(complexQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("复杂聚合组合结果: {}", complexResults.getAggregations().get("business_types"));

        // 2. 过滤聚合
        NativeSearchQuery filterQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .addAggregation(AggregationBuilders.filter("high_rating_businesses",
                rangeQuery("rating").gte(4.5))
                .subAggregation(AggregationBuilders.avg("avg_sales").field("salesVolume")))
            .build();

        SearchHits<EsBusinessInfo> filterResults = elasticsearchRestTemplate.search(filterQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        log.info("过滤聚合结果: {}", filterResults.getAggregations().get("high_rating_businesses"));
    }

    @Test
    @Order(7)
    @DisplayName("清理测试数据")
    void testCleanup() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES);
        if (indexOperations.exists()) {
            boolean deleted = indexOperations.delete();
            assertTrue(deleted, "索引删除失败");
            log.info("成功删除测试索引: {}", INDEX_NAME);
        }
    }

    /**
     * 创建基础商家数据
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

    /**
     * 创建包含嵌套标签的商家数据
     */
    private EsBusinessInfo createBusinessWithNestedTags(Integer id, String name, String businessType,
                                                        String area, Double rating, Long salesVolume, Boolean isVip) {
        EsBusinessInfo business = createBusiness(id, name, businessType, area, rating, salesVolume, isVip,
            Arrays.asList("高端菜品"), new GeoPoint(39.916527, 116.397128));

        // 添加嵌套标签
        List<EsBusinessInfo.TagInfo> tagsDetail = Arrays.asList(
            EsBusinessInfo.TagInfo.builder().tag("高端").weight(8).description("高端餐厅").build(),
            EsBusinessInfo.TagInfo.builder().tag("精致").weight(7).description("精致菜品").build(),
            EsBusinessInfo.TagInfo.builder().tag("服务").weight(9).description("优质服务").build()
        );
        business.setTagsDetail(tagsDetail);

        return business;
    }
}
