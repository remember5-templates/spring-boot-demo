package com.remember5.es.es7.es;

import com.remember5.es.es7.Springboot3Es7Application;
import com.remember5.es.es7.entity.EsBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 优雅的测试结构示例
 * 展示如何不使用@Order来控制测试顺序，而是使用更优雅的方式
 */
@Slf4j
@DisplayName("优雅的测试结构示例")
@SpringBootTest(classes = Springboot3Es7Application.class)
@TestPropertySource(properties = {
    "spring.elasticsearch.rest.connection-timeout=30s",
    "spring.elasticsearch.rest.read-timeout=30s"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 仅在必要时使用
@Tag("elegant")
class ElegantTestStructureExample {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    private static final String INDEX_NAME = "elegant_test_index";
    private static final IndexCoordinates INDEX_COORDINATES = IndexCoordinates.of(INDEX_NAME);

    // 静态变量用于在测试方法间共享状态
    private static boolean indexCreated = false;
    private static List<EsBusinessInfo> testData;

    /**
     * 方案一：使用@BeforeAll进行一次性初始化
     * 优点：确保在所有测试前执行，只执行一次
     * 缺点：如果初始化失败，所有测试都会跳过
     */
    @BeforeAll
    static void setUpIndex() {
        log.info("=== 使用@BeforeAll进行索引初始化 ===");
        // 这里可以添加索引创建逻辑
        // 注意：@BeforeAll是静态方法，不能直接注入ElasticsearchRestTemplate
    }

    /**
     * 方案二：使用@BeforeEach进行每次测试前的初始化
     * 优点：每个测试都有干净的环境
     * 缺点：重复执行，性能开销较大
     */
    @BeforeEach
    void setUpEach() {
        log.info("=== 使用@BeforeEach进行每次初始化 ===");
        // 这里可以添加每次测试前的初始化逻辑
    }

    /**
     * 方案三：使用@AfterAll进行一次性清理
     */
    @AfterAll
    static void tearDownIndex() {
        log.info("=== 使用@AfterAll进行索引清理 ===");
        // 这里可以添加索引清理逻辑
    }

    /**
     * 方案四：使用@AfterEach进行每次测试后的清理
     */
    @AfterEach
    void tearDownEach() {
        log.info("=== 使用@AfterEach进行每次清理 ===");
        // 这里可以添加每次测试后的清理逻辑
    }

    // ==================== 推荐的优雅方案 ====================

    /**
     * 推荐方案：使用测试方法内部管理生命周期
     * 每个测试方法都是独立的，自己管理自己的数据
     */
    @Test
    @DisplayName("优雅方案：独立测试方法")
    @Tag("independent")
    void testIndependentMethod() {
        log.info("=== 优雅方案：独立测试方法 ===");

        // 1. 创建临时索引
        String tempIndexName = "temp_index_" + System.currentTimeMillis();
        IndexCoordinates tempIndex = IndexCoordinates.of(tempIndexName);

        try {
            // 创建索引
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(tempIndex);
            if (indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.create();
            log.info("创建临时索引: {}", tempIndexName);

            // 2. 执行测试逻辑
            EsBusinessInfo testBusiness = createTestBusiness(1, "测试商家");
            EsBusinessInfo saved = elasticsearchRestTemplate.save(testBusiness, tempIndex);
            assertNotNull(saved);
            log.info("测试数据保存成功: {}", saved.getName());

            // 3. 验证结果
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(termQuery("id", 1))
                .build();
            SearchHits<EsBusinessInfo> results = elasticsearchRestTemplate.search(query, EsBusinessInfo.class, tempIndex);
            assertEquals(1, results.getTotalHits());
            log.info("验证成功，找到 {} 个文档", results.getTotalHits());

        } finally {
            // 4. 清理临时索引（确保执行）
            try {
                IndexOperations indexOps = elasticsearchRestTemplate.indexOps(tempIndex);
                if (indexOps.exists()) {
                    indexOps.delete();
                    log.info("清理临时索引: {}", tempIndexName);
                }
            } catch (Exception e) {
                log.warn("清理临时索引失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 推荐方案：使用测试类级别的生命周期管理
     * 通过静态变量和条件检查来管理状态
     */
    @Test
    @DisplayName("优雅方案：类级别生命周期管理")
    @Tag("class-level")
    void testClassLevelLifecycle() {
        log.info("=== 优雅方案：类级别生命周期管理 ===");

        // 1. 条件性创建索引
        if (!indexCreated) {
            createIndexIfNotExists();
            indexCreated = true;
        }

        // 2. 执行测试逻辑
        EsBusinessInfo testBusiness = createTestBusiness(2, "类级别测试商家");
        EsBusinessInfo saved = elasticsearchRestTemplate.save(testBusiness, INDEX_COORDINATES);
        assertNotNull(saved);
        log.info("类级别测试数据保存成功: {}", saved.getName());

        // 3. 验证结果
        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(termQuery("id", 2))
            .build();
        SearchHits<EsBusinessInfo> results = elasticsearchRestTemplate.search(query, EsBusinessInfo.class, INDEX_COORDINATES);
        assertEquals(1, results.getTotalHits());
        log.info("类级别验证成功，找到 {} 个文档", results.getTotalHits());
    }

    /**
     * 推荐方案：使用测试套件模式
     * 将相关测试组织在一个测试套件中
     */
    @Nested
    @DisplayName("测试套件：完整的CRUD操作")
    @Tag("test-suite")
    class CrudTestSuite {

        private String suiteIndexName;
        private IndexCoordinates suiteIndex;

        @BeforeEach
        void setUpSuite() {
            suiteIndexName = "suite_index_" + System.currentTimeMillis();
            suiteIndex = IndexCoordinates.of(suiteIndexName);

            // 创建套件专用索引
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(suiteIndex);
            if (indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.create();
            log.info("测试套件索引创建: {}", suiteIndexName);
        }

        @AfterEach
        void tearDownSuite() {
            // 清理套件专用索引
            try {
                IndexOperations indexOps = elasticsearchRestTemplate.indexOps(suiteIndex);
                if (indexOps.exists()) {
                    indexOps.delete();
                    log.info("测试套件索引清理: {}", suiteIndexName);
                }
            } catch (Exception e) {
                log.warn("测试套件索引清理失败: {}", e.getMessage());
            }
        }

        @Test
        @DisplayName("套件测试：创建操作")
        void testCreate() {
            EsBusinessInfo business = createTestBusiness(1, "套件测试商家");
            EsBusinessInfo saved = elasticsearchRestTemplate.save(business, suiteIndex);
            assertNotNull(saved);
            log.info("套件创建测试成功: {}", saved.getName());
        }

        @Test
        @DisplayName("套件测试：查询操作")
        void testRead() {
            // 先创建数据
            EsBusinessInfo business = createTestBusiness(2, "套件查询商家");
            elasticsearchRestTemplate.save(business, suiteIndex);

            // 然后查询
            NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(termQuery("id", 2))
                .build();
            SearchHits<EsBusinessInfo> results = elasticsearchRestTemplate.search(query, EsBusinessInfo.class, suiteIndex);
            assertEquals(1, results.getTotalHits());
            log.info("套件查询测试成功");
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建索引（如果不存在）
     */
    private void createIndexIfNotExists() {
        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES);
        if (!indexOps.exists()) {
            indexOps.create();
            log.info("创建索引: {}", INDEX_NAME);
        } else {
            log.info("索引已存在: {}", INDEX_NAME);
        }
    }

    /**
     * 创建测试商家数据
     */
    private EsBusinessInfo createTestBusiness(Integer id, String name) {
        EsBusinessInfo business = new EsBusinessInfo();
        business.setId(id);
        business.setName(name);
        business.setDescription("这是一个测试商家");
        business.setAddress("测试地址");
        business.setContactInfo("13800138000");
        business.setRating(4.5);
        business.setSalesVolume(1000L);
        business.setReviewCount(100);
        business.setFavoriteCount(50L);
        business.setIsVip(true);
        business.setDeliverySupport(true);
        business.setMinOrderAmount(20.0);
        business.setArea("测试区域");
        business.setBusinessType("餐饮服务");
        business.setMainProducts(Arrays.asList("测试产品"));
        business.setTags(Arrays.asList("测试", "示例"));
        business.setCreateTime(new Date());
        business.setUpdateTime(new Date());
        business.setCoordinate(new GeoPoint(39.916527, 116.397128));
        return business;
    }

    // ==================== 清理方法 ====================

    /**
     * 最终清理：删除类级别索引
     * 这个方法可以在所有测试完成后手动调用，或者通过其他机制触发
     */
    @Test
    @DisplayName("最终清理：删除类级别索引")
    @Tag("cleanup")
    void testFinalCleanup() {
        log.info("=== 最终清理：删除类级别索引 ===");

        try {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(INDEX_COORDINATES);
            if (indexOps.exists()) {
                indexOps.delete();
                log.info("成功删除类级别索引: {}", INDEX_NAME);
                indexCreated = false; // 重置状态
            }
        } catch (Exception e) {
            log.warn("删除类级别索引失败: {}", e.getMessage());
        }
    }
}
