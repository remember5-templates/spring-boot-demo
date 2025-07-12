package com.remember5.es.es7.es;

import com.remember5.es.es7.Springboot3Es7Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

/**
 * Elasticsearch测试基类
 * 提供优雅的索引生命周期管理，避免使用@Order
 */
@Slf4j
@SpringBootTest(classes = Springboot3Es7Application.class)
@TestPropertySource(properties = {
    "spring.elasticsearch.rest.connection-timeout=30s",
    "spring.elasticsearch.rest.read-timeout=30s"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // 重要：确保每个测试类只有一个实例
public abstract class BaseElasticsearchTest {

    @Autowired
    protected ElasticsearchRestTemplate elasticsearchRestTemplate;

    // 每个测试类使用唯一的索引名称
    protected final String indexName;
    protected final IndexCoordinates indexCoordinates;

    // 标记索引是否已创建
    private boolean indexCreated = false;

    /**
     * 构造函数：生成唯一的索引名称
     */
    protected BaseElasticsearchTest() {
        this.indexName = generateUniqueIndexName();
        this.indexCoordinates = IndexCoordinates.of(indexName);
        log.info("测试类 {} 使用索引: {}", this.getClass().getSimpleName(), indexName);
    }

    /**
     * 生成唯一的索引名称
     * 格式：test_{类名}_{时间戳}_{随机UUID}
     */
    private String generateUniqueIndexName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("test_%s_%s_%s", className, timestamp, uuid);
    }

    /**
     * 获取索引名称
     */
    protected String getIndexName() {
        return indexName;
    }

    /**
     * 获取索引坐标
     */
    protected IndexCoordinates getIndexCoordinates() {
        return indexCoordinates;
    }

    /**
     * 创建索引（如果不存在）
     * 使用懒加载模式，只在需要时创建
     */
    protected void createIndexIfNotExists() {
        if (!indexCreated) {
            try {
                IndexOperations indexOps = elasticsearchRestTemplate.indexOps(indexCoordinates);
                if (indexOps.exists()) {
                    log.info("索引已存在，删除旧索引: {}", indexName);
                    indexOps.delete();
                }

                boolean created = indexOps.create();
                if (created) {
                    log.info("成功创建索引: {}", indexName);
                    indexCreated = true;
                } else {
                    log.warn("索引创建失败: {}", indexName);
                }
            } catch (Exception e) {
                log.error("创建索引时发生错误: {}", e.getMessage(), e);
                throw new RuntimeException("无法创建索引: " + indexName, e);
            }
        }
    }

    /**
     * 删除索引
     */
    protected void deleteIndex() {
        try {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(indexCoordinates);
            if (indexOps.exists()) {
                boolean deleted = indexOps.delete();
                if (deleted) {
                    log.info("成功删除索引: {}", indexName);
                    indexCreated = false;
                } else {
                    log.warn("索引删除失败: {}", indexName);
                }
            } else {
                log.info("索引不存在，无需删除: {}", indexName);
            }
        } catch (Exception e) {
            log.error("删除索引时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 刷新索引
     */
    protected void refreshIndex() {
        try {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(indexCoordinates);
            indexOps.refresh();
            log.debug("索引刷新成功: {}", indexName);
        } catch (Exception e) {
            log.warn("索引刷新失败: {}", e.getMessage());
        }
    }

    /**
     * 检查索引是否存在
     */
    protected boolean indexExists() {
        try {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(indexCoordinates);
            return indexOps.exists();
        } catch (Exception e) {
            log.warn("检查索引存在性时发生错误: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取索引中的文档数量
     */
    protected long getDocumentCount() {
        try {
            return elasticsearchRestTemplate.count(
                new org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder().build(),
                indexCoordinates
            );
        } catch (Exception e) {
            log.warn("获取文档数量时发生错误: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 清理索引中的所有文档
     */
    protected void clearIndex() {
        try {
            // 删除所有文档
            elasticsearchRestTemplate.delete(
                new org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder()
                    .withQuery(org.elasticsearch.index.query.QueryBuilders.matchAllQuery())
                    .build(),
                indexCoordinates
            );
            refreshIndex();
            log.info("清理索引文档成功: {}", indexName);
        } catch (Exception e) {
            log.warn("清理索引文档失败: {}", e.getMessage());
        }
    }

    /**
     * 在测试类开始前执行
     * 可以选择在这里创建索引，或者使用懒加载模式
     */
    @BeforeAll
    void setUpClass() {
        log.info("=== 测试类 {} 开始 ===", this.getClass().getSimpleName());
        // 注意：这里不创建索引，使用懒加载模式
    }

    /**
     * 在测试类结束后执行
     * 清理索引
     */
    @AfterAll
    void tearDownClass() {
        log.info("=== 测试类 {} 结束，清理索引 ===", this.getClass().getSimpleName());
        deleteIndex();
    }

    /**
     * 在每个测试方法前执行
     * 确保索引存在，清理旧数据
     */
    @BeforeEach
    void setUp() {
        log.debug("=== 测试方法开始，准备索引 ===");
        createIndexIfNotExists();
        clearIndex(); // 清理旧数据，确保测试环境干净
    }

    /**
     * 在每个测试方法后执行
     * 可以选择清理数据或保留数据
     */
    @AfterEach
    void tearDown() {
        log.debug("=== 测试方法结束 ===");
        // 注意：这里不删除索引，让索引在测试类结束后统一清理
        // 这样可以提高测试效率，避免重复创建索引
    }

    /**
     * 创建临时索引用于特定测试
     * 返回临时索引的坐标，测试完成后会自动清理
     */
    protected IndexCoordinates createTempIndex() {
        String tempIndexName = "temp_" + indexName + "_" + System.currentTimeMillis();
        IndexCoordinates tempIndex = IndexCoordinates.of(tempIndexName);

        try {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(tempIndex);
            if (indexOps.exists()) {
                indexOps.delete();
            }
            indexOps.create();
            log.info("创建临时索引: {}", tempIndexName);
        } catch (Exception e) {
            log.error("创建临时索引失败: {}", e.getMessage(), e);
            throw new RuntimeException("无法创建临时索引: " + tempIndexName, e);
        }

        return tempIndex;
    }

    /**
     * 删除临时索引
     */
    protected void deleteTempIndex(IndexCoordinates tempIndex) {
        try {
            IndexOperations indexOps = elasticsearchRestTemplate.indexOps(tempIndex);
            if (indexOps.exists()) {
                indexOps.delete();
                log.info("删除临时索引: {}", tempIndex.getIndexName());
            }
        } catch (Exception e) {
            log.warn("删除临时索引失败: {}", e.getMessage());
        }
    }

    /**
     * 等待索引刷新
     * 在某些情况下，需要等待索引刷新才能看到最新数据
     */
    protected void waitForIndexRefresh() {
        try {
            Thread.sleep(100); // 短暂等待
            refreshIndex();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("等待索引刷新时被中断");
        }
    }

    /**
     * 获取测试统计信息
     */
    protected void logTestStats() {
        long docCount = getDocumentCount();
        boolean exists = indexExists();
        log.info("测试统计 - 索引: {}, 存在: {}, 文档数: {}", indexName, exists, docCount);
    }
}
