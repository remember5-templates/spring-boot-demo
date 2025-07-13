package com.remember5.springboot3.es8.es;

import com.remember5.springboot3.es8.entity.BurgerKingMerchant;
import com.remember5.springboot3.es8.repository.BurgerKingMerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;

/**
 * 汉堡王商户搜索功能测试
 * 
 * @author wangjiahao
 * @date 2025/1/8
 */
@Slf4j
@DisplayName("汉堡王商户搜索功能测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BurgerKingMerchantSearchTest {

    @Autowired
    private BurgerKingMerchantRepository repository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    @Order(1)
    @DisplayName("1. 精确匹配 - 根据省份精确查询")
    void testExactMatch() {
        log.info("测试精确匹配查询...");
        
        // 使用CriteriaQuery进行精确匹配
        Criteria criteria = new Criteria("storeProvince").is("北京市");
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        Assertions.assertTrue(searchHits.getTotalHits() > 0, "应该找到北京市的门店");
        
        log.info("精确匹配查询完成，找到{}条北京市的门店", searchHits.getTotalHits());
        
        // 验证所有结果都是北京市的
        searchHits.getSearchHits().forEach(hit -> 
            Assertions.assertEquals("北京市", hit.getContent().getStoreProvince(), 
                "所有结果应该都是北京市的门店"));
    }

    @Test
    @Order(2)
    @DisplayName("2. 范围搜索 - 根据门店编号范围查询")
    void testRangeSearch() {
        log.info("测试范围搜索功能...");
        
        // 查询门店编号在19000-20000之间的门店
        Criteria criteria = new Criteria("storeNo").greaterThanEqual(19000).lessThanEqual(20000);
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        
        log.info("范围搜索完成，找到{}条门店编号在19000-20000之间的门店", searchHits.getTotalHits());
        
        // 验证所有结果都在指定范围内
        searchHits.getSearchHits().forEach(hit -> {
            Integer storeNo = hit.getContent().getStoreNo();
            Assertions.assertTrue(storeNo >= 19000 && storeNo <= 20000, 
                "门店编号应该在19000-20000范围内");
        });
    }

    @Test
    @Order(3)
    @DisplayName("3. 复合查询 - 多条件组合查询")
    void testCompoundQuery() {
        log.info("测试复合查询功能...");
        
        // 查询北京市且营业状态为true的门店
        Criteria criteria = new Criteria("storeProvince").is("北京市")
            .and("storeState").is(true)
            .and("hasBreakfast").is(true);
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        
        log.info("复合查询完成，找到{}条符合条件的门店", searchHits.getTotalHits());
        
        // 验证所有结果都符合条件
        searchHits.getSearchHits().forEach(hit -> {
            BurgerKingMerchant merchant = hit.getContent();
            Assertions.assertEquals("北京市", merchant.getStoreProvince());
            Assertions.assertTrue(merchant.getStoreState());
            Assertions.assertTrue(merchant.getHasBreakfast());
        });
    }

    @Test
    @Order(4)
    @DisplayName("4. 分页查询 - 分页获取门店列表")
    void testPagination() {
        log.info("测试分页查询功能...");
        
        // 创建分页参数
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "storeId"));
        
        // 查询所有门店，分页显示
        Page<BurgerKingMerchant> page = repository.findAll(pageable);
        
        Assertions.assertNotNull(page, "分页结果不应为空");
        Assertions.assertEquals(10, page.getContent().size(), "第一页应该有10条记录");
        Assertions.assertTrue(page.getTotalElements() > 0, "总记录数应该大于0");
        
        log.info("分页查询完成，总记录数: {}, 当前页: {}, 每页大小: {}", 
            page.getTotalElements(), page.getNumber(), page.getSize());
        
        // 测试第二页
        Pageable secondPage = PageRequest.of(1, 10, Sort.by(Sort.Direction.DESC, "storeId"));
        Page<BurgerKingMerchant> secondPageResult = repository.findAll(secondPage);
        
        Assertions.assertNotNull(secondPageResult, "第二页结果不应为空");
        Assertions.assertEquals(1, secondPageResult.getNumber(), "应该是第二页");
        
        log.info("第二页查询完成，记录数: {}", secondPageResult.getContent().size());
    }

    @Test
    @Order(5)
    @DisplayName("5. 排序查询 - 按门店名称排序")
    void testSorting() {
        log.info("测试排序查询功能...");
        
        // 按门店名称升序排序
        CriteriaQuery query = new CriteriaQuery(new Criteria());
        query.addSort(Sort.by(Sort.Direction.ASC, "storeName.keyword"));
        query.setMaxResults(10);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        Assertions.assertTrue(searchHits.getTotalHits() > 0, "应该有搜索结果");
        
        log.info("排序查询完成，找到{}条记录", searchHits.getTotalHits());
        
        // 验证排序结果
        List<SearchHit<BurgerKingMerchant>> hits = searchHits.getSearchHits();
        for (int i = 1; i < hits.size(); i++) {
            String prevName = hits.get(i - 1).getContent().getStoreName();
            String currName = hits.get(i).getContent().getStoreName();
            Assertions.assertTrue(prevName.compareTo(currName) <= 0, 
                "门店名称应该按升序排列");
        }
        
        // 显示前5条排序结果
        hits.stream()
            .limit(5)
            .forEach(hit -> log.info("排序结果: {}", hit.getContent().getStoreName()));
    }

    @Test
    @Order(6)
    @DisplayName("6. 模糊搜索 - 根据地址模糊查询")
    void testFuzzySearch() {
        log.info("测试模糊搜索功能...");
        
        // 使用Criteria进行模糊搜索
        Criteria criteria = new Criteria("storeAddress").contains("海淀");
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        
        log.info("模糊搜索完成，找到{}条包含'海淀'的门店", searchHits.getTotalHits());
        
        // 显示前3条结果
        searchHits.getSearchHits().stream()
            .limit(3)
            .forEach(hit -> log.info("模糊匹配门店: {}", hit.getContent().getStoreAddress()));
    }

    @Test
    @Order(7)
    @DisplayName("7. 多字段搜索 - 在多个字段中搜索")
    void testMultiFieldSearch() {
        log.info("测试多字段搜索功能...");
        
        // 在门店名称和地址中搜索"万达"
        Criteria criteria = new Criteria("storeName").contains("万达")
            .or("storeAddress").contains("万达");
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        
        log.info("多字段搜索完成，找到{}条包含'万达'的门店", searchHits.getTotalHits());
        
        // 显示搜索结果
        searchHits.getSearchHits().forEach(hit -> {
            BurgerKingMerchant merchant = hit.getContent();
            log.info("多字段匹配门店: {} - {}", merchant.getStoreName(), merchant.getStoreAddress());
        });
    }

    @Test
    @Order(8)
    @DisplayName("8. 布尔值查询 - 查询有早餐的门店")
    void testBooleanQuery() {
        log.info("测试布尔值查询...");
        
        // 查询有早餐的门店
        Criteria criteria = new Criteria("hasBreakfast").is(true);
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        
        log.info("布尔值查询完成，找到{}条有早餐的门店", searchHits.getTotalHits());
        
        // 验证所有结果都有早餐
        searchHits.getSearchHits().forEach(hit -> 
            Assertions.assertTrue(hit.getContent().getHasBreakfast(), "所有结果都应该有早餐"));
    }

    @Test
    @Order(9)
    @DisplayName("9. 空值查询 - 查询没有联系人的门店")
    void testNullQuery() {
        log.info("测试空值查询...");
        
        // 查询没有联系人的门店
        Criteria criteria = new Criteria("storeContactName").is("");
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);
        
        Assertions.assertNotNull(searchHits, "搜索结果不应为空");
        
        log.info("空值查询完成，找到{}条没有联系人的门店", searchHits.getTotalHits());
        
        // 验证所有结果都没有联系人
        searchHits.getSearchHits().forEach(hit -> 
            Assertions.assertNull(hit.getContent().getStoreContactName(), "所有结果都应该没有联系人"));
    }

    @Test
    @Order(10)
    @DisplayName("10. 统计查询 - 统计各省份门店数量")
    void testCountByProvince() {
        log.info("测试统计查询...");
        
        // 统计北京市的门店数量
        Criteria criteria = new Criteria("storeProvince").is("北京市");
        CriteriaQuery query = new CriteriaQuery(criteria);
        
        long count = elasticsearchOperations.count(query, BurgerKingMerchant.class);
        
        Assertions.assertTrue(count > 0, "北京市应该有门店");
        
        log.info("统计查询完成，北京市共有{}家门店", count);
        
        // 统计上海市的门店数量
        Criteria criteria2 = new Criteria("storeProvince").is("上海市");
        CriteriaQuery query2 = new CriteriaQuery(criteria2);
        
        long count2 = elasticsearchOperations.count(query2, BurgerKingMerchant.class);
        
        log.info("上海市共有{}家门店", count2);
    }
} 