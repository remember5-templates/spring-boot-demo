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
package com.remember5.demo.springboot3.es7;

import com.remember5.demo.springboot3.es7.entity.ESBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.range.Range;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangjiahao
 * @date 2025/7/11 16:15
 */
@Slf4j
@DisplayName("测试es")
@SpringBootTest(classes = DemoSpringboot3Es7Application.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusinessInfoTests {

    @Autowired
    private ElasticsearchRestTemplate template;

    @Test
    @Order(1)
    @DisplayName("创建索引")
    void createIndex() {
        IndexOperations indexOperations = template.indexOps(ESBusinessInfo.class);
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
        final List<ESBusinessInfo> list = Arrays.asList(
                ESBusinessInfo.builder().id(1).province("四川省").city("成都市").area("高新区").address("高新区锦城大道666号奥克斯广场C座8层811").coordinate(new GeoPoint(30.58, 104.06)).name("筋骨堂热敷推拿按摩（锦城万达店）").contactInfo("18911111111").businessType("体育培训").status("开业").salesVolume(1000L).rating(4.8).reviewCount(120).isVip(true).favoriteCount(500L).tags(Arrays.asList("连锁", "24小时")).mainProducts(Arrays.asList("按摩", "理疗")).deliverySupport(true).minOrderAmount(20.0).avgDeliveryTime(30).createTime(new java.util.Date()).build(),
                ESBusinessInfo.builder().id(2).province("四川省").city("成都市").area("高新区").address("天府大道北端的1736号洲际酒店大堂左侧3楼").coordinate(new GeoPoint(30.57, 104.06)).name("泊悦SPA·巴厘式芳香水疗").contactInfo("18922222222").businessType("体育培训").status("开业").salesVolume(800L).rating(4.5).reviewCount(80).isVip(false).favoriteCount(200L).tags(Arrays.asList("高端", "SPA")).mainProducts(Arrays.asList("SPA", "按摩")).deliverySupport(true).minOrderAmount(50.0).avgDeliveryTime(40).createTime(new java.util.Date()).build(),
                ESBusinessInfo.builder().id(3).province("四川省").city("成都市").area("高新区").address("新园南三路66号").coordinate(new GeoPoint(30.59, 104.04)).name("速达汽车贴膜·隐形车衣·改色膜").contactInfo("18933333333").businessType("艺术培训").status("开业").salesVolume(300L).rating(4.2).reviewCount(30).isVip(false).favoriteCount(50L).tags(Arrays.asList("汽车", "贴膜")).mainProducts(Arrays.asList("贴膜", "车衣")).deliverySupport(false).minOrderAmount(0.0).avgDeliveryTime(0).createTime(new java.util.Date()).build(),
                ESBusinessInfo.builder().id(4).province("四川省").city("成都市").area("金牛区").address("交大路246号").coordinate(new GeoPoint(30.71, 104.04)).name("V8潮牌·接发烫染连锁（交大店）").contactInfo("18944444444").businessType("养老服务").status("开业").salesVolume(500L).rating(4.0).reviewCount(60).isVip(true).favoriteCount(300L).tags(Arrays.asList("连锁", "美发")).mainProducts(Arrays.asList("接发", "烫染")).deliverySupport(false).minOrderAmount(0.0).avgDeliveryTime(0).createTime(new java.util.Date()).build(),
                ESBusinessInfo.builder().id(5).province("四川省").city("成都市").area("金牛区").address("同友街9号5幢1楼1号").coordinate(new GeoPoint(30.68, 104.02)).name("名匠总店（同友路店）").contactInfo("18955555555").businessType("托育服务").status("开业").salesVolume(200L).rating(3.8).reviewCount(20).isVip(false).favoriteCount(80L).tags(Arrays.asList("托育", "亲子")).mainProducts(Arrays.asList("托育", "亲子活动")).deliverySupport(false).minOrderAmount(0.0).avgDeliveryTime(0).createTime(new java.util.Date()).build(),
                ESBusinessInfo.builder().id(6).province("四川省").city("成都市").area("锦江区").address("狮子山街道菱安路266号附131号一层").coordinate(new GeoPoint(30.61, 104.12)).name("听秋美甲美瞳").contactInfo("18966666666").businessType("艺术培训").status("开业").salesVolume(400L).rating(4.6).reviewCount(40).isVip(false).favoriteCount(120L).tags(Arrays.asList("美甲", "美瞳")).mainProducts(Arrays.asList("美甲", "美瞳")).deliverySupport(true).minOrderAmount(30.0).avgDeliveryTime(25).createTime(new java.util.Date()).build(),
                ESBusinessInfo.builder().id(7).province("四川省").city("成都市").area("锦江区").address("水杉街126号1层").coordinate(new GeoPoint(30.61, 104.13)).name("辰辉造型·护肤（蓝谷地店）").contactInfo("18977777777").businessType("托育服务").status("开业").salesVolume(150L).rating(3.9).reviewCount(10).isVip(false).favoriteCount(30L).tags(Arrays.asList("护肤", "造型")).mainProducts(Arrays.asList("护肤", "造型")).deliverySupport(false).minOrderAmount(0.0).avgDeliveryTime(0).createTime(new java.util.Date()).build()
        );
        final Iterable<ESBusinessInfo> save = template.save(list);
        Assertions.assertNotNull(save);
    }

    // ==================== 基础查询 ====================
    @Test
    @Order(3)
    @DisplayName("查询所有商家")
    void findAll() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        log.info("总商家数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("商家: {}", hit.getContent().getName()));
    }

    @Test
    @Order(4)
    @DisplayName("按ID精确查询")
    void findById() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("id", 1))
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        searchHits.forEach(hit -> log.info("按ID查询: {}", hit.getContent()));
    }

    // ==================== 中文分词查询 ====================
    @Test
    @Order(5)
    @DisplayName("中文分词查询-商家名称")
    void chineseNameQuery() {
        // 测试IK分词效果
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", "按摩"))
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        log.info("包含'按摩'的商家数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("按摩相关商家: {}", hit.getContent().getName()));
    }

    @Test
    @Order(6)
    @DisplayName("中文分词查询-地址")
    void chineseAddressQuery() {
        // 测试地址分词
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("address", "高新区"))
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        log.info("高新区商家数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("高新区商家: {} - {}", hit.getContent().getName(), hit.getContent().getAddress()));
    }

    @Test
    @Order(7)
    @DisplayName("中文分词查询-商家描述")
    void chineseDescriptionQuery() {
        // 测试描述分词（需要先更新数据添加描述）
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("description", "专业"))
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        log.info("包含'专业'描述的商家数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("专业商家: {}", hit.getContent().getName()));
    }

    @Test
    @Order(8)
    @DisplayName("多字段中文分词查询")
    void multiFieldChineseQuery() {
        // 在名称和地址中搜索"连锁"
        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery("连锁")
                .field("name", 2.0f)
                .field("address", 1.0f)
                .type(MultiMatchQueryBuilder.Type.BEST_FIELDS);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery)
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        log.info("包含'连锁'的商家数量: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("连锁商家: {} (得分: {})", hit.getContent().getName(), hit.getScore()));
    }

    // ==================== 排序和分页 ====================
    @Test
    @Order(9)
    @DisplayName("按ID升序排序查询")
    void findAndSortById() {
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery("*");
        NativeSearchQuery query = new NativeSearchQuery(queryBuilder);
        query.addSort(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Order.asc("id")));
        final SearchHits<ESBusinessInfo> search = template.search(query, ESBusinessInfo.class);
        Assertions.assertNotNull(search);
        search.forEach(hit -> log.info("按id升序排序: {}", hit));
    }

    @Test
    @Order(10)
    @DisplayName("分页查询")
    void paginationQuery() {
        Pageable pageable = PageRequest.of(0, 2);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withPageable(pageable)
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        log.info("总记录数: {}", searchHits.getTotalHits());
        searchHits.forEach(hit -> log.info("分页结果: {}", hit.getContent()));
    }

    // ==================== 聚合查询 ====================
    @Test
    @Order(11)
    @DisplayName("按area字段聚合统计数量")
    void aggByArea() {
        NativeSearchQuery query = new NativeSearchQuery(QueryBuilders.matchAllQuery());
        query.addAggregation(AggregationBuilders.terms("area_count").field("area.keyword"));
        final SearchHits<ESBusinessInfo> search = template.search(query, ESBusinessInfo.class);
        Assertions.assertNotNull(search);
        if (search.getAggregations() != null) {
            Terms areaAgg = search.getAggregations().get("area_count");
            if (areaAgg != null) {
                areaAgg.getBuckets().forEach(bucket -> {
                    log.info("area: {} count: {}", bucket.getKeyAsString(), bucket.getDocCount());
                });
            }
        }
    }

    @Test
    @Order(12)
    @DisplayName("按评分区间聚合")
    void ratingRangeAggregation() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .addAggregation(AggregationBuilders.range("rating_ranges")
                        .field("rating")
                        .addRange("低于3分", Double.NEGATIVE_INFINITY, 3)
                        .addRange("3-4分", 3, 4)
                        .addRange("4-4.5分", 4, 4.5)
                        .addRange("高于4.5分", 4.5, Double.POSITIVE_INFINITY))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        if (hits.getAggregations() != null) {
            Range rangeAgg = hits.getAggregations().get("rating_ranges");
            rangeAgg.getBuckets().forEach(bucket -> {
                log.info("评分区间 {}: {} 个商家", bucket.getKeyAsString(), bucket.getDocCount());
            });
        }
    }

    // ==================== 地理查询 ====================
    @Test
    @Order(13)
    @DisplayName("地理距离查询-查询距离指定坐标5km内的商家")
    void geoDistanceQuery() {
        GeoPoint centerPoint = new GeoPoint(30.58, 104.07);
        NativeSearchQuery query = new NativeSearchQuery(
                QueryBuilders.geoDistanceQuery("coordinate")
                        .point(centerPoint.getLat(), centerPoint.getLon())
                        .distance("5km")
        );
        final SearchHits<ESBusinessInfo> search = template.search(query, ESBusinessInfo.class);
        Assertions.assertNotNull(search);
        search.forEach(hit -> {
            ESBusinessInfo business = hit.getContent();
            double distance = calculateDistance(centerPoint, business.getCoordinate());
            log.info("商家: {}, 距离中心点: {}km", business.getName(), distance);
        });
    }

    @Test
    @Order(14)
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
        final SearchHits<ESBusinessInfo> search = template.search(query, ESBusinessInfo.class);
        Assertions.assertNotNull(search);
        search.forEach(hit -> {
            ESBusinessInfo business = hit.getContent();
            double distance = calculateDistance(centerPoint, business.getCoordinate());
            log.info("商家: {}, 距离: {}km", business.getName(), distance);
        });
    }

    // ==================== 高级查询 ====================
    @Test
    @Order(15)
    @DisplayName("复合查询-多条件组合")
    void complexQuery() {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("businessType", "体育培训"))
                .should(QueryBuilders.matchQuery("area", "高新区"))
                .mustNot(QueryBuilders.matchQuery("status", "停业"));
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        searchHits.forEach(hit -> log.info("复合查询结果: {}", hit.getContent()));
    }

    @Test
    @Order(16)
    @DisplayName("高亮查询-完整实现")
    void highlightQueryComplete() {
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("name")
                .field("address")
                .preTags("<em>")
                .postTags("</em>")
                .fragmentSize(150)
                .numOfFragments(3);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("高新区"))
                .withHighlightBuilder(highlightBuilder)
                .build();
        SearchHits<ESBusinessInfo> searchHits = template.search(query, ESBusinessInfo.class);
        searchHits.forEach(hit -> {
            log.info("文档: {}", hit.getContent());
            hit.getHighlightFields().forEach((field, fragments) -> {
                log.info("字段: {}, 高亮: {}", field, fragments);
            });
        });
    }

    // ==================== 业务场景查询 ====================
    @Test
    @Order(17)
    @DisplayName("按销量排序查询")
    void sortBySalesVolume() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(new org.elasticsearch.search.sort.FieldSortBuilder("salesVolume").order(SortOrder.DESC))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("销量排序: {} - {}", hit.getContent().getName(), hit.getContent().getSalesVolume()));
    }

    @Test
    @Order(18)
    @DisplayName("查询VIP商家")
    void findVipShops() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("isVip", true))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("VIP商家: {}", hit.getContent().getName()));
    }

    @Test
    @Order(19)
    @DisplayName("查询支持配送且起送金额低于X的商家")
    void findDeliveryAndMinOrder() {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("deliverySupport", true))
                .must(QueryBuilders.rangeQuery("minOrderAmount").lt(30));
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("支持配送且起送金额低于30元: {} - {}元", hit.getContent().getName(), hit.getContent().getMinOrderAmount()));
    }

    @Test
    @Order(20)
    @DisplayName("查询某地理范围内销量最高的商家")
    void findTopSalesInGeoRange() {
        GeoPoint center = new GeoPoint(30.58, 104.07);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.geoDistanceQuery("coordinate").point(center.getLat(), center.getLon()).distance("5km"))
                .withSort(new org.elasticsearch.search.sort.FieldSortBuilder("salesVolume").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 1))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("5km内销量最高: {} - {}", hit.getContent().getName(), hit.getContent().getSalesVolume()));
    }

    @Test
    @Order(21)
    @DisplayName("查询新入驻商家（按createTime倒序）")
    void findNewestShops() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(new org.elasticsearch.search.sort.FieldSortBuilder("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 5))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("新入驻商家: {} - 入驻时间:{}", hit.getContent().getName(), hit.getContent().getCreateTime()));
    }

    @Test
    @Order(22)
    @DisplayName("查询评价数最多的商家")
    void findMostReviewedShop() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(new org.elasticsearch.search.sort.FieldSortBuilder("reviewCount").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 1))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("评价数最多: {} - {}条", hit.getContent().getName(), hit.getContent().getReviewCount()));
    }

    @Test
    @Order(23)
    @DisplayName("查询主营某商品的商家")
    void findByMainProduct() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("mainProducts", "按摩"))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("主营按摩: {}", hit.getContent().getName()));
    }

    @Test
    @Order(24)
    @DisplayName("查询收藏数前N的商家")
    void findTopFavoriteShops() {
        int N = 3;
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .withSort(new org.elasticsearch.search.sort.FieldSortBuilder("favoriteCount").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, N))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("收藏数前{}: {} - {}次", N, hit.getContent().getName(), hit.getContent().getFavoriteCount()));
    }

    @Test
    @Order(25)
    @DisplayName("按标签筛选商家")
    void findByTag() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("tags", "连锁"))
                .build();
        SearchHits<ESBusinessInfo> hits = template.search(query, ESBusinessInfo.class);
        hits.forEach(hit -> log.info("连锁标签商家: {}", hit.getContent().getName()));
    }

    // ==================== 清理操作 ====================
    @Test
    @Order(26)
    @DisplayName("清理测试数据")
    void cleanupTestData() {
        try {
            // 逐个删除已知的测试数据ID，避免查询可能的问题
            List<String> testIds = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9");
            int deletedCount = 0;

            for (String id : testIds) {
                try {
                    template.delete(id, IndexCoordinates.of("business_info"));
                    deletedCount++;
                    log.debug("删除了ID为 {} 的测试数据", id);
                } catch (Exception e) {
                    log.debug("删除ID为 {} 的数据时失败（可能不存在）: {}", id, e.getMessage());
                }
            }

            log.info("清理测试数据完成，成功删除 {} 条数据", deletedCount);
        } catch (Exception e) {
            log.error("清理测试数据时发生错误: {}", e.getMessage());
            // 不抛出异常，避免影响其他测试
        }
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
