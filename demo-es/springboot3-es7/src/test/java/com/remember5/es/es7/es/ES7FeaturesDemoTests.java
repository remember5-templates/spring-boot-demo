package com.remember5.es.es7.es;

import com.remember5.es.es7.Springboot3Es7Application;
import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;

import java.util.Arrays;
import java.util.Date;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * ES7功能演示测试
 * 覆盖Elasticsearch的主要特性
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@DisplayName("ES7功能演示测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
class ES7FeaturesDemoTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    @DisplayName("1. 基础查询类型演示")
    void testBasicQueryTypes() {
        log.info("=== 基础查询类型演示 ===");

        // 1.1 术语查询 - 精确匹配
        log.info("1.1 术语查询");
        SearchHits<EsBusinessInfo> termResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(termQuery("businessType", "餐饮服务"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("术语查询结果数量: {}", termResults.getTotalHits());

        // 1.2 多术语查询
        log.info("1.2 多术语查询");
        SearchHits<EsBusinessInfo> termsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(termsQuery("businessType", "餐饮服务", "零售服务"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多术语查询结果数量: {}", termsResults.getTotalHits());

        // 1.3 前缀查询
        log.info("1.3 前缀查询");
        SearchHits<EsBusinessInfo> prefixResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(prefixQuery("name", "测试"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("前缀查询结果数量: {}", prefixResults.getTotalHits());

        // 1.4 通配符查询
        log.info("1.4 通配符查询");
        SearchHits<EsBusinessInfo> wildcardResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(wildcardQuery("name", "*商家*"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("通配符查询结果数量: {}", wildcardResults.getTotalHits());

        // 1.5 正则表达式查询
        log.info("1.5 正则表达式查询");
        SearchHits<EsBusinessInfo> regexpResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(regexpQuery("name", ".*测试.*"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("正则表达式查询结果数量: {}", regexpResults.getTotalHits());

        // 1.6 模糊查询
        log.info("1.6 模糊查询");
        SearchHits<EsBusinessInfo> fuzzyResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(fuzzyQuery("name", "测试商"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("模糊查询结果数量: {}", fuzzyResults.getTotalHits());

        // 1.7 存在性查询
        log.info("1.7 存在性查询");
        SearchHits<EsBusinessInfo> existsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(existsQuery("description"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("存在性查询结果数量: {}", existsResults.getTotalHits());

        // 1.8 多字段查询
        log.info("1.8 多字段查询");
        SearchHits<EsBusinessInfo> multiMatchResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(multiMatchQuery("测试", "name", "address"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多字段查询结果数量: {}", multiMatchResults.getTotalHits());
    }

    @Test
    @DisplayName("2. 高级查询类型演示")
    void testAdvancedQueryTypes() {
        log.info("=== 高级查询类型演示 ===");

        // 2.1 地理距离查询
        log.info("2.1 地理距离查询");
        SearchHits<EsBusinessInfo> geoDistanceResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(geoDistanceQuery("coordinate")
                                .point(30.58, 104.06)
                                .distance("5km"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("地理距离查询结果数量: {}", geoDistanceResults.getTotalHits());

        // 2.2 地理边界框查询
        log.info("2.2 地理边界框查询");
        SearchHits<EsBusinessInfo> geoBoundingBoxResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(geoBoundingBoxQuery("coordinate")
                                .setCorners(30.5, 104.0, 30.6, 104.1))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("地理边界框查询结果数量: {}", geoBoundingBoxResults.getTotalHits());

        // 2.3 范围查询
        log.info("2.3 范围查询");
        SearchHits<EsBusinessInfo> rangeResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(rangeQuery("rating").gte(4.0).lte(5.0))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("范围查询结果数量: {}", rangeResults.getTotalHits());

        // 2.4 复合布尔查询
        log.info("2.4 复合布尔查询");
        SearchHits<EsBusinessInfo> boolResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(boolQuery()
                                .must(termQuery("businessType", "餐饮服务"))
                                .should(rangeQuery("rating").gte(4.5))
                                .filter(termQuery("isVip", true)))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("复合布尔查询结果数量: {}", boolResults.getTotalHits());
    }

    @Test
    @DisplayName("3. 桶聚合演示")
    void testBucketAggregations() {
        log.info("=== 桶聚合演示 ===");

        // 3.1 术语聚合
        log.info("3.1 术语聚合");
        SearchHits<EsBusinessInfo> termsAggResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.terms("business_types").field("businessType"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("术语聚合结果: {}", termsAggResults.getAggregations());

        // 3.2 范围聚合
        log.info("3.2 范围聚合");
        SearchHits<EsBusinessInfo> rangeAggResults = elasticsearchTemplate.search(
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
        log.info("范围聚合结果: {}", rangeAggResults.getAggregations());

        // 3.3 直方图聚合
        log.info("3.3 直方图聚合");
        SearchHits<EsBusinessInfo> histogramAggResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.histogram("sales_histogram")
                                .field("salesVolume")
                                .interval(500))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("直方图聚合结果: {}", histogramAggResults.getAggregations());

        // 3.4 过滤器聚合
        log.info("3.4 过滤器聚合");
        SearchHits<EsBusinessInfo> filterAggResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.filter("vip_businesses", termQuery("isVip", true)))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("过滤器聚合结果: {}", filterAggResults.getAggregations());
    }

    @Test
    @DisplayName("4. 度量聚合演示")
    void testMetricsAggregations() {
        log.info("=== 度量聚合演示 ===");

        // 4.1 基础度量聚合
        log.info("4.1 基础度量聚合");
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

        // 4.2 统计聚合
        log.info("4.2 统计聚合");
        SearchHits<EsBusinessInfo> statsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.stats("rating_stats").field("rating"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("统计聚合结果: {}", statsResults.getAggregations());

        // 4.3 扩展统计聚合
        log.info("4.3 扩展统计聚合");
        SearchHits<EsBusinessInfo> extendedStatsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.extendedStats("rating_extended_stats").field("rating"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("扩展统计聚合结果: {}", extendedStatsResults.getAggregations());

        // 4.4 百分位聚合
        log.info("4.4 百分位聚合");
        SearchHits<EsBusinessInfo> percentileResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.percentiles("rating_percentiles").field("rating"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("百分位聚合结果: {}", percentileResults.getAggregations());

        // 4.5 基数聚合
        log.info("4.5 基数聚合");
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
    @DisplayName("5. 嵌套聚合演示")
    void testNestedAggregations() {
        log.info("=== 嵌套聚合演示 ===");

        // 5.1 嵌套聚合
        log.info("5.1 嵌套聚合");
        SearchHits<EsBusinessInfo> nestedAggResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.terms("business_types")
                                .field("businessType")
                                .subAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                                .subAggregation(AggregationBuilders.sum("total_sales").field("salesVolume"))
                                .subAggregation(AggregationBuilders.terms("areas").field("area")))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("嵌套聚合结果: {}", nestedAggResults.getAggregations());

        // 5.2 多层嵌套聚合
        log.info("5.2 多层嵌套聚合");
        SearchHits<EsBusinessInfo> multiLevelAggResults = elasticsearchTemplate.search(
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
        log.info("多层嵌套聚合结果: {}", multiLevelAggResults.getAggregations());
    }

    @Test
    @DisplayName("6. 建议查询演示")
    void testSuggestQueries() throws Exception {
        log.info("=== 建议查询演示 ===");

        // 6.1 完成建议
        log.info("6.1 完成建议");
        SearchRequest searchRequest = new SearchRequest("business_info");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("business_suggest",
                SuggestBuilders.completionSuggestion("name")
                        .prefix("测试")
                        .size(5));

        searchSourceBuilder.suggest(suggestBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        log.info("完成建议结果: {}", response.getSuggest());
    }

    @Test
    @DisplayName("7. 批量操作演示")
    void testBulkOperations() {
        log.info("=== 批量操作演示 ===");

        // 7.1 批量保存
        log.info("7.1 批量保存");
        EsBusinessInfo business1 = EsBusinessInfo.builder()
                .id(1001)
                .name("批量测试商家1")
                .businessType("餐饮服务")
                .rating(4.5)
                .salesVolume(1000L)
                .createTime(new Date())
                .build();

        EsBusinessInfo business2 = EsBusinessInfo.builder()
                .id(1002)
                .name("批量测试商家2")
                .businessType("零售服务")
                .rating(4.2)
                .salesVolume(800L)
                .createTime(new Date())
                .build();

        Iterable<EsBusinessInfo> savedBusinesses = elasticsearchTemplate.save(Arrays.asList(business1, business2));
        int savedCount = 0;
        for (EsBusinessInfo saved : savedBusinesses) {
            savedCount++;
            log.info("保存商家: ID={}, 名称={}", saved.getId(), saved.getName());
        }
        log.info("批量保存完成，共保存 {} 条数据", savedCount);

        // 7.2 批量更新
        log.info("7.2 批量更新");
        UpdateQuery updateQuery = UpdateQuery.builder("1001")
                .withScript("ctx._source.rating = 4.8")
                .build();
        elasticsearchTemplate.update(updateQuery, elasticsearchTemplate.getIndexCoordinatesFor(EsBusinessInfo.class));
        log.info("批量更新完成");

        // 7.3 批量删除
        log.info("7.3 批量删除");
        elasticsearchTemplate.delete(new NativeSearchQueryBuilder().withQuery(termQuery("id", 1001)).build(), EsBusinessInfo.class);
        elasticsearchTemplate.delete(new NativeSearchQueryBuilder().withQuery(termQuery("id", 1002)).build(), EsBusinessInfo.class);
        log.info("批量删除完成");
    }

    @Test
    @DisplayName("8. 查询优化演示")
    void testQueryOptimization() {
        log.info("=== 查询优化演示 ===");

        // 8.1 字段过滤
        log.info("8.1 字段过滤");
        SearchHits<EsBusinessInfo> fieldFilterResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withFields("id", "name", "rating") // 只返回指定字段
                        .build(),
                EsBusinessInfo.class
        );
        log.info("字段过滤结果数量: {}", fieldFilterResults.getTotalHits());

        // 8.2 排序优化
        log.info("8.2 排序优化");
        SearchHits<EsBusinessInfo> sortOptimizedResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withSort(org.elasticsearch.search.sort.SortBuilders.fieldSort("id").order(org.elasticsearch.search.sort.SortOrder.ASC))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("排序优化结果数量: {}", sortOptimizedResults.getTotalHits());

        // 8.3 分页查询
        log.info("8.3 分页查询");
        SearchHits<EsBusinessInfo> pageResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withPageable(org.springframework.data.domain.PageRequest.of(0, 5))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("分页查询结果数量: {}", pageResults.getTotalHits());
    }

    @Test
    @DisplayName("9. 高亮查询演示")
    void testHighlightQueries() {
        log.info("=== 高亮查询演示 ===");

        // 9.1 基础高亮
        log.info("9.1 基础高亮");
        SearchHits<EsBusinessInfo> highlightResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchQuery("name", "测试"))
                        .withHighlightFields(new org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder.Field("name"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("高亮查询结果数量: {}", highlightResults.getTotalHits());

        // 9.2 多字段高亮
        log.info("9.2 多字段高亮");
        SearchHits<EsBusinessInfo> multiFieldHighlightResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(multiMatchQuery("测试", "name", "address"))
                        .withHighlightFields(
                                new org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder.Field("name"),
                                new org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder.Field("address")
                        )
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多字段高亮查询结果数量: {}", multiFieldHighlightResults.getTotalHits());
    }

    @Test
    @DisplayName("10. 错误处理和监控演示")
    void testErrorHandlingAndMonitoring() {
        log.info("=== 错误处理和监控演示 ===");

        // 10.1 索引健康检查
        log.info("10.1 索引健康检查");
        boolean indexExists = elasticsearchTemplate.indexOps(EsBusinessInfo.class).exists();
        log.info("索引是否存在: {}", indexExists);

        // 10.2 连接测试
        log.info("10.2 连接测试");
        try {
            SearchHits<EsBusinessInfo> connectionTest = elasticsearchTemplate.search(
                    new NativeSearchQueryBuilder()
                            .withQuery(matchAllQuery())
                            .withMaxResults(1)
                            .build(),
                    EsBusinessInfo.class
            );
            log.info("连接测试成功，返回结果数量: {}", connectionTest.getTotalHits());
        } catch (Exception e) {
            log.error("连接测试失败: {}", e.getMessage());
        }

        // 10.3 索引统计
        log.info("10.3 索引统计");
        SearchHits<EsBusinessInfo> statsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withMaxResults(0) // 只获取统计信息，不返回文档
                        .build(),
                EsBusinessInfo.class
        );
        log.info("索引统计 - 总文档数: {}", statsResults.getTotalHits());
    }

    @Test
    @DisplayName("11. 嵌套文档与嵌套聚合演示")
    void testNestedDocumentAndAggregations() {
        log.info("=== 嵌套文档与嵌套聚合演示 ===");
        // 1. 新增带嵌套标签的商家
        EsBusinessInfo.TagInfo tag1 = EsBusinessInfo.TagInfo.builder().tag("连锁").weight(10).description("全国连锁品牌").build();
        EsBusinessInfo.TagInfo tag2 = EsBusinessInfo.TagInfo.builder().tag("24小时").weight(8).description("全天营业").build();
        EsBusinessInfo business = EsBusinessInfo.builder()
                .id(2001)
                .name("嵌套测试商家")
                .tagsDetail(Arrays.asList(tag1, tag2))
                .build();
        elasticsearchTemplate.save(business);
        // 2. 嵌套查询：查找有tag=连锁的商家
        SearchHits<EsBusinessInfo> nestedQueryHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(nestedQuery("tagsDetail", termQuery("tagsDetail.tag", "连锁"), ScoreMode.Avg))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("嵌套查询结果数量: {}", nestedQueryHits.getTotalHits());
        // 3. 嵌套聚合：统计每个标签下的商家数量
        SearchHits<EsBusinessInfo> nestedAggHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(
                                AggregationBuilders.nested("tags_nested", "tagsDetail")
                                        .subAggregation(AggregationBuilders.terms("tag_count").field("tagsDetail.tag"))
                        )
                        .build(),
                EsBusinessInfo.class
        );
        log.info("嵌套聚合结果: {}", nestedAggHits.getAggregations());
    }
}
