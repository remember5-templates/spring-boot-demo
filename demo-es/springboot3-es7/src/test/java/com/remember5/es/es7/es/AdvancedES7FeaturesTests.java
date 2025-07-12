package com.remember5.es.es7.es;

import com.remember5.es.es7.Springboot3Es7Application;
import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * ES7高级功能演示测试
 * 包含脚本查询、函数评分、索引别名等高级特性
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@DisplayName("ES7高级功能演示测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
class AdvancedES7FeaturesTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    @DisplayName("1. 脚本查询演示")
    void testScriptQueries() {
        log.info("=== 脚本查询演示 ===");

        // 1.1 脚本查询
        log.info("1.1 脚本查询");
        SearchHits<EsBusinessInfo> scriptQueryResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(scriptQuery(
                                new org.elasticsearch.script.Script(
                                        org.elasticsearch.script.ScriptType.INLINE,
                                        "painless",
                                        "doc['salesVolume'].value > 1000 && doc['rating'].value > 4.5",
                                        null
                                )
                        ))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("脚本查询结果数量: {}", scriptQueryResults.getTotalHits());

        // 1.2 脚本聚合
        log.info("1.2 脚本聚合");
        SearchHits<EsBusinessInfo> scriptAggResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.scriptedMetric("custom_score")
                                .initScript(new org.elasticsearch.script.Script(
                                        org.elasticsearch.script.ScriptType.INLINE,
                                        "painless",
                                        "state.scores = []",
                                        null
                                ))
                                .mapScript(new org.elasticsearch.script.Script(
                                        org.elasticsearch.script.ScriptType.INLINE,
                                        "painless",
                                        "state.scores.add(doc['rating'].value * doc['salesVolume'].value)",
                                        null
                                ))
                                .combineScript(new org.elasticsearch.script.Script(
                                        org.elasticsearch.script.ScriptType.INLINE,
                                        "painless",
                                        "double total = 0; for (score in state.scores) { total += score } return total",
                                        null
                                ))
                                .reduceScript(new org.elasticsearch.script.Script(
                                        org.elasticsearch.script.ScriptType.INLINE,
                                        "painless",
                                        "double total = 0; for (result in states) { total += result } return total",
                                        null
                                )))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("脚本聚合结果: {}", scriptAggResults.getAggregations());
    }

    @Test
    @DisplayName("2. 反向嵌套聚合演示")
    void testReverseNestedAggregations() {
        log.info("=== 反向嵌套聚合演示 ===");

        // 2.1 反向嵌套聚合
        log.info("2.1 反向嵌套聚合");
        SearchHits<EsBusinessInfo> reverseNestedResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .addAggregation(AggregationBuilders.nested("tags_nested", "tagsDetail")
                                .subAggregation(AggregationBuilders.terms("tag_terms").field("tagsDetail.tag")
                                        .subAggregation(AggregationBuilders.reverseNested("business_count"))))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("反向嵌套聚合结果: {}", reverseNestedResults.getAggregations());
    }

    @Test
    @DisplayName("3. 地理网格聚合演示")
    void testGeoGridAggregations() {
        log.info("=== 地理网格聚合演示 ===");

        // 3.1 GeoHash网格聚合
        log.info("3.1 GeoHash网格聚合");
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

        // 3.2 GeoTile网格聚合
        log.info("3.2 GeoTile网格聚合");
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
    @DisplayName("4. 函数评分查询演示")
    void testFunctionScoreQueries() {
        log.info("=== 函数评分查询演示 ===");

        // 4.1 常量评分查询
        log.info("4.1 常量评分查询");
        SearchHits<EsBusinessInfo> constantScoreResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(constantScoreQuery(termQuery("businessType", "餐饮服务")).boost(2.0f))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("常量评分查询结果数量: {}", constantScoreResults.getTotalHits());

        // 4.2 Dis Max查询
        log.info("4.2 Dis Max查询");
        SearchHits<EsBusinessInfo> disMaxResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(disMaxQuery()
                                .add(matchQuery("name", "测试"))
                                .add(matchQuery("address", "测试"))
                                .tieBreaker(0.3f))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("Dis Max查询结果数量: {}", disMaxResults.getTotalHits());
    }

    @Test
    @DisplayName("5. 索引别名管理演示")
    void testIndexAliasManagement() throws Exception {
        log.info("=== 索引别名管理演示 ===");

        String indexName = elasticsearchTemplate.getIndexCoordinatesFor(EsBusinessInfo.class).getIndexName();
        String aliasName = "business_info_alias";

        // 5.1 创建索引别名
        log.info("5.1 创建索引别名");
        IndicesAliasesRequest aliasRequest = new IndicesAliasesRequest();
        aliasRequest.addAliasAction(IndicesAliasesRequest.AliasActions.add()
                .index(indexName)
                .alias(aliasName));

        boolean aliasCreated = restHighLevelClient.indices().updateAliases(aliasRequest, RequestOptions.DEFAULT).isAcknowledged();
        log.info("索引别名创建结果: {}", aliasCreated);

        // 5.2 查询索引别名
        log.info("5.2 查询索引别名");
        GetAliasesRequest getAliasesRequest = new GetAliasesRequest(aliasName);
        boolean aliasExists = restHighLevelClient.indices().existsAlias(getAliasesRequest, RequestOptions.DEFAULT);
        log.info("索引别名是否存在: {}", aliasExists);

        // 5.3 通过别名查询
        log.info("5.3 通过别名查询");
        SearchRequest searchRequest = new SearchRequest(aliasName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        log.info("通过别名查询结果数量: {}", response.getHits().getTotalHits().value);
    }

    @Test
    @DisplayName("6. 高级聚合组合演示")
    void testAdvancedAggregationCombinations() {
        log.info("=== 高级聚合组合演示 ===");

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
                                .subAggregation(AggregationBuilders.sum("total_sales").field("salesVolume")))
                        .build(),
                EsBusinessInfo.class
        );
        log.info("复杂嵌套聚合结果: {}", complexNestedResults.getAggregations());
    }

    @Test
    @DisplayName("7. 性能优化查询演示")
    void testPerformanceOptimizedQueries() {
        log.info("=== 性能优化查询演示 ===");

        // 7.1 字段过滤优化
        log.info("7.1 字段过滤优化");
        SearchHits<EsBusinessInfo> fieldFilterResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withFields("id", "name", "rating") // 只返回指定字段
                        .withMaxResults(10)
                        .build(),
                EsBusinessInfo.class
        );
        log.info("字段过滤查询结果数量: {}", fieldFilterResults.getTotalHits());

        // 7.2 查询缓存
        log.info("7.2 查询缓存");
        SearchHits<EsBusinessInfo> cacheResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .build(),
                EsBusinessInfo.class
        );
        log.info("查询缓存结果数量: {}", cacheResults.getTotalHits());
    }

    @Test
    @DisplayName("8. 监控和诊断演示")
    void testMonitoringAndDiagnostics() {
        log.info("=== 监控和诊断演示 ===");

        // 8.1 索引统计
        log.info("8.1 索引统计");
        SearchHits<EsBusinessInfo> statsResults = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder()
                        .withQuery(matchAllQuery())
                        .withMaxResults(0) // 只获取统计信息
                        .build(),
                EsBusinessInfo.class
        );
        log.info("索引统计 - 总文档数: {}", statsResults.getTotalHits());

        // 8.2 索引健康检查
        log.info("8.2 索引健康检查");
        boolean indexExists = elasticsearchTemplate.indexOps(EsBusinessInfo.class).exists();
        log.info("索引是否存在: {}", indexExists);

        // 8.3 连接测试
        log.info("8.3 连接测试");
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
    }
}
