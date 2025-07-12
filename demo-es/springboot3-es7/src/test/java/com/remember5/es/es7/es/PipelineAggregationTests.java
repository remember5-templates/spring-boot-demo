package com.remember5.es.es7.es;

import com.remember5.demo.springboot3.es7.DemoSpringboot3Es7Application;
import com.remember5.demo.springboot3.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * 管道聚合测试
 * 演示Elasticsearch的各种聚合功能
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@DisplayName("聚合功能测试")
@SpringBootTest(classes = DemoSpringboot3Es7Application.class)
@Tag("aggregation")
@Tag("pipeline")
class PipelineAggregationTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Test
    @DisplayName("1. 基础聚合演示")
    void testBasicAggregations() {
        log.info("=== 基础聚合演示 ===");

        // 1.1 术语聚合
        log.info("1.1 术语聚合");
        SearchHits<EsBusinessInfo> termsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.terms("business_types").field("businessType"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("术语聚合结果: {}", termsResults.getAggregations());

        // 1.2 直方图聚合
        log.info("1.2 直方图聚合");
        SearchHits<EsBusinessInfo> histogramResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.histogram("sales_histogram")
                                .field("salesVolume")
                                .interval(500))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("直方图聚合结果: {}", histogramResults.getAggregations());

        // 1.3 范围聚合
        log.info("1.3 范围聚合");
        SearchHits<EsBusinessInfo> rangeResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.range("rating_ranges")
                                .field("rating")
                                .addRange("低分", 0, 4.0)
                                .addRange("中分", 4.0, 4.5)
                                .addRange("高分", 4.5, 5.0))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("范围聚合结果: {}", rangeResults.getAggregations());
    }

    @Test
    @DisplayName("2. 嵌套聚合演示")
    void testNestedAggregations() {
        log.info("=== 嵌套聚合演示 ===");

        // 2.1 嵌套聚合
        log.info("2.1 嵌套聚合");
        SearchHits<EsBusinessInfo> nestedResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.terms("business_types")
                                .field("businessType")
                                .subAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                                .subAggregation(AggregationBuilders.sum("total_sales").field("salesVolume")))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("嵌套聚合结果: {}", nestedResults.getAggregations());

        // 2.2 多层嵌套聚合
        log.info("2.2 多层嵌套聚合");
        SearchHits<EsBusinessInfo> multiLevelResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.terms("business_types")
                                .field("businessType")
                                .subAggregation(AggregationBuilders.terms("areas")
                                        .field("area")
                                        .subAggregation(AggregationBuilders.avg("avg_rating").field("rating"))))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多层嵌套聚合结果: {}", multiLevelResults.getAggregations());
    }

    @Test
    @DisplayName("3. 度量聚合演示")
    void testMetricsAggregations() {
        log.info("=== 度量聚合演示 ===");

        // 3.1 基础度量聚合
        log.info("3.1 基础度量聚合");
        SearchHits<EsBusinessInfo> metricsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                        .addAggregation(AggregationBuilders.max("max_sales").field("salesVolume"))
                        .addAggregation(AggregationBuilders.min("min_rating").field("rating"))
                        .addAggregation(AggregationBuilders.sum("total_sales").field("salesVolume"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("基础度量聚合结果: {}", metricsResults.getAggregations());

        // 3.2 统计聚合
        log.info("3.2 统计聚合");
        SearchHits<EsBusinessInfo> statsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.stats("rating_stats").field("rating"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("统计聚合结果: {}", statsResults.getAggregations());

        // 3.3 扩展统计聚合
        log.info("3.3 扩展统计聚合");
        SearchHits<EsBusinessInfo> extendedStatsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.extendedStats("rating_extended_stats").field("rating"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("扩展统计聚合结果: {}", extendedStatsResults.getAggregations());

        // 3.4 百分位聚合
        log.info("3.4 百分位聚合");
        SearchHits<EsBusinessInfo> percentileResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.percentiles("rating_percentiles").field("rating"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("百分位聚合结果: {}", percentileResults.getAggregations());

        // 3.5 基数聚合
        log.info("3.5 基数聚合");
        SearchHits<EsBusinessInfo> cardinalityResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.cardinality("unique_business_types").field("businessType"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("基数聚合结果: {}", cardinalityResults.getAggregations());
    }

    @Test
    @DisplayName("4. 地理聚合演示")
    void testGeoAggregations() {
        log.info("=== 地理聚合演示 ===");

        // 4.1 地理距离聚合
        log.info("4.1 地理距离聚合");
        SearchHits<EsBusinessInfo> geoDistanceResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.geoDistance("distance_ranges", new GeoPoint(30.58, 104.06))
                                .field("coordinate")
                                .addRange("近", 0, 5)
                                .addRange("中", 5, 10)
                                .addRange("远", 10, 50))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("地理距离聚合结果: {}", geoDistanceResults.getAggregations());

        // 4.2 GeoHash网格聚合
        log.info("4.2 GeoHash网格聚合");
        SearchHits<EsBusinessInfo> geoHashGridResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.geohashGrid("geo_grid")
                                .field("coordinate")
                                .precision(5))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("GeoHash网格聚合结果: {}", geoHashGridResults.getAggregations());

        // 4.3 GeoTile网格聚合
        log.info("4.3 GeoTile网格聚合");
        SearchHits<EsBusinessInfo> geoTileGridResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.geotileGrid("geo_tile_grid")
                                .field("coordinate")
                                .precision(5))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("GeoTile网格聚合结果: {}", geoTileGridResults.getAggregations());
    }

    @Test
    @DisplayName("5. 过滤器聚合演示")
    void testFilterAggregations() {
        log.info("=== 过滤器聚合演示 ===");

        // 5.1 过滤器聚合
        log.info("5.1 过滤器聚合");
        SearchHits<EsBusinessInfo> filterResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.filter("vip_businesses", termQuery("isVip", true)))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("过滤器聚合结果: {}", filterResults.getAggregations());

        // 5.2 多过滤器聚合
        log.info("5.2 多过滤器聚合");
        SearchHits<EsBusinessInfo> filtersResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.filters("business_filters",
                                termQuery("businessType", "餐饮服务"),
                                termQuery("businessType", "零售服务"),
                                termQuery("isVip", true)))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多过滤器聚合结果: {}", filtersResults.getAggregations());
    }

    @Test
    @DisplayName("6. 复杂聚合组合演示")
    void testComplexAggregationCombinations() {
        log.info("=== 复杂聚合组合演示 ===");

        // 6.1 复杂嵌套聚合
        log.info("6.1 复杂嵌套聚合");
        SearchHits<EsBusinessInfo> complexNestedResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.terms("business_types")
                                .field("businessType")
                                .subAggregation(AggregationBuilders.nested("tags_nested", "tagsDetail")
                                        .subAggregation(AggregationBuilders.terms("tag_terms").field("tagsDetail.tag")
                                                .subAggregation(AggregationBuilders.avg("avg_weight").field("tagsDetail.weight"))))
                                .subAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                                .subAggregation(AggregationBuilders.sum("total_sales").field("salesVolume"))
                                .subAggregation(AggregationBuilders.stats("rating_stats").field("rating")))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("复杂嵌套聚合结果: {}", complexNestedResults.getAggregations());

        // 6.2 多维度分析聚合
        log.info("6.2 多维度分析聚合");
        SearchHits<EsBusinessInfo> multiDimensionalResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.terms("business_types")
                                .field("businessType")
                                .subAggregation(AggregationBuilders.terms("areas")
                                        .field("area")
                                        .subAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                                        .subAggregation(AggregationBuilders.sum("total_sales").field("salesVolume"))))
                        .addAggregation(AggregationBuilders.geoDistance("distance_analysis", new GeoPoint(30.58, 104.06))
                                .field("coordinate")
                                .addRange("近", 0, 5)
                                .addRange("中", 5, 10)
                                .addRange("远", 10, 50)
                                .subAggregation(AggregationBuilders.avg("avg_rating").field("rating")))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多维度分析聚合结果: {}", multiDimensionalResults.getAggregations());
    }
}
