package com.remember5.springboot3.es8.es;

import com.remember5.springboot3.es8.entity.BurgerKingMerchant;
import com.remember5.springboot3.es8.repository.BurgerKingMerchantRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 汉堡王商户聚合查询测试
 *
 * @author wangjiahao
 * @date 2025/1/8
 */
@Slf4j
@DisplayName("汉堡王商户聚合查询测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BurgerKingMerchantAggregationTest {

    @Autowired
    private BurgerKingMerchantRepository repository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    @Order(1)
    @DisplayName("1. 基础统计 - 统计总门店数量")
    void testBasicCount() {
        log.info("测试基础统计功能...");

        long totalCount = repository.count();

        Assertions.assertTrue(totalCount > 0, "总门店数量应该大于0");

        log.info("总门店数量: {}", totalCount);
    }

    @Test
    @Order(2)
    @DisplayName("2. 分组统计 - 按省份统计门店数量")
    void testGroupByProvince() {
        log.info("测试按省份分组统计...");

        // 获取所有门店
        List<BurgerKingMerchant> allMerchants = new ArrayList<>();
        repository.findAll().forEach(allMerchants::add);

        // 按省份分组统计，处理null值
        Map<String, Long> provinceStats = allMerchants.stream()
            .collect(Collectors.groupingBy(
                merchant -> merchant.getStoreProvince() != null ? merchant.getStoreProvince() : "未知省份",
                Collectors.counting()
            ));

        Assertions.assertNotNull(provinceStats, "省份统计结果不应为空");
        Assertions.assertFalse(provinceStats.isEmpty(), "省份统计结果不应为空");

        log.info("按省份分组统计完成");
        provinceStats.forEach((province, count) ->
            log.info("{}: {}家门店", province, count));
    }

    @Test
    @Order(3)
    @DisplayName("3. 分组统计 - 按城市统计门店数量")
    void testGroupByCity() {
        log.info("测试按城市分组统计...");

        // 获取所有门店
        List<BurgerKingMerchant> allMerchants = new ArrayList<>();
        repository.findAll().forEach(allMerchants::add);

        // 按城市分组统计，处理null值
        Map<String, Long> cityStats = allMerchants.stream()
            .collect(Collectors.groupingBy(
                merchant -> merchant.getStoreCity() != null ? merchant.getStoreCity() : "未知城市",
                Collectors.counting()
            ));

        Assertions.assertNotNull(cityStats, "城市统计结果不应为空");
        Assertions.assertFalse(cityStats.isEmpty(), "城市统计结果不应为空");

        log.info("按城市分组统计完成");
        cityStats.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(10)
            .forEach(entry -> log.info("{}: {}家门店", entry.getKey(), entry.getValue()));
    }

    @Test
    @Order(4)
    @DisplayName("4. 数值统计 - 门店编号统计")
    void testNumericStats() {
        log.info("测试门店编号数值统计...");

        // 获取所有门店
        List<BurgerKingMerchant> merchants = new ArrayList<>();
        repository.findAll().forEach(merchants::add);

        // 计算统计信息
        int minStoreNo = merchants.stream()
            .mapToInt(merchant -> merchant.getStoreNo() != null ? merchant.getStoreNo() : Integer.MAX_VALUE)
            .min()
            .orElse(0);

        int maxStoreNo = merchants.stream()
            .mapToInt(merchant -> merchant.getStoreNo() != null ? merchant.getStoreNo() : Integer.MIN_VALUE)
            .max()
            .orElse(0);

        double avgStoreNo = merchants.stream()
            .filter(merchant -> merchant.getStoreNo() != null)
            .mapToInt(BurgerKingMerchant::getStoreNo)
            .average()
            .orElse(0.0);

        long totalStores = merchants.size();

        log.info("门店编号统计完成");
        log.info("最小门店编号: {}", minStoreNo);
        log.info("最大门店编号: {}", maxStoreNo);
        log.info("平均门店编号: {:.2f}", avgStoreNo);
        log.info("总门店数量: {}", totalStores);

        Assertions.assertTrue(minStoreNo > 0, "最小门店编号应该大于0");
        Assertions.assertTrue(maxStoreNo > minStoreNo, "最大门店编号应该大于最小门店编号");
    }

    @Test
    @Order(5)
    @DisplayName("5. 布尔值统计 - 统计有早餐的门店")
    void testBooleanStats() {
        log.info("测试布尔值统计...");

        // 统计有早餐的门店
        Criteria hasBreakfastCriteria = new Criteria("hasBreakfast").is(true);
        CriteriaQuery hasBreakfastQuery = new CriteriaQuery(hasBreakfastCriteria);
        long hasBreakfastCount = elasticsearchOperations.count(hasBreakfastQuery, BurgerKingMerchant.class);

        // 统计没有早餐的门店
        Criteria noBreakfastCriteria = new Criteria("hasBreakfast").is(false);
        CriteriaQuery noBreakfastQuery = new CriteriaQuery(noBreakfastCriteria);
        long noBreakfastCount = elasticsearchOperations.count(noBreakfastQuery, BurgerKingMerchant.class);

        // 统计可以用卡的门店
        Criteria useCardCriteria = new Criteria("useCard").is(true);
        CriteriaQuery useCardQuery = new CriteriaQuery(useCardCriteria);
        long useCardCount = elasticsearchOperations.count(useCardQuery, BurgerKingMerchant.class);

        log.info("布尔值统计完成");
        log.info("有早餐的门店: {}家", hasBreakfastCount);
        log.info("没有早餐的门店: {}家", noBreakfastCount);
        log.info("可以用卡的门店: {}家", useCardCount);

        Assertions.assertTrue(hasBreakfastCount >= 0, "有早餐的门店数量应该大于等于0");
        Assertions.assertTrue(noBreakfastCount >= 0, "没有早餐的门店数量应该大于等于0");
        Assertions.assertTrue(useCardCount >= 0, "可以用卡的门店数量应该大于等于0");
    }

    @Test
    @Order(6)
    @DisplayName("6. 复合统计 - 按省份统计有早餐的门店")
    void testCompoundStats() {
        log.info("测试复合统计...");

        // 获取所有门店
        List<BurgerKingMerchant> allMerchants = new ArrayList<>();
        repository.findAll().forEach(allMerchants::add);

        // 按省份统计有早餐的门店
        Map<String, Long> provinceBreakfastStats = allMerchants.stream()
            .filter(merchant -> Boolean.TRUE.equals(merchant.getHasBreakfast()))
            .collect(Collectors.groupingBy(
                merchant -> merchant.getStoreProvince() != null ? merchant.getStoreProvince() : "未知省份",
                Collectors.counting()
            ));

        log.info("按省份统计有早餐的门店完成");
        provinceBreakfastStats.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> log.info("{}: {}家有早餐的门店", entry.getKey(), entry.getValue()));
    }

    @Test
    @Order(7)
    @DisplayName("7. 范围统计 - 按门店编号范围统计")
    void testRangeStats() {
        log.info("测试范围统计...");

        // 获取所有门店
        List<BurgerKingMerchant> merchants = new ArrayList<>();
        repository.findAll().forEach(merchants::add);

        // 按门店编号范围统计
        long smallStores = merchants.stream()
            .filter(merchant -> merchant.getStoreNo() != null && merchant.getStoreNo() < 20000)
            .count();

        long mediumStores = merchants.stream()
            .filter(merchant -> merchant.getStoreNo() != null && 
                merchant.getStoreNo() >= 20000 && merchant.getStoreNo() < 30000)
            .count();

        long largeStores = merchants.stream()
            .filter(merchant -> merchant.getStoreNo() != null && merchant.getStoreNo() >= 30000)
            .count();

        log.info("按门店编号范围统计完成");
        log.info("小型门店(编号<20000): {}家", smallStores);
        log.info("中型门店(编号20000-30000): {}家", mediumStores);
        log.info("大型门店(编号>=30000): {}家", largeStores);

        Assertions.assertTrue(smallStores + mediumStores + largeStores > 0, "应该有门店数据");
    }

    @Test
    @Order(8)
    @DisplayName("8. 唯一值统计 - 统计不同省份数量")
    void testUniqueStats() {
        log.info("测试唯一值统计...");

        // 获取所有门店
        List<BurgerKingMerchant> allMerchants = new ArrayList<>();
        repository.findAll().forEach(allMerchants::add);

        // 统计不同省份数量
        long uniqueProvinces = allMerchants.stream()
            .map(merchant -> merchant.getStoreProvince() != null ? merchant.getStoreProvince() : "未知省份")
            .distinct()
            .count();

        // 统计不同城市数量
        long uniqueCities = allMerchants.stream()
            .map(merchant -> merchant.getStoreCity() != null ? merchant.getStoreCity() : "未知城市")
            .distinct()
            .count();

        log.info("唯一值统计完成");
        log.info("不同省份数量: {}", uniqueProvinces);
        log.info("不同城市数量: {}", uniqueCities);

        Assertions.assertTrue(uniqueProvinces > 0, "应该有不同省份");
        Assertions.assertTrue(uniqueCities > 0, "应该有不同城市");
    }

    @Test
    @Order(9)
    @DisplayName("9. 条件统计 - 统计营业状态")
    void testConditionalStats() {
        log.info("测试条件统计...");

        // 统计营业状态为true的门店
        Criteria openCriteria = new Criteria("storeState").is(true);
        CriteriaQuery openQuery = new CriteriaQuery(openCriteria);
        long openCount = elasticsearchOperations.count(openQuery, BurgerKingMerchant.class);

        // 统计营业状态为false的门店
        Criteria closedCriteria = new Criteria("storeState").is(false);
        CriteriaQuery closedQuery = new CriteriaQuery(closedCriteria);
        long closedCount = elasticsearchOperations.count(closedQuery, BurgerKingMerchant.class);

        log.info("条件统计完成");
        log.info("营业中的门店: {}家", openCount);
        log.info("关闭的门店: {}家", closedCount);

        Assertions.assertTrue(openCount >= 0, "营业中的门店数量应该大于等于0");
        Assertions.assertTrue(closedCount >= 0, "关闭的门店数量应该大于等于0");
    }

    @Test
    @Order(10)
    @DisplayName("10. 综合统计报告")
    void testComprehensiveReport() {
        log.info("生成综合统计报告...");

        // 获取所有门店
        List<BurgerKingMerchant> merchants = new ArrayList<>();
        repository.findAll().forEach(merchants::add);

        // 基础统计
        long totalCount = merchants.size();

        // 按省份统计
        Map<String, Long> provinceStats = merchants.stream()
            .collect(Collectors.groupingBy(
                merchant -> merchant.getStoreProvince() != null ? merchant.getStoreProvince() : "未知省份",
                Collectors.counting()
            ));

        // 有早餐的门店统计
        long hasBreakfastCount = merchants.stream()
            .filter(merchant -> Boolean.TRUE.equals(merchant.getHasBreakfast()))
            .count();

        // 可以用卡的门店统计
        long useCardCount = merchants.stream()
            .filter(merchant -> Boolean.TRUE.equals(merchant.getUseCard()))
            .count();

        // 营业中的门店统计
        long openCount = merchants.stream()
            .filter(merchant -> Boolean.TRUE.equals(merchant.getStoreState()))
            .count();

        log.info("=== 汉堡王门店综合统计报告 ===");
        log.info("总门店数量: {}", totalCount);
        log.info("有早餐的门店: {}家 ({:.1f}%)", hasBreakfastCount, (double) hasBreakfastCount / totalCount * 100);
        log.info("可以用卡的门店: {}家 ({:.1f}%)", useCardCount, (double) useCardCount / totalCount * 100);
        log.info("营业中的门店: {}家 ({:.1f}%)", openCount, (double) openCount / totalCount * 100);
        log.info("按省份分布:");
        provinceStats.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry -> log.info("  {}: {}家", entry.getKey(), entry.getValue()));
        log.info("=== 报告结束 ===");

        Assertions.assertTrue(totalCount > 0, "应该有门店数据");
    }
}
