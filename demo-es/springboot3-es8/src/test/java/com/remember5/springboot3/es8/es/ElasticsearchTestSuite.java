package com.remember5.springboot3.es8.es;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

/**
 * Elasticsearch测试套件
 * 用于统一管理和运行所有ES相关的测试
 *
 * @author wangjiahao
 * @date 2025/1/8
 */
@Slf4j
@DisplayName("Elasticsearch测试套件")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Suite
@SelectClasses({
    BurgerKingMerchantCRUDTest.class,
    BurgerKingMerchantSearchTest.class,
    BurgerKingMerchantAggregationTest.class,
    BurgerKingMerchantGeoTest.class
})
public class ElasticsearchTestSuite {

    @Test
    @Order(1)
    @DisplayName("1. 测试套件初始化")
    void testSuiteInitialization() {
        log.info("=== Elasticsearch测试套件开始 ===");
        log.info("测试环境: Spring Boot 3 + Elasticsearch 8");
        log.info("测试数据: 汉堡王门店信息");
        log.info("测试范围: CRUD操作、搜索功能、聚合查询、地理位置搜索");
        log.info("=== 初始化完成 ===");
    }

    @Test
    @Order(2)
    @DisplayName("2. 测试套件总结")
    void testSuiteSummary() {
        log.info("=== 测试套件总结 ===");
        log.info("已实现的测试类:");
        log.info("1. BurgerKingMerchantCRUDTest - 基础CRUD操作测试");
        log.info("   - 数据初始化");
        log.info("   - 文档创建、读取、更新、删除");
        log.info("   - 批量操作");
        log.info("   - 文档统计和存在性检查");

        log.info("2. BurgerKingMerchantSearchTest - 搜索功能测试");
        log.info("   - 精确匹配查询");
        log.info("   - 范围搜索");
        log.info("   - 复合查询");
        log.info("   - 分页查询");
        log.info("   - 排序查询");
        log.info("   - 模糊搜索");
        log.info("   - 多字段搜索");
        log.info("   - 布尔值查询");
        log.info("   - 空值查询");
        log.info("   - 统计查询");

        log.info("3. BurgerKingMerchantAggregationTest - 聚合查询测试");
        log.info("   - 基础统计");
        log.info("   - 分组统计");
        log.info("   - 数值统计");
        log.info("   - 布尔值统计");
        log.info("   - 复合统计");
        log.info("   - 范围统计");
        log.info("   - 唯一值统计");
        log.info("   - 条件统计");
        log.info("   - 综合统计报告");

        log.info("4. BurgerKingMerchantGeoTest - 地理位置搜索测试");
        log.info("   - 地理位置数据验证");
        log.info("   - 地理位置范围查询");
        log.info("   - 地理位置距离查询");
        log.info("   - 地理位置多边形查询");
        log.info("   - 复合地理位置查询");
        log.info("   - 地理位置排序");
        log.info("   - 地理位置统计");
        log.info("   - 地理位置距离计算");
        log.info("   - 地理位置数据质量检查");
        log.info("   - 地理位置查询性能测试");

        log.info("=== 测试套件总结完成 ===");
    }

    @Test
    @Order(3)
    @DisplayName("3. 测试数据说明")
    void testDataDescription() {
        log.info("=== 测试数据说明 ===");
        log.info("数据源: bk_stores.csv");
        log.info("数据内容: 汉堡王门店信息");
        log.info("主要字段:");
        log.info("  - storeId: 门店ID");
        log.info("  - storeProvince: 省份");
        log.info("  - storeCity: 城市");
        log.info("  - storeName: 门店名称");
        log.info("  - storeAddress: 门店地址");
        log.info("  - location: 地理位置(经纬度)");
        log.info("  - storeNo: 门店编号");
        log.info("  - hasBreakfast: 是否有早餐");
        log.info("  - useCard: 是否可以用卡");
        log.info("  - storeState: 营业状态");
        log.info("  - storePublish: 发布状态");
        log.info("=== 数据说明完成 ===");
    }

    @Test
    @Order(4)
    @DisplayName("4. 测试运行指南")
    void testRunGuide() {
        log.info("=== 测试运行指南 ===");
        log.info("1. 确保Elasticsearch服务已启动 (默认端口: 9200)");
        log.info("2. 确保CSV数据文件存在: src/main/resources/bk_stores.csv");
        log.info("3. 运行顺序建议:");
        log.info("   - 先运行BurgerKingMerchantCRUDTest进行数据初始化");
        log.info("   - 然后运行其他测试类");
        log.info("4. 单个测试类运行:");
        log.info("   - 右键点击测试类 -> Run");
        log.info("5. 所有测试运行:");
        log.info("   - 右键点击ElasticsearchTestSuite -> Run");
        log.info("6. 命令行运行:");
        log.info("   - mvn test -Dtest=ElasticsearchTestSuite");
        log.info("=== 运行指南完成 ===");
    }

    @Test
    @Order(5)
    @DisplayName("5. 测试套件完成")
    void testSuiteCompletion() {
        log.info("=== Elasticsearch测试套件完成 ===");
        log.info("所有测试类已准备就绪");
        log.info("可以开始运行具体的功能测试");
        log.info("=== 测试套件结束 ===");
    }
}
