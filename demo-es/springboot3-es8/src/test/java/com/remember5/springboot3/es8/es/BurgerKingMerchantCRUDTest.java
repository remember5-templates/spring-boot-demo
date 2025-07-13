package com.remember5.springboot3.es8.es;

import com.remember5.springboot3.es8.entity.BurgerKingMerchant;
import com.remember5.springboot3.es8.repository.BurgerKingMerchantRepository;
import com.remember5.springboot3.es8.util.EnhancedCsvLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

/**
 * 汉堡王商户CRUD操作测试
 *
 * @author wangjiahao
 * @date 2025/1/8
 */
@Slf4j
@DisplayName("汉堡王商户CRUD操作测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BurgerKingMerchantCRUDTest {

    @Autowired
    private BurgerKingMerchantRepository repository;

    private static final Integer TEST_STORE_ID = 99999;

    @Test
    @Order(1)
    @DisplayName("1. 数据初始化 - 从CSV加载数据到ES")
    void testDataInitialization() {
        log.info("开始从CSV文件加载数据到Elasticsearch...");

        List<BurgerKingMerchant> merchants = EnhancedCsvLoader.loadCsvData(
            "src/main/resources/bk_stores.csv",
            BurgerKingMerchant.class
        );

        Assertions.assertNotNull(merchants, "CSV数据加载失败");
        Assertions.assertFalse(merchants.isEmpty(), "CSV数据为空");

        log.info("CSV数据加载成功，共{}条记录", merchants.size());

        // 保存到ES
        repository.saveAll(merchants);

        // 验证数据是否保存成功
        long count = repository.count();
        Assertions.assertTrue(count > 0, "数据保存失败");

        log.info("数据初始化完成，ES中共有{}条记录", count);
    }

    @Test
    @Order(2)
    @DisplayName("2. 创建文档 - 插入单条商户数据")
    void testCreateDocument() {
        log.info("测试创建单条商户文档...");

        BurgerKingMerchant merchant = BurgerKingMerchant.builder()
            .storeId(TEST_STORE_ID)
            .storeProvince("测试省")
            .storeCity("测试市")
            .storeArea("测试区")
            .storeName("测试汉堡王门店")
            .storeAddress("测试地址123号")
            .storeBrandManage("测试管理公司")
            .storeBusinessHours("9:00-22:00")
            .storeContactName("测试联系人")
            .storeContactPhone("13800138000")
            .storeIPaddress("192.168.1.100")
            .storeNo(99999)
            .storePhone("010-12345678")
            .storePublish(true)
            .storeState(true)
            .useCard(true)
            .hasBreakfast(true)
            .build();

        BurgerKingMerchant saved = repository.save(merchant);

        Assertions.assertNotNull(saved, "保存失败");
        Assertions.assertEquals(TEST_STORE_ID, saved.getStoreId());
        Assertions.assertEquals("测试汉堡王门店", saved.getStoreName());

        log.info("文档创建成功，ID: {}", saved.getStoreId());
    }

    @Test
    @Order(3)
    @DisplayName("3. 查询文档 - 根据ID查询")
    void testReadDocument() {
        log.info("测试根据ID查询文档...");

        Optional<BurgerKingMerchant> found = repository.findById(TEST_STORE_ID);

        Assertions.assertTrue(found.isPresent(), "未找到测试文档");

        BurgerKingMerchant merchant = found.get();
        Assertions.assertEquals(TEST_STORE_ID, merchant.getStoreId());
        Assertions.assertEquals("测试汉堡王门店", merchant.getStoreName());
        Assertions.assertEquals("测试省", merchant.getStoreProvince());

        log.info("文档查询成功: {}", merchant.getStoreName());
    }

    @Test
    @Order(4)
    @DisplayName("4. 更新文档 - 修改商户信息")
    void testUpdateDocument() {
        log.info("测试更新文档...");

        Optional<BurgerKingMerchant> found = repository.findById(TEST_STORE_ID);
        Assertions.assertTrue(found.isPresent(), "未找到要更新的文档");

        BurgerKingMerchant merchant = found.get();
        merchant.setStoreName("更新后的汉堡王门店");
        merchant.setStoreAddress("更新后的地址456号");
        merchant.setStoreContactPhone("13900139000");

        BurgerKingMerchant updated = repository.save(merchant);

        Assertions.assertNotNull(updated, "更新失败");
        Assertions.assertEquals("更新后的汉堡王门店", updated.getStoreName());
        Assertions.assertEquals("更新后的地址456号", updated.getStoreAddress());
        Assertions.assertEquals("13900139000", updated.getStoreContactPhone());

        log.info("文档更新成功: {}", updated.getStoreName());
    }

    @Test
    @Order(5)
    @DisplayName("5. 批量操作 - 批量插入多条数据")
    void testBulkOperations() {
        log.info("测试批量操作...");

        List<BurgerKingMerchant> bulkData = List.of(
            BurgerKingMerchant.builder()
                .storeId(99998)
                .storeProvince("批量测试省1")
                .storeCity("批量测试市1")
                .storeName("批量测试门店1")
                .storeAddress("批量测试地址1")
                .storePublish(true)
                .storeState(true)
                .build(),
            BurgerKingMerchant.builder()
                .storeId(99997)
                .storeProvince("批量测试省2")
                .storeCity("批量测试市2")
                .storeName("批量测试门店2")
                .storeAddress("批量测试地址2")
                .storePublish(true)
                .storeState(true)
                .build()
        );

        Iterable<BurgerKingMerchant> saved = repository.saveAll(bulkData);

        Assertions.assertNotNull(saved, "批量保存失败");

        // 验证批量保存的数据
        Optional<BurgerKingMerchant> found1 = repository.findById(99998);
        Optional<BurgerKingMerchant> found2 = repository.findById(99997);

        Assertions.assertTrue(found1.isPresent(), "批量数据1未保存成功");
        Assertions.assertTrue(found2.isPresent(), "批量数据2未保存成功");

        log.info("批量操作成功，保存了{}条数据", bulkData.size());
    }

    @Test
    @Order(6)
    @DisplayName("6. 删除文档 - 删除测试数据")
    void testDeleteDocument() {
        log.info("测试删除文档...");

        // 删除单条数据
        repository.deleteById(TEST_STORE_ID);

        Optional<BurgerKingMerchant> found = repository.findById(TEST_STORE_ID);
        Assertions.assertFalse(found.isPresent(), "文档删除失败");

        // 删除批量测试数据
        repository.deleteById(99998);
        repository.deleteById(99997);

        Optional<BurgerKingMerchant> found1 = repository.findById(99998);
        Optional<BurgerKingMerchant> found2 = repository.findById(99997);

        Assertions.assertFalse(found1.isPresent(), "批量数据1删除失败");
        Assertions.assertFalse(found2.isPresent(), "批量数据2删除失败");

        log.info("文档删除成功");
    }

    @Test
    @Order(7)
    @DisplayName("7. 统计文档数量")
    void testCountDocuments() {
        log.info("测试统计文档数量...");

        long count = repository.count();

        Assertions.assertTrue(count > 0, "文档数量应该大于0");

        log.info("当前索引中共有{}条文档", count);
    }

    @Test
    @Order(8)
    @DisplayName("8. 检查文档是否存在")
    void testExistsById() {
        log.info("测试检查文档是否存在...");

        // 查询一个真实存在的ID（从CSV数据中）
        boolean exists = repository.existsById(139);

        Assertions.assertTrue(exists, "ID为139的文档应该存在");

        // 查询一个不存在的ID
        boolean notExists = repository.existsById(999999);

        Assertions.assertFalse(notExists, "ID为999999的文档不应该存在");

        log.info("文档存在性检查完成");
    }
}
