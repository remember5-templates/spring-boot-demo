package com.remember5.es.es7.biz;

import com.remember5.es.es7.entity.EsBusinessInfo;
import com.remember5.es.es7.entity.EsGoodsInfo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 简化电商平台搜索测试
 * 专注于核心搜索功能
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("ecommerce-simple")
class SimpleEcommerceSearchTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 测试数据准备
     */
    @Test
    @Order(1)
    @DisplayName("准备测试数据")
    void prepareTestData() {
        // 创建索引
        if (!elasticsearchRestTemplate.indexOps(EsBusinessInfo.class).exists()) {
            elasticsearchRestTemplate.indexOps(EsBusinessInfo.class).create();
        }
        if (!elasticsearchRestTemplate.indexOps(EsGoodsInfo.class).exists()) {
            elasticsearchRestTemplate.indexOps(EsGoodsInfo.class).create();
        }

        // 插入商家测试数据
        List<EsBusinessInfo> businesses = createBusinessTestData();
        elasticsearchRestTemplate.save(businesses);

        // 插入商品测试数据
        List<EsGoodsInfo> goods = createGoodsTestData();
        elasticsearchRestTemplate.save(goods);

        System.out.println("测试数据准备完成");
    }

    /**
     * 商家搜索测试
     */
    @Test
    @Order(2)
    @DisplayName("商家搜索测试")
    public void testBusinessSearch() {
        System.out.println("\n=== 商家搜索测试 ===");

        // 1. 按商家名称搜索
        testBusinessNameSearch();

        // 2. 按地理位置搜索
        testBusinessLocationSearch();

        // 3. 按商家类型搜索
        testBusinessTypeSearch();

        // 4. 按评分和销量搜索
        testBusinessRatingSalesSearch();
    }

    /**
     * 商品搜索测试
     */
    @Test
    @Order(3)
    @DisplayName("商品搜索测试")
    public void testGoodsSearch() {
        System.out.println("\n=== 商品搜索测试 ===");

        // 1. 按商品名称搜索
        testGoodsNameSearch();

        // 2. 按价格区间搜索
        testGoodsPriceSearch();

        // 3. 按分类搜索
        testGoodsCategorySearch();

        // 4. 按品牌搜索
        testGoodsBrandSearch();
    }

    /**
     * 综合搜索测试
     */
    @Test
    @Order(4)
    @DisplayName("综合搜索测试")
    public void testComprehensiveSearch() {
        System.out.println("\n=== 综合搜索测试 ===");

        // 1. 智能推荐搜索
        testSmartRecommendSearch();

        // 2. 热门搜索
        testHotSearch();

        // 3. 搜索建议
        testSearchSuggest();
    }

    /**
     * 清理测试数据
     */
    @Test
    @Order(5)
    @DisplayName("清理测试数据")
    public void cleanupTestData() {
        // 删除测试数据
        NativeSearchQuery deleteBusinessQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();
        NativeSearchQuery deleteGoodsQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();

        elasticsearchRestTemplate.delete(deleteBusinessQuery, EsBusinessInfo.class);
        elasticsearchRestTemplate.delete(deleteGoodsQuery, EsGoodsInfo.class);
        System.out.println("测试数据清理完成");
    }

    // ==================== 商家搜索测试方法 ====================

    private void testBusinessNameSearch() {
        System.out.println("--- 按商家名称搜索 ---");

        // 搜索包含"餐厅"的商家
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", "餐厅"))
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsBusinessInfo> searchHits = elasticsearchRestTemplate.search(query, EsBusinessInfo.class);
        System.out.println("找到商家数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsBusinessInfo business = hit.getContent();
            System.out.println("商家: " + business.getName() + ", 评分: " + business.getRating());
        });
    }

    private void testBusinessLocationSearch() {
        System.out.println("--- 按地理位置搜索 ---");

        // 搜索北京市的商家
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("city", "北京市"))
                .must(QueryBuilders.termQuery("status", "正常"));

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsBusinessInfo> searchHits = elasticsearchRestTemplate.search(query, EsBusinessInfo.class);
        System.out.println("北京市商家数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsBusinessInfo business = hit.getContent();
            System.out.println("商家: " + business.getName() + ", 地址: " + business.getAddress());
        });
    }

    private void testBusinessTypeSearch() {
        System.out.println("--- 按商家类型搜索 ---");

        // 搜索餐饮类商家
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("businessType", "餐饮"))
                .withSort(SortBuilders.fieldSort("salesVolume").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsBusinessInfo> searchHits = elasticsearchRestTemplate.search(query, EsBusinessInfo.class);
        System.out.println("餐饮类商家数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsBusinessInfo business = hit.getContent();
            System.out.println("商家: " + business.getName() + ", 销量: " + business.getSalesVolume());
        });
    }

    private void testBusinessRatingSalesSearch() {
        System.out.println("--- 按评分和销量搜索 ---");

        // 搜索评分大于4.5且销量大于1000的商家
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("rating").gte(4.5))
                .must(QueryBuilders.rangeQuery("salesVolume").gte(1000));

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("salesVolume").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsBusinessInfo> searchHits = elasticsearchRestTemplate.search(query, EsBusinessInfo.class);
        System.out.println("高评分高销量商家数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsBusinessInfo business = hit.getContent();
            System.out.println("商家: " + business.getName() + ", 评分: " + business.getRating() + ", 销量: " + business.getSalesVolume());
        });
    }

    // ==================== 商品搜索测试方法 ====================

    private void testGoodsNameSearch() {
        System.out.println("--- 按商品名称搜索 ---");

        // 搜索包含"手机"的商品
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", "手机"))
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("salesVolume").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsGoodsInfo> searchHits = elasticsearchRestTemplate.search(query, EsGoodsInfo.class);
        System.out.println("找到商品数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsGoodsInfo goods = hit.getContent();
            System.out.println("商品: " + goods.getName() + ", 价格: " + goods.getCurrentPrice());
        });
    }

    private void testGoodsPriceSearch() {
        System.out.println("--- 按价格区间搜索 ---");

        // 搜索价格在1000-5000之间的商品
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("currentPrice").gte(1000).lte(5000))
                .must(QueryBuilders.termQuery("status", "上架"));

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(SortBuilders.fieldSort("currentPrice").order(SortOrder.ASC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsGoodsInfo> searchHits = elasticsearchRestTemplate.search(query, EsGoodsInfo.class);
        System.out.println("价格区间商品数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsGoodsInfo goods = hit.getContent();
            System.out.println("商品: " + goods.getName() + ", 价格: " + goods.getCurrentPrice());
        });
    }

    private void testGoodsCategorySearch() {
        System.out.println("--- 按分类搜索 ---");

        // 搜索电子产品分类的商品
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("categoryName", "电子产品"))
                .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsGoodsInfo> searchHits = elasticsearchRestTemplate.search(query, EsGoodsInfo.class);
        System.out.println("电子产品数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsGoodsInfo goods = hit.getContent();
            System.out.println("商品: " + goods.getName() + ", 分类: " + goods.getCategoryName());
        });
    }

    private void testGoodsBrandSearch() {
        System.out.println("--- 按品牌搜索 ---");

        // 搜索苹果品牌的商品
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("brand", "苹果"))
                .withSort(SortBuilders.fieldSort("salesVolume").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsGoodsInfo> searchHits = elasticsearchRestTemplate.search(query, EsGoodsInfo.class);
        System.out.println("苹果品牌商品数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsGoodsInfo goods = hit.getContent();
            System.out.println("商品: " + goods.getName() + ", 品牌: " + goods.getBrand());
        });
    }

    // ==================== 综合搜索测试方法 ====================

    private void testSmartRecommendSearch() {
        System.out.println("--- 智能推荐搜索 ---");

        // 基于用户行为的智能推荐（模拟）
        // 搜索高评分、高销量、新品的商品
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.rangeQuery("rating").gte(4.5))
                .must(QueryBuilders.rangeQuery("salesVolume").gte(500))
                .should(QueryBuilders.termQuery("isNew", true))
                .should(QueryBuilders.termQuery("isHot", true))
                .should(QueryBuilders.termQuery("isRecommend", true))
                .minimumShouldMatch(1);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsGoodsInfo> searchHits = elasticsearchRestTemplate.search(query, EsGoodsInfo.class);
        System.out.println("推荐商品数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsGoodsInfo goods = hit.getContent();
            System.out.println("推荐商品: " + goods.getName() + ", 评分: " + goods.getRating() + ", 销量: " + goods.getSalesVolume());
        });
    }

    private void testHotSearch() {
        System.out.println("--- 热门搜索 ---");

        // 搜索热销商品
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("isHot", true))
                .withSort(SortBuilders.fieldSort("monthlySales").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .build();

        SearchHits<EsGoodsInfo> searchHits = elasticsearchRestTemplate.search(query, EsGoodsInfo.class);
        System.out.println("热销商品数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsGoodsInfo goods = hit.getContent();
            System.out.println("热销商品: " + goods.getName() + ", 月销量: " + goods.getMonthlySales());
        });
    }

    private void testSearchSuggest() {
        System.out.println("--- 搜索建议 ---");

        // 搜索建议功能（基于商品名称）
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.prefixQuery("name", "手"))
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<EsGoodsInfo> searchHits = elasticsearchRestTemplate.search(query, EsGoodsInfo.class);
        System.out.println("搜索建议数量: " + searchHits.getTotalHits());
        searchHits.getSearchHits().forEach(hit -> {
            EsGoodsInfo goods = hit.getContent();
            System.out.println("建议: " + goods.getName());
        });
    }

    // ==================== 测试数据创建方法 ====================

    private List<EsBusinessInfo> createBusinessTestData() {
        List<EsBusinessInfo> businesses = new ArrayList<>();

        // 餐饮商家
        businesses.add(EsBusinessInfo.builder()
                .id(1)
                .name("北京烤鸭店")
                .province("北京市")
                .city("北京市")
                .area("朝阳区")
                .street("三里屯街道")
                .address("北京市朝阳区三里屯街道工体北路8号")
                .contactInfo("010-12345678")
                .businessType("餐饮")
                .status("正常")
                .description("正宗北京烤鸭，百年老字号")
                .openTime("09:00-22:00")
                .createTime(new Date())
                .updateTime(new Date())
                .salesVolume(5000L)
                .rating(4.8)
                .reviewCount(1200)
                .tags(Arrays.asList("连锁", "老字号", "24小时"))
                .isVip(true)
                .favoriteCount(800L)
                .businessLevel(5)
                .certificationStatus("已认证")
                .certificationType("企业认证")
                .creditScore(95)
                .servicePromises(Arrays.asList("7天无理由退货", "48小时发货"))
                .paymentMethods(Arrays.asList("支付宝", "微信", "银行卡"))
                .deliveryMethods(Arrays.asList("自提", "快递", "同城配送"))
                .build());

        businesses.add(EsBusinessInfo.builder()
                .id(2)
                .name("苹果专卖店")
                .province("北京市")
                .city("北京市")
                .area("海淀区")
                .street("中关村街道")
                .address("北京市海淀区中关村大街1号")
                .contactInfo("010-87654321")
                .businessType("电子产品")
                .status("正常")
                .description("苹果官方授权专卖店")
                .openTime("10:00-21:00")
                .createTime(new Date())
                .updateTime(new Date())
                .salesVolume(8000L)
                .rating(4.9)
                .reviewCount(2000)
                .tags(Arrays.asList("官方授权", "连锁", "正品保证"))
                .isVip(true)
                .favoriteCount(1500L)
                .businessLevel(5)
                .certificationStatus("已认证")
                .certificationType("企业认证")
                .creditScore(98)
                .servicePromises(Arrays.asList("7天无理由退货", "15天换货"))
                .paymentMethods(Arrays.asList("支付宝", "微信", "银行卡", "分期付款"))
                .deliveryMethods(Arrays.asList("自提", "快递"))
                .build());

        businesses.add(EsBusinessInfo.builder()
                .id(3)
                .name("星巴克咖啡")
                .province("上海市")
                .city("上海市")
                .area("黄浦区")
                .street("南京路街道")
                .address("上海市黄浦区南京东路123号")
                .contactInfo("021-12345678")
                .businessType("餐饮")
                .status("正常")
                .description("星巴克咖啡连锁店")
                .openTime("07:00-23:00")
                .createTime(new Date())
                .updateTime(new Date())
                .salesVolume(3000L)
                .rating(4.6)
                .reviewCount(800)
                .tags(Arrays.asList("连锁", "24小时", "咖啡"))
                .isVip(false)
                .favoriteCount(500L)
                .businessLevel(4)
                .certificationStatus("已认证")
                .certificationType("企业认证")
                .creditScore(88)
                .servicePromises(Arrays.asList("30分钟送达"))
                .paymentMethods(Arrays.asList("支付宝", "微信", "星巴克APP"))
                .deliveryMethods(Arrays.asList("自提", "外卖"))
                .build());

        return businesses;
    }

    private List<EsGoodsInfo> createGoodsTestData() {
        List<EsGoodsInfo> goods = new ArrayList<>();

        // 手机商品
        goods.add(EsGoodsInfo.builder()
                .id(1)
                .businessId(2)
                .name("iPhone 15 Pro Max")
                .subtitle("苹果最新旗舰手机")
                .description("iPhone 15 Pro Max，搭载A17 Pro芯片，钛金属设计")
                .brand("苹果")
                .model("iPhone 15 Pro Max")
                .categoryId(1)
                .categoryName("电子产品")
                .categoryPath(Arrays.asList("电子产品", "手机", "智能手机"))
                .tags(Arrays.asList("新品", "热销", "推荐"))
                .mainImage("https://example.com/iphone15.jpg")
                .images(Arrays.asList("https://example.com/iphone15_1.jpg", "https://example.com/iphone15_2.jpg"))
                .status("上架")
                .goodsType("实物")
                .isNew(true)
                .isHot(true)
                .isRecommend(true)
                .isFlashSale(false)
                .originalPrice(9999.00)
                .currentPrice(8999.00)
                .minPrice(8999.00)
                .maxPrice(8999.00)
                .stock(100)
                .salesVolume(500L)
                .monthlySales(200L)
                .rating(4.9)
                .reviewCount(150)
                .positiveRate(0.98)
                .weight(221.0)
                .volume(1000.0)
                .dimensions("159.9x76.7x8.25")
                .origin("中国")
                .material("钛金属")
                .colors(Arrays.asList("深空黑色", "银色", "金色"))
                .sizes(Arrays.asList("256GB", "512GB", "1TB"))
                .supportReturn(true)
                .supportCod(false)
                .supportInstallment(true)
                .freight(0.00)
                .freeShipping(true)
                .estimatedShippingTime(24)
                .estimatedArrivalTime(3)
                .createTime(new Date())
                .updateTime(new Date())
                .onlineTime(new Date())
                .build());

        goods.add(EsGoodsInfo.builder()
                .id(2)
                .businessId(2)
                .name("MacBook Pro 14英寸")
                .subtitle("专业级笔记本电脑")
                .description("MacBook Pro 14英寸，搭载M3 Pro芯片，专业级性能")
                .brand("苹果")
                .model("MacBook Pro 14")
                .categoryId(2)
                .categoryName("电子产品")
                .categoryPath(Arrays.asList("电子产品", "电脑", "笔记本电脑"))
                .tags(Arrays.asList("热销", "推荐", "专业"))
                .mainImage("https://example.com/macbook.jpg")
                .images(Arrays.asList("https://example.com/macbook_1.jpg", "https://example.com/macbook_2.jpg"))
                .status("上架")
                .goodsType("实物")
                .isNew(false)
                .isHot(true)
                .isRecommend(true)
                .isFlashSale(false)
                .originalPrice(14999.00)
                .currentPrice(13999.00)
                .minPrice(13999.00)
                .maxPrice(13999.00)
                .stock(50)
                .salesVolume(300L)
                .monthlySales(100L)
                .rating(4.8)
                .reviewCount(80)
                .positiveRate(0.95)
                .weight(1600.0)
                .volume(2000.0)
                .dimensions("312.6x221.2x15.5")
                .origin("中国")
                .material("铝合金")
                .colors(Arrays.asList("深空灰色", "银色"))
                .sizes(Arrays.asList("512GB", "1TB", "2TB"))
                .supportReturn(true)
                .supportCod(false)
                .supportInstallment(true)
                .freight(0.00)
                .freeShipping(true)
                .estimatedShippingTime(48)
                .estimatedArrivalTime(5)
                .createTime(new Date())
                .updateTime(new Date())
                .onlineTime(new Date())
                .build());

        goods.add(EsGoodsInfo.builder()
                .id(3)
                .businessId(1)
                .name("北京烤鸭")
                .subtitle("正宗北京烤鸭")
                .description("正宗北京烤鸭，皮酥肉嫩，百年传承工艺")
                .brand("北京烤鸭店")
                .model("传统烤鸭")
                .categoryId(3)
                .categoryName("餐饮")
                .categoryPath(Arrays.asList("餐饮", "中餐", "烤鸭"))
                .tags(Arrays.asList("老字号", "特色", "推荐"))
                .mainImage("https://example.com/duck.jpg")
                .images(Arrays.asList("https://example.com/duck_1.jpg", "https://example.com/duck_2.jpg"))
                .status("上架")
                .goodsType("实物")
                .isNew(false)
                .isHot(true)
                .isRecommend(true)
                .isFlashSale(false)
                .originalPrice(188.00)
                .currentPrice(168.00)
                .minPrice(168.00)
                .maxPrice(168.00)
                .stock(200)
                .salesVolume(1000L)
                .monthlySales(500L)
                .rating(4.7)
                .reviewCount(300)
                .positiveRate(0.92)
                .weight(1500.0)
                .volume(500.0)
                .dimensions("30x20x10")
                .shelfLife(3)
                .origin("北京")
                .material("鸭肉")
                .supportReturn(false)
                .supportCod(true)
                .supportInstallment(false)
                .freight(10.00)
                .freeShipping(false)
                .freeShippingThreshold(200.00)
                .estimatedShippingTime(2)
                .estimatedArrivalTime(1)
                .createTime(new Date())
                .updateTime(new Date())
                .onlineTime(new Date())
                .build());

        return goods;
    }
}
