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
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Elasticsearch搜索功能测试
 * 测试各种搜索功能：基础搜索、高级搜索、地理位置搜索、嵌套文档搜索、脚本搜索
 */
@Slf4j
@DisplayName("ES搜索功能测试")
@SpringBootTest(classes = Springboot3Es7Application.class)
@Tag("search")
@Tag("advanced")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ESSearchFeatureTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final String INDEX_NAME = "business_info_search";
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
            createBusiness(1, "北京烤鸭店", "正宗北京烤鸭", "北京市朝阳区", 4.8, 1000L, true,
                Arrays.asList("烤鸭", "京菜"), new GeoPoint(39.916527, 116.397128)),
            createBusiness(2, "上海小笼包", "上海特色小笼包", "上海市黄浦区", 4.6, 800L, false,
                Arrays.asList("小笼包", "沪菜"), new GeoPoint(31.230416, 121.473701)),
            createBusiness(3, "广州茶餐厅", "港式茶餐厅", "广州市天河区", 4.5, 1200L, true,
                Arrays.asList("茶点", "粤菜"), new GeoPoint(23.129163, 113.264435)),
            createBusiness(4, "成都火锅店", "正宗川式火锅", "成都市锦江区", 4.9, 1500L, true,
                Arrays.asList("火锅", "川菜"), new GeoPoint(30.572815, 104.066803)),
            createBusiness(5, "西安面馆", "陕西特色面食", "西安市雁塔区", 4.4, 600L, false,
                Arrays.asList("面食", "陕菜"), new GeoPoint(34.341568, 108.939840))
        );

        elasticsearchRestTemplate.save(businesses, INDEX_COORDINATES);
        log.info("成功创建测试数据，共 {} 个商家", businesses.size());
    }

    @Test
    @Order(2)
    @DisplayName("基础搜索")
    void testBasicSearch() {
        // 1. 精确匹配搜索
        NativeSearchQuery exactQuery = new NativeSearchQueryBuilder()
            .withQuery(termQuery("name", "北京烤鸭店"))
            .build();
        SearchHits<EsBusinessInfo> exactResults = elasticsearchRestTemplate.search(exactQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(1, exactResults.getTotalHits());
        log.info("精确匹配搜索找到 {} 个结果", exactResults.getTotalHits());

        // 2. 模糊搜索
        NativeSearchQuery fuzzyQuery = new NativeSearchQueryBuilder()
            .withQuery(fuzzyQuery("name", "烤鸭"))
            .build();
        SearchHits<EsBusinessInfo> fuzzyResults = elasticsearchRestTemplate.search(fuzzyQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(fuzzyResults.getTotalHits() > 0);
        log.info("模糊搜索找到 {} 个结果", fuzzyResults.getTotalHits());

        // 3. 范围搜索
        NativeSearchQuery rangeQuery = new NativeSearchQueryBuilder()
            .withQuery(rangeQuery("rating").gte(4.5).lte(5.0))
            .build();
        SearchHits<EsBusinessInfo> rangeResults = elasticsearchRestTemplate.search(rangeQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(rangeResults.getTotalHits() > 0);
        log.info("范围搜索找到 {} 个结果", rangeResults.getTotalHits());
    }

    @Test
    @Order(3)
    @DisplayName("高级搜索")
    void testAdvancedSearch() {
        // 1. 布尔查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
            .must(matchQuery("description", "特色"))
            .should(termQuery("isVip", true))
            .mustNot(rangeQuery("rating").lt(4.0))
            .filter(rangeQuery("salesVolume").gte(500L));

        NativeSearchQuery advancedQuery = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
            .build();

        SearchHits<EsBusinessInfo> results = elasticsearchRestTemplate.search(advancedQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(results.getTotalHits() > 0);
        log.info("高级搜索找到 {} 个结果", results.getTotalHits());

        // 2. 多字段搜索
        NativeSearchQuery multiFieldQuery = new NativeSearchQueryBuilder()
            .withQuery(multiMatchQuery("特色", "name", "description", "mainProducts"))
            .build();

        SearchHits<EsBusinessInfo> multiFieldResults = elasticsearchRestTemplate.search(multiFieldQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(multiFieldResults.getTotalHits() > 0);
        log.info("多字段搜索找到 {} 个结果", multiFieldResults.getTotalHits());
    }

    @Test
    @Order(4)
    @DisplayName("地理位置搜索")
    void testGeoSearch() {
        // 1. 地理距离查询
        NativeSearchQuery geoDistanceQuery = new NativeSearchQueryBuilder()
            .withQuery(geoDistanceQuery("coordinate")
                .point(39.916527, 116.397128) // 北京坐标
                .distance(1000, DistanceUnit.KILOMETERS))
            .build();

        SearchHits<EsBusinessInfo> geoResults = elasticsearchRestTemplate.search(geoDistanceQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(geoResults.getTotalHits() > 0);
        log.info("地理距离搜索找到 {} 个结果", geoResults.getTotalHits());

        // 2. 地理距离排序
        GeoDistanceSortBuilder geoSort = SortBuilders.geoDistanceSort("coordinate", 39.916527, 116.397128)
            .order(SortOrder.ASC)
            .unit(DistanceUnit.KILOMETERS);

        NativeSearchQuery geoSortQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .withSort(geoSort)
            .build();

        SearchHits<EsBusinessInfo> geoSortResults = elasticsearchRestTemplate.search(geoSortQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(5, geoSortResults.getTotalHits());
        log.info("地理距离排序搜索找到 {} 个结果", geoSortResults.getTotalHits());
    }

    @Test
    @Order(5)
    @DisplayName("嵌套文档搜索")
    void testNestedSearch() {
        // 创建包含嵌套标签的商家
        EsBusinessInfo businessWithTags = createBusinessWithNestedTags(6, "高级餐厅", "高端餐厅", "北京市海淀区", 4.9, 2000L, true);
        elasticsearchRestTemplate.save(businessWithTags, INDEX_COORDINATES);

        // 嵌套查询
        NativeSearchQuery nestedQuery = new NativeSearchQueryBuilder()
            .withQuery(nestedQuery("tagsDetail",
                boolQuery()
                    .must(termQuery("tagsDetail.tag", "高端"))
                    .must(rangeQuery("tagsDetail.weight").gte(5)),
                org.apache.lucene.search.join.ScoreMode.Avg))
            .build();

        SearchHits<EsBusinessInfo> nestedResults = elasticsearchRestTemplate.search(nestedQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(nestedResults.getTotalHits() > 0);
        log.info("嵌套文档搜索找到 {} 个结果", nestedResults.getTotalHits());
    }

    @Test
    @Order(6)
    @DisplayName("脚本搜索")
    void testScriptSearch() {
        // 使用脚本查询计算评分
        NativeSearchQuery scriptQuery = new NativeSearchQueryBuilder()
            .withQuery(scriptQuery(
                new org.elasticsearch.script.Script(
                    org.elasticsearch.script.ScriptType.INLINE,
                    "painless",
                    "doc['rating'].value * 0.7 + doc['salesVolume'].value * 0.0003 > 3.5",
                    null
                )))
            .build();

        SearchHits<EsBusinessInfo> scriptResults = elasticsearchRestTemplate.search(scriptQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(scriptResults.getTotalHits() > 0);
        log.info("脚本搜索找到 {} 个结果", scriptResults.getTotalHits());

        // 函数评分查询
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] functions = {
            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                ScoreFunctionBuilders.fieldValueFactorFunction("rating").factor(1.2f)
            ),
            new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                ScoreFunctionBuilders.fieldValueFactorFunction("salesVolume").factor(0.0001f)
            )
        };

        NativeSearchQuery functionScoreQuery = new NativeSearchQueryBuilder()
            .withQuery(functionScoreQuery(matchAllQuery(), functions))
            .build();

        SearchHits<EsBusinessInfo> functionScoreResults = elasticsearchRestTemplate.search(functionScoreQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(5, functionScoreResults.getTotalHits());
        log.info("函数评分搜索找到 {} 个结果", functionScoreResults.getTotalHits());
    }

    @Test
    @Order(7)
    @DisplayName("高亮搜索")
    void testHighlightSearch() {
        HighlightBuilder highlightBuilder = new HighlightBuilder()
            .field("name").preTags("<em>").postTags("</em>")
            .field("description").preTags("<em>").postTags("</em>")
            .fragmentSize(150)
            .numOfFragments(3);

        NativeSearchQuery highlightQuery = new NativeSearchQueryBuilder()
            .withQuery(matchQuery("description", "特色"))
            .withHighlightBuilder(highlightBuilder)
            .build();

        SearchHits<EsBusinessInfo> highlightResults = elasticsearchRestTemplate.search(highlightQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertTrue(highlightResults.getTotalHits() > 0);
        log.info("高亮搜索找到 {} 个结果", highlightResults.getTotalHits());
    }

    @Test
    @Order(8)
    @DisplayName("分页和排序搜索")
    void testPaginationAndSorting() {
        // 分页查询
        Pageable pageable = PageRequest.of(0, 2);
        NativeSearchQuery pageQuery = new NativeSearchQueryBuilder()
            .withQuery(matchAllQuery())
            .withPageable(pageable)
            .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
            .build();

        SearchHits<EsBusinessInfo> pageResults = elasticsearchRestTemplate.search(pageQuery, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(5, pageResults.getTotalHits()); // 总记录数
        assertTrue(pageResults.getSearchHits().size() <= 2); // 当前页记录数
        log.info("分页搜索：总记录数 {}，当前页记录数 {}", pageResults.getTotalHits(), pageResults.getSearchHits().size());
    }

    @Test
    @Order(9)
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
    private EsBusinessInfo createBusiness(Integer id, String name, String description, String address,
                                          Double rating, Long salesVolume, Boolean isVip,
                                          List<String> mainProducts, GeoPoint coordinate) {
        EsBusinessInfo business = new EsBusinessInfo();
        business.setId(id);
        business.setName(name);
        business.setDescription(description);
        business.setAddress(address);
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
        business.setArea("测试区域");
        business.setTags(Arrays.asList("测试", "示例"));
        return business;
    }

    /**
     * 创建包含嵌套标签的商家数据
     */
    private EsBusinessInfo createBusinessWithNestedTags(Integer id, String name, String description,
                                                        String address, Double rating, Long salesVolume, Boolean isVip) {
        EsBusinessInfo business = createBusiness(id, name, description, address, rating, salesVolume, isVip,
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
