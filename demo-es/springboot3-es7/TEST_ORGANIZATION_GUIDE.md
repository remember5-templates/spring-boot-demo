# ES测试组织最佳实践指南

## 🎯 测试组织原则

### 1. 测试独立性原则
- ✅ **每个测试独立运行**：不依赖其他测试的执行结果
- ✅ **自包含数据**：每个测试自己准备和清理数据
- ✅ **可重复执行**：测试可以多次运行而不产生副作用

### 2. 功能分组原则
- ✅ **按功能模块分组**：搜索、聚合、CRUD等
- ✅ **按复杂度分组**：基础功能、高级功能
- ✅ **按使用场景分组**：业务场景、技术场景

## 📁 推荐的测试文件组织

### 1. 基础操作测试
```
ESBasicOperationsTests.java
├── testCreateDocument()      # 创建文档
├── testReadDocument()        # 读取文档
├── testUpdateDocument()      # 更新文档
├── testDeleteDocument()      # 删除文档
└── testBulkOperations()      # 批量操作
```

### 2. 搜索功能测试
```
ESSearchFeatureTests.java
├── testBasicSearch()         # 基础搜索
├── testAdvancedSearch()      # 高级搜索
├── testGeoSearch()           # 地理位置搜索
├── testNestedSearch()        # 嵌套文档搜索
└── testScriptSearch()        # 脚本搜索
```

### 3. 聚合功能测试
```
ESAggregationFeatureTests.java
├── testMetricsAggregations() # 度量聚合
├── testBucketAggregations()  # 桶聚合
├── testNestedAggregations()  # 嵌套聚合
└── testGeoAggregations()     # 地理聚合
```

### 4. 高级功能测试
```
ESAdvancedFeatureTests.java
├── testIndexManagement()     # 索引管理
├── testAliasManagement()     # 别名管理
├── testMappingManagement()   # 映射管理
└── testPerformanceOptimization() # 性能优化
```

### 5. 数据同步测试
```
ESDataSyncTests.java
├── testEventDrivenSync()     # 事件驱动同步
├── testBatchSync()           # 批量同步
└── testForceSync()           # 强制同步
```

## 🚀 测试执行策略

### 1. 使用Maven Profiles
```xml
<profiles>
    <!-- 基础功能测试 -->
    <profile>
        <id>basic</id>
        <properties>
            <test.includes>**/ESBasicOperationsTests.java</test.includes>
        </properties>
    </profile>
    
    <!-- 搜索功能测试 -->
    <profile>
        <id>search</id>
        <properties>
            <test.includes>**/ESSearchFeatureTests.java</test.includes>
        </properties>
    </profile>
    
    <!-- 聚合功能测试 -->
    <profile>
        <id>aggregation</id>
        <properties>
            <test.includes>**/ESAggregationFeatureTests.java</test.includes>
        </properties>
    </profile>
    
    <!-- 完整测试套件 -->
    <profile>
        <id>full</id>
        <properties>
            <test.includes>**/*Tests.java</test.includes>
        </properties>
    </profile>
</profiles>
```

### 2. 命令行执行
```bash
# 运行基础功能测试
./mvnw test -Pbasic

# 运行搜索功能测试
./mvnw test -Psearch

# 运行聚合功能测试
./mvnw test -Paggregation

# 运行完整测试套件
./mvnw test -Pfull

# 运行特定测试类
./mvnw test -Dtest=ESBasicOperationsTests

# 运行特定测试方法
./mvnw test -Dtest=ESBasicOperationsTests#testCreateDocument
```

## 🏗️ 测试数据管理

### 1. 测试数据准备
```java
@BeforeEach
void setUp() {
    // 清理旧数据
    elasticsearchTemplate.delete(Query.findAll(), ESBusinessInfo.class);
    
    // 准备测试数据
    List<ESBusinessInfo> testData = createTestData();
    elasticsearchTemplate.save(testData);
    
    // 刷新索引
    elasticsearchTemplate.indexOps(ESBusinessInfo.class).refresh();
}

@AfterEach
void tearDown() {
    // 清理测试数据
    elasticsearchTemplate.delete(Query.findAll(), ESBusinessInfo.class);
}
```

### 2. 测试数据工厂
```java
@Component
public class TestDataFactory {
    
    public ESBusinessInfo createBusinessInfo() {
        // 创建测试数据
    }
    
    public List<ESBusinessInfo> createBusinessInfoList(int count) {
        // 创建测试数据列表
    }
}
```

## 📊 测试分类标签

### 1. 使用@Tag注解
```java
@Tag("basic")
@Tag("crud")
class ESBasicOperationsTests {
    
    @Test
    @Tag("create")
    void testCreateDocument() {
        // 测试创建文档
    }
    
    @Test
    @Tag("read")
    void testReadDocument() {
        // 测试读取文档
    }
}
```

### 2. 按标签执行测试
```bash
# 运行基础功能测试
./mvnw test -Dgroups=basic

# 运行CRUD相关测试
./mvnw test -Dgroups=crud

# 运行搜索相关测试
./mvnw test -Dgroups=search
```

## 🔧 测试配置管理

### 1. 测试配置文件
```yaml
# src/test/resources/application-test.yml
spring:
  elasticsearch:
    uris: http://localhost:9200
    connection-timeout: 30s
    socket-timeout: 30s

logging:
  level:
    org.elasticsearch: DEBUG
    com.remember5.demo: DEBUG
```

### 2. 测试类配置
```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
class ESBasicOperationsTests {
    // 测试实现
}
```

## 📈 测试执行优化

### 1. 并行执行
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        <perCoreThreadCount>true</perCoreThreadCount>
    </configuration>
</plugin>
```

### 2. 测试隔离
```java
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ESBasicOperationsTests {
    // 每个测试方法后重新创建应用上下文
}
```

## 🎯 最佳实践总结

### ✅ 推荐做法：
1. **按功能模块组织测试文件**
2. **每个测试独立运行**
3. **使用测试数据工厂**
4. **合理使用@Tag分类**
5. **配置测试专用环境**
6. **使用Maven Profiles管理执行**

### ❌ 避免做法：
1. **强制测试执行顺序**
2. **测试间数据依赖**
3. **在测试中使用@Order注解**
4. **测试文件过于庞大**
5. **测试数据硬编码**

## 🚀 执行示例

```bash
# 快速验证基础功能
./mvnw test -Dtest=ESBasicOperationsTests -DfailIfNoTests=false

# 验证搜索功能
./mvnw test -Dtest=ESSearchFeatureTests -DfailIfNoTests=false

# 验证聚合功能
./mvnw test -Dtest=ESAggregationFeatureTests -DfailIfNoTests=false

# 完整功能验证
./mvnw test -DfailIfNoTests=false
```

这种组织方式既保证了测试的独立性，又提供了灵活的执行策略，是ES测试的最佳实践。 