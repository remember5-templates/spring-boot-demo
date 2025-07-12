package com.remember5.es.es7.es;

import com.remember5.es.es7.Springboot3Es7Application;
import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Arrays;
import java.util.Date;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * 度量聚合测试
 * 演示Elasticsearch中各种度量聚合的使用
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@DisplayName("度量聚合测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("aggregation")
@Tag("metrics")
class MetricsAggregationTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Test
    @Order(1)
    @DisplayName("准备测试数据")
    void prepareTestData() {
        // 创建测试数据
        createTestData();
        log.info("测试数据准备完成");
    }

    @Test
    @Order(2)
    @DisplayName("测试基础度量聚合 - Avg, Max, Min, Sum")
    void testBasicMetricsAggregations() {
        // 验证数据已准备
        SearchHits<EsBusinessInfo> countHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(),
                EsBusinessInfo.class
        );
        log.info("当前ES中的数据数量: {}", countHits.getTotalHits());

        if (countHits.getTotalHits() == 0) {
            log.warn("没有测试数据，重新创建");
            createTestData();
        }

        // 构建聚合查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .addAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                .addAggregation(AggregationBuilders.max("max_sales").field("salesVolume"))
                .addAggregation(AggregationBuilders.min("min_order_amount").field("minOrderAmount"))
                .addAggregation(AggregationBuilders.sum("total_sales").field("salesVolume"));

        // 执行查询
        SearchHits<EsBusinessInfo> searchHits = elasticsearchTemplate.search(queryBuilder.build(), EsBusinessInfo.class);
        Aggregations aggregations = searchHits.getAggregations();

        // 解析聚合结果
        if (aggregations != null) {
            // 1. 平均评分
            Avg avgRating = aggregations.get("avg_rating");
            log.info("平均评分: {}", avgRating.getValue());

            // 2. 最高销量
            Max maxSales = aggregations.get("max_sales");
            log.info("最高销量: {}", maxSales.getValue());

            // 3. 最低起送金额
            Min minOrderAmount = aggregations.get("min_order_amount");
            log.info("最低起送金额: {}", minOrderAmount.getValue());

            // 4. 总销量
            Sum totalSales = aggregations.get("total_sales");
            log.info("总销量: {}", totalSales.getValue());
        }
    }

    @Test
    @Order(3)
    @DisplayName("测试统计聚合 - Stats")
    void testStatsAggregation() {
        // 验证数据已准备
        SearchHits<EsBusinessInfo> countHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(),
                EsBusinessInfo.class
        );
        if (countHits.getTotalHits() == 0) {
            log.warn("没有测试数据，重新创建");
            createTestData();
        }

        // 构建统计聚合查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .addAggregation(AggregationBuilders.stats("rating_stats").field("rating"))
                .addAggregation(AggregationBuilders.stats("sales_stats").field("salesVolume"));

        // 执行查询
        SearchHits<EsBusinessInfo> searchHits = elasticsearchTemplate.search(queryBuilder.build(), EsBusinessInfo.class);
        Aggregations aggregations = searchHits.getAggregations();

        // 解析统计结果
        if (aggregations != null) {
            // 评分统计
            Stats ratingStats = aggregations.get("rating_stats");
            log.info("评分统计:");
            log.info("  平均值: {}", ratingStats.getAvg());
            log.info("  最大值: {}", ratingStats.getMax());
            log.info("  最小值: {}", ratingStats.getMin());
            log.info("  总和: {}", ratingStats.getSum());
            log.info("  数量: {}", ratingStats.getCount());

            // 销量统计
            Stats salesStats = aggregations.get("sales_stats");
            log.info("销量统计:");
            log.info("  平均值: {}", salesStats.getAvg());
            log.info("  最大值: {}", salesStats.getMax());
            log.info("  最小值: {}", salesStats.getMin());
            log.info("  总和: {}", salesStats.getSum());
            log.info("  数量: {}", salesStats.getCount());
        }
    }

    @Test
    @Order(4)
    @DisplayName("测试基数聚合 - Cardinality")
    void testCardinalityAggregation() {
        // 验证数据已准备
        SearchHits<EsBusinessInfo> countHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(),
                EsBusinessInfo.class
        );
        if (countHits.getTotalHits() == 0) {
            log.warn("没有测试数据，重新创建");
            createTestData();
        }

        // 构建基数聚合查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .addAggregation(AggregationBuilders.cardinality("unique_business_types").field("businessType"))
                .addAggregation(AggregationBuilders.cardinality("unique_areas").field("area"));

        // 执行查询
        SearchHits<EsBusinessInfo> searchHits = elasticsearchTemplate.search(queryBuilder.build(), EsBusinessInfo.class);
        Aggregations aggregations = searchHits.getAggregations();

        // 解析基数结果
        if (aggregations != null) {
            // 不同商家类型数量
            Cardinality uniqueBusinessTypes = aggregations.get("unique_business_types");
            log.info("不同商家类型数量: {}", uniqueBusinessTypes.getValue());

            // 不同区域数量
            Cardinality uniqueAreas = aggregations.get("unique_areas");
            log.info("不同区域数量: {}", uniqueAreas.getValue());
        }
    }

    @Test
    @Order(5)
    @DisplayName("测试条件聚合 - 按条件分组统计")
    void testConditionalAggregations() {
        // 验证数据已准备
        SearchHits<EsBusinessInfo> countHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(),
                EsBusinessInfo.class
        );
        if (countHits.getTotalHits() == 0) {
            log.warn("没有测试数据，重新创建");
            createTestData();
        }

        // 构建条件聚合查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .addAggregation(AggregationBuilders
                        .filter("vip_businesses",
                                org.elasticsearch.index.query.QueryBuilders.termQuery("isVip", true))
                        .subAggregation(AggregationBuilders.avg("vip_avg_rating").field("rating"))
                        .subAggregation(AggregationBuilders.sum("vip_total_sales").field("salesVolume")))
                .addAggregation(AggregationBuilders
                        .filter("delivery_businesses",
                                org.elasticsearch.index.query.QueryBuilders.termQuery("deliverySupport", true))
                        .subAggregation(AggregationBuilders.avg("delivery_avg_rating").field("rating"))
                        .subAggregation(AggregationBuilders.min("delivery_min_order").field("minOrderAmount")));

        // 执行查询
        SearchHits<EsBusinessInfo> searchHits = elasticsearchTemplate.search(queryBuilder.build(), EsBusinessInfo.class);
        Aggregations aggregations = searchHits.getAggregations();

        // 解析条件聚合结果
        if (aggregations != null) {
            // VIP商家统计
            org.elasticsearch.search.aggregations.bucket.filter.Filter vipFilter =
                    aggregations.get("vip_businesses");
            log.info("VIP商家统计:");
            log.info("  VIP商家数量: {}", vipFilter.getDocCount());

            Avg vipAvgRating = vipFilter.getAggregations().get("vip_avg_rating");
            log.info("  VIP商家平均评分: {}", vipAvgRating.getValue());

            Sum vipTotalSales = vipFilter.getAggregations().get("vip_total_sales");
            log.info("  VIP商家总销量: {}", vipTotalSales.getValue());

            // 支持配送商家统计
            org.elasticsearch.search.aggregations.bucket.filter.Filter deliveryFilter =
                    aggregations.get("delivery_businesses");
            log.info("支持配送商家统计:");
            log.info("  支持配送商家数量: {}", deliveryFilter.getDocCount());

            Avg deliveryAvgRating = deliveryFilter.getAggregations().get("delivery_avg_rating");
            log.info("  支持配送商家平均评分: {}", deliveryAvgRating.getValue());

            Min deliveryMinOrder = deliveryFilter.getAggregations().get("delivery_min_order");
            log.info("  支持配送商家最低起送金额: {}", deliveryMinOrder.getValue());
        }
    }

    @Test
    @Order(6)
    @DisplayName("测试多字段聚合 - 综合统计")
    void testMultiFieldAggregations() {
        // 验证数据已准备
        SearchHits<EsBusinessInfo> countHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(),
                EsBusinessInfo.class
        );
        if (countHits.getTotalHits() == 0) {
            log.warn("没有测试数据，重新创建");
            createTestData();
        }

        // 构建多字段聚合查询
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .addAggregation(AggregationBuilders
                        .avg("avg_rating").field("rating"))
                .addAggregation(AggregationBuilders
                        .avg("avg_sales").field("salesVolume"))
                .addAggregation(AggregationBuilders
                        .avg("avg_review_count").field("reviewCount"))
                .addAggregation(AggregationBuilders
                        .avg("avg_favorite_count").field("favoriteCount"))
                .addAggregation(AggregationBuilders
                        .avg("avg_delivery_time").field("avgDeliveryTime"));

        // 执行查询
        SearchHits<EsBusinessInfo> searchHits = elasticsearchTemplate.search(queryBuilder.build(), EsBusinessInfo.class);
        Aggregations aggregations = searchHits.getAggregations();

        // 解析多字段聚合结果
        if (aggregations != null) {
            log.info("商家综合统计:");

            Avg avgRating = aggregations.get("avg_rating");
            log.info("  平均评分: {}", avgRating.getValue());

            Avg avgSales = aggregations.get("avg_sales");
            log.info("  平均销量: {}", avgSales.getValue());

            Avg avgReviewCount = aggregations.get("avg_review_count");
            log.info("  平均评价数: {}", avgReviewCount.getValue());

            Avg avgFavoriteCount = aggregations.get("avg_favorite_count");
            log.info("  平均收藏数: {}", avgFavoriteCount.getValue());

            Avg avgDeliveryTime = aggregations.get("avg_delivery_time");
            log.info("  平均配送时间: {} 分钟", avgDeliveryTime.getValue());
        }
    }

    @Test
    @Order(7)
    @DisplayName("清理测试数据")
    void cleanupTestData() {
        // 清理测试数据
        elasticsearchTemplate.delete(new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(), EsBusinessInfo.class);
        log.info("测试数据清理完成");
    }

    /**
     * 创建测试数据
     */
    private void createTestData() {
        // 清理现有数据
        elasticsearchTemplate.delete(new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(), EsBusinessInfo.class);

        // 创建测试商家数据
        EsBusinessInfo business1 = EsBusinessInfo.builder()
                .id(1)
                .name("川菜馆")
                .businessType("餐饮服务")
                .area("高新区")
                .rating(4.5)
                .salesVolume(1000L)
                .reviewCount(200)
                .favoriteCount(150L)
                .isVip(true)
                .deliverySupport(true)
                .minOrderAmount(20.0)
                .avgDeliveryTime(30)
                .createTime(new Date())
                .build();

        EsBusinessInfo business2 = EsBusinessInfo.builder()
                .id(2)
                .name("火锅店")
                .businessType("餐饮服务")
                .area("锦江区")
                .rating(4.8)
                .salesVolume(2000L)
                .reviewCount(500)
                .favoriteCount(300L)
                .isVip(true)
                .deliverySupport(true)
                .minOrderAmount(50.0)
                .avgDeliveryTime(45)
                .createTime(new Date())
                .build();

        EsBusinessInfo business3 = EsBusinessInfo.builder()
                .id(3)
                .name("服装店")
                .businessType("零售服务")
                .area("武侯区")
                .rating(4.2)
                .salesVolume(500L)
                .reviewCount(100)
                .favoriteCount(80L)
                .isVip(false)
                .deliverySupport(false)
                .minOrderAmount(0.0)
                .avgDeliveryTime(0)
                .createTime(new Date())
                .build();

        EsBusinessInfo business4 = EsBusinessInfo.builder()
                .id(4)
                .name("咖啡厅")
                .businessType("餐饮服务")
                .area("高新区")
                .rating(4.6)
                .salesVolume(800L)
                .reviewCount(150)
                .favoriteCount(120L)
                .isVip(false)
                .deliverySupport(true)
                .minOrderAmount(15.0)
                .avgDeliveryTime(25)
                .createTime(new Date())
                .build();

        // 保存到ES
        Iterable<EsBusinessInfo> savedBusinesses = elasticsearchTemplate.save(Arrays.asList(business1, business2, business3, business4));

        // 验证数据是否保存成功
        int savedCount = 0;
        for (EsBusinessInfo saved : savedBusinesses) {
            savedCount++;
            log.info("保存商家: ID={}, 名称={}", saved.getId(), saved.getName());
        }

        log.info("测试数据创建完成，共保存 {} 条数据", savedCount);

        // 等待索引刷新
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
