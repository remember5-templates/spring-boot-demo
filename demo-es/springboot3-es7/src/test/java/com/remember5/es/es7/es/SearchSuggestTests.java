package com.remember5.es.es7.es;

import com.remember5.es.es7.Springboot3Es7Application;
import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * 搜索功能测试
 * 演示Elasticsearch的各种搜索功能
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@DisplayName("搜索功能测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("search")
@Tag("query")
class SearchSuggestTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Test
    @DisplayName("1. 模糊搜索演示")
    void testFuzzySearch() {
        log.info("=== 模糊搜索演示 ===");

        // 1.1 基础模糊搜索
        log.info("1.1 基础模糊搜索");
        SearchHits<EsBusinessInfo> fuzzyResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(fuzzyQuery("businessName", "餐饮"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("模糊搜索结果数量: {}", fuzzyResults.getTotalHits());

        // 1.2 带参数的模糊搜索
        log.info("1.2 带参数的模糊搜索");
        SearchHits<EsBusinessInfo> fuzzyWithParamsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(fuzzyQuery("businessName", "餐饮")
                                .prefixLength(2)
                                .maxExpansions(10))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("带参数的模糊搜索结果数量: {}", fuzzyWithParamsResults.getTotalHits());
    }

    @Test
    @DisplayName("2. 通配符搜索演示")
    void testWildcardSearch() {
        log.info("=== 通配符搜索演示 ===");

        // 2.1 前缀搜索
        log.info("2.1 前缀搜索");
        SearchHits<EsBusinessInfo> prefixResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(prefixQuery("businessName", "餐饮"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("前缀搜索结果数量: {}", prefixResults.getTotalHits());

        // 2.2 通配符搜索
        log.info("2.2 通配符搜索");
        SearchHits<EsBusinessInfo> wildcardResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(wildcardQuery("businessName", "*餐饮*"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("通配符搜索结果数量: {}", wildcardResults.getTotalHits());

        // 2.3 正则表达式搜索
        log.info("2.3 正则表达式搜索");
        SearchHits<EsBusinessInfo> regexpResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(regexpQuery("businessName", ".*餐饮.*"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("正则表达式搜索结果数量: {}", regexpResults.getTotalHits());
    }

    @Test
    @DisplayName("3. 多字段搜索演示")
    void testMultiFieldSearch() {
        log.info("=== 多字段搜索演示 ===");

        // 3.1 多字段匹配搜索
        log.info("3.1 多字段匹配搜索");
        SearchHits<EsBusinessInfo> multiMatchResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(multiMatchQuery("餐饮", "businessName", "businessType", "businessDescription"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多字段匹配搜索结果数量: {}", multiMatchResults.getTotalHits());

        // 3.2 跨字段搜索
        log.info("3.2 跨字段搜索");
        SearchHits<EsBusinessInfo> crossFieldResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(multiMatchQuery("餐饮服务", "businessName", "businessType", "businessDescription")
                                .type("cross_fields"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("跨字段搜索结果数量: {}", crossFieldResults.getTotalHits());

        // 3.3 最佳字段搜索
        log.info("3.3 最佳字段搜索");
        SearchHits<EsBusinessInfo> bestFieldsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(multiMatchQuery("餐饮", "businessName", "businessType")
                                .type("best_fields")
                                .tieBreaker(0.3f))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("最佳字段搜索结果数量: {}", bestFieldsResults.getTotalHits());
    }

    @Test
    @DisplayName("4. 复合查询演示")
    void testCompoundQueries() {
        log.info("=== 复合查询演示 ===");

        // 4.1 布尔查询
        log.info("4.1 布尔查询");
        SearchHits<EsBusinessInfo> boolResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(boolQuery()
                                .must(matchQuery("businessType", "餐饮服务"))
                                .should(matchQuery("area", "成都"))
                                .mustNot(matchQuery("isVip", false))
                                .filter(rangeQuery("rating").gte(4.0)))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("布尔查询结果数量: {}", boolResults.getTotalHits());

        // 4.2 常量评分查询
        log.info("4.2 常量评分查询");
        SearchHits<EsBusinessInfo> constantScoreResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(constantScoreQuery(termQuery("isVip", true)).boost(2.0f))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("常量评分查询结果数量: {}", constantScoreResults.getTotalHits());

        // 4.3 范围查询
        log.info("4.3 范围查询");
        SearchHits<EsBusinessInfo> rangeResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(rangeQuery("rating")
                                .gte(4.0)
                                .lte(5.0))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("范围查询结果数量: {}", rangeResults.getTotalHits());
    }

    @Test
    @DisplayName("5. 地理位置搜索演示")
    void testGeoSearch() {
        log.info("=== 地理位置搜索演示 ===");

        // 5.1 地理距离搜索
        log.info("5.1 地理距离搜索");
        SearchHits<EsBusinessInfo> geoDistanceResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(geoDistanceQuery("coordinate")
                                .point(30.58, 104.06)
                                .distance("5km"))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("地理距离搜索结果数量: {}", geoDistanceResults.getTotalHits());

        // 5.2 地理边界框搜索
        log.info("5.2 地理边界框搜索");
        SearchHits<EsBusinessInfo> geoBoundingBoxResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(geoBoundingBoxQuery("coordinate")
                                .setCorners(30.0, 103.0, 31.0, 105.0))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("地理边界框搜索结果数量: {}", geoBoundingBoxResults.getTotalHits());
    }

    @Test
    @DisplayName("6. 嵌套文档搜索演示")
    void testNestedSearch() {
        log.info("=== 嵌套文档搜索演示 ===");

        // 6.1 嵌套查询
        log.info("6.1 嵌套查询");
        SearchHits<EsBusinessInfo> nestedResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(nestedQuery("tagsDetail",
                                boolQuery()
                                        .must(matchQuery("tagsDetail.tag", "热门"))
                                        .must(rangeQuery("tagsDetail.weight").gte(0.8)),
                                ScoreMode.Avg))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("嵌套查询结果数量: {}", nestedResults.getTotalHits());

        // 6.2 嵌套聚合
        log.info("6.2 嵌套聚合");
        SearchHits<EsBusinessInfo> nestedAggResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.nested("tags_nested", "tagsDetail")
                                .subAggregation(AggregationBuilders.terms("tag_terms").field("tagsDetail.tag")))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("嵌套聚合结果: {}", nestedAggResults.getAggregations());
    }

    @Test
    @DisplayName("7. 脚本查询演示")
    void testScriptQueries() {
        log.info("=== 脚本查询演示 ===");

        // 7.1 脚本查询
        log.info("7.1 脚本查询");
        SearchHits<EsBusinessInfo> scriptResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(scriptQuery(
                                new org.elasticsearch.script.Script(
                                        org.elasticsearch.script.ScriptType.INLINE,
                                        "painless",
                                        "doc['rating'].value * doc['salesVolume'].value > 10000",
                                        null)))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("脚本查询结果数量: {}", scriptResults.getTotalHits());
    }

    @Test
    @DisplayName("8. 排序和分页演示")
    void testSortAndPagination() {
        log.info("=== 排序和分页演示 ===");

        // 8.1 基础排序
        log.info("8.1 基础排序");
        SearchHits<EsBusinessInfo> sortResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withSort(org.elasticsearch.search.sort.SortBuilders
                                .fieldSort("rating")
                                .order(org.elasticsearch.search.sort.SortOrder.DESC))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("排序结果数量: {}", sortResults.getTotalHits());

        // 8.2 多字段排序
        log.info("8.2 多字段排序");
        SearchHits<EsBusinessInfo> multiSortResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withSort(org.elasticsearch.search.sort.SortBuilders
                                .fieldSort("rating")
                                .order(org.elasticsearch.search.sort.SortOrder.DESC))
                        .withSort(org.elasticsearch.search.sort.SortBuilders
                                .fieldSort("salesVolume")
                                .order(org.elasticsearch.search.sort.SortOrder.DESC))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("多字段排序结果数量: {}", multiSortResults.getTotalHits());

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
}
