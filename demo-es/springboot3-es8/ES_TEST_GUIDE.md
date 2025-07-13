# Elasticsearch 8 测试指南

## 项目概述

这是一个基于 Spring Boot 3 + Elasticsearch 8 的测试项目，使用汉堡王门店数据作为测试数据源，全面测试 Elasticsearch 的各种功能。

## 技术栈

- **Spring Boot**: 3.5.3
- **Elasticsearch**: 8.x
- **Spring Data Elasticsearch**: 最新版本
- **OpenCSV**: 5.11.2 (用于CSV数据解析)
- **JUnit 5**: 测试框架
- **Lombok**: 简化代码

## 测试数据

### 数据源
- 文件: `src/main/resources/bk_stores.csv`
- 内容: 汉堡王门店信息
- 记录数: 约1000+条

### 主要字段
| 字段名 | 类型 | 说明 |
|--------|------|------|
| storeId | Integer | 门店ID (主键) |
| storeProvince | String | 省份 |
| storeCity | String | 城市 |
| storeArea | String | 区域 |
| storeName | String | 门店名称 |
| storeAddress | String | 门店地址 |
| location | GeoPoint | 地理位置(经纬度) |
| storeNo | Integer | 门店编号 |
| hasBreakfast | Boolean | 是否有早餐 |
| useCard | Boolean | 是否可以用卡 |
| storeState | Boolean | 营业状态 |
| storePublish | Boolean | 发布状态 |

## 测试功能点

### 1. 基础CRUD操作测试 (BurgerKingMerchantCRUDTest)

#### 功能点
- ✅ **数据初始化**: 从CSV文件加载数据到ES
- ✅ **文档创建**: 插入单条商户数据
- ✅ **文档查询**: 根据ID查询文档
- ✅ **文档更新**: 修改商户信息
- ✅ **文档删除**: 删除测试数据
- ✅ **批量操作**: 批量插入多条数据
- ✅ **文档统计**: 统计文档数量
- ✅ **存在性检查**: 检查文档是否存在

#### 测试方法
```java
@Test
@Order(1)
@DisplayName("1. 数据初始化 - 从CSV加载数据到ES")
void testDataInitialization()
```

### 2. 搜索功能测试 (BurgerKingMerchantSearchTest)

#### 功能点
- ✅ **精确匹配**: 根据省份精确查询
- ✅ **范围搜索**: 根据门店编号范围查询
- ✅ **复合查询**: 多条件组合查询
- ✅ **分页查询**: 分页获取门店列表
- ✅ **排序查询**: 按门店名称排序
- ✅ **模糊搜索**: 根据地址模糊查询
- ✅ **多字段搜索**: 在多个字段中搜索
- ✅ **布尔值查询**: 查询有早餐的门店
- ✅ **空值查询**: 查询没有联系人的门店
- ✅ **统计查询**: 统计各省份门店数量

#### 测试方法
```java
@Test
@Order(1)
@DisplayName("1. 精确匹配 - 根据省份精确查询")
void testExactMatch()
```

### 3. 聚合查询测试 (BurgerKingMerchantAggregationTest)

#### 功能点
- ✅ **基础统计**: 统计总门店数量
- ✅ **分组统计**: 按省份/城市统计门店数量
- ✅ **数值统计**: 门店编号统计(最大值、最小值、平均值)
- ✅ **布尔值统计**: 统计有早餐的门店
- ✅ **复合统计**: 按省份统计有早餐的门店
- ✅ **范围统计**: 按门店编号范围统计
- ✅ **唯一值统计**: 统计不同省份数量
- ✅ **条件统计**: 统计营业状态
- ✅ **综合统计报告**: 生成完整的统计报告

#### 测试方法
```java
@Test
@Order(1)
@DisplayName("1. 基础统计 - 统计总门店数量")
void testBasicCount()
```

### 4. 地理位置搜索测试 (BurgerKingMerchantGeoTest)

#### 功能点
- ✅ **地理位置数据验证**: 验证地理位置数据完整性
- ✅ **地理位置范围查询**: 查询指定区域内的门店
- ✅ **地理位置距离查询**: 查询距离指定点一定范围内的门店
- ✅ **地理位置多边形查询**: 查询指定多边形区域内的门店
- ✅ **复合地理位置查询**: 地理位置+其他条件
- ✅ **地理位置排序**: 按距离排序
- ✅ **地理位置统计**: 统计各地区的门店分布
- ✅ **地理位置距离计算**: 计算两点间距离
- ✅ **地理位置数据质量检查**: 检查坐标有效性
- ✅ **地理位置查询性能测试**: 测试查询性能

#### 测试方法
```java
@Test
@Order(1)
@DisplayName("1. 地理位置数据验证")
void testGeoDataValidation()
```

## 运行测试

### 前置条件

1. **启动Elasticsearch服务**
   ```bash
   # 使用Docker启动ES
   docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 \
     -e "discovery.type=single-node" \
     -e "xpack.security.enabled=false" \
     elasticsearch:8.11.0
   ```

2. **检查ES服务状态**
   ```bash
   curl http://localhost:9200
   ```

3. **确保CSV数据文件存在**
   ```
   src/main/resources/bk_stores.csv
   ```

### 运行方式

#### 1. IDE运行
- 右键点击测试类 → Run
- 推荐运行顺序:
  1. `ElasticsearchTestSuite` (测试套件)
  2. `BurgerKingMerchantCRUDTest` (数据初始化)
  3. 其他测试类

#### 2. Maven命令行运行
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=BurgerKingMerchantCRUDTest

# 运行测试套件
mvn test -Dtest=ElasticsearchTestSuite

# 运行特定测试方法
mvn test -Dtest=BurgerKingMerchantCRUDTest#testDataInitialization
```

#### 3. 单个测试方法运行
```bash
# 运行数据初始化测试
mvn test -Dtest=BurgerKingMerchantCRUDTest#testDataInitialization

# 运行搜索测试
mvn test -Dtest=BurgerKingMerchantSearchTest#testExactMatch
```

## 测试结果

### 预期输出示例

```
=== Elasticsearch测试套件开始 ===
测试环境: Spring Boot 3 + Elasticsearch 8
测试数据: 汉堡王门店信息
测试范围: CRUD操作、搜索功能、聚合查询、地理位置搜索
=== 初始化完成 ===

开始从CSV文件加载数据到Elasticsearch...
CSV数据加载成功，共1131条记录
数据初始化完成，ES中共有1131条记录

测试精确匹配查询...
精确匹配查询完成，找到156条北京市的门店

测试范围搜索功能...
范围搜索完成，找到45条门店编号在19000-20000之间的门店

=== 汉堡王门店综合统计报告 ===
总门店数量: 1131
有早餐的门店: 856家 (75.7%)
可以用卡的门店: 1023家 (90.5%)
营业中的门店: 1105家 (97.7%)
按省份分布:
  北京市: 156家
  上海市: 89家
  天津市: 45家
=== 报告结束 ===
```

## 配置说明

### application.yml
```yaml
spring:
  application:
    name: springboot3-es8
  elasticsearch:
    uris: http://localhost:9200
    connection-timeout: 10s
    socket-timeout: 30s
    username:  # 如果ES启用了安全认证，请配置用户名
    password:  # 如果ES启用了安全认证，请配置密码
```

### 实体类配置
```java
@Document(indexName = "burger_king_merchant")
public class BurgerKingMerchant {
    @Id
    private Integer storeId;
    
    @Field(type = FieldType.Keyword)
    private String storeProvince;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String storeName;
    
    @GeoPointField
    private GeoPoint location;
    
    // ... 其他字段
}
```

## 常见问题

### 1. 连接ES失败
- 检查ES服务是否启动
- 检查端口9200是否可访问
- 检查防火墙设置

### 2. CSV数据加载失败
- 检查文件路径是否正确
- 检查文件编码是否为UTF-8
- 检查CSV格式是否正确

### 3. 地理位置查询无结果
- 检查location字段是否有数据
- 检查坐标格式是否正确
- 检查查询范围是否合理

### 4. 聚合查询异常
- 检查字段类型是否正确
- 检查数据是否为空
- 检查查询条件是否合理

## 扩展功能

### 1. 添加新的搜索功能
```java
@Test
@DisplayName("新增搜索功能")
void testNewSearchFeature() {
    // 实现新的搜索逻辑
}
```

### 2. 添加新的聚合功能
```java
@Test
@DisplayName("新增聚合功能")
void testNewAggregationFeature() {
    // 实现新的聚合逻辑
}
```

### 3. 添加性能测试
```java
@Test
@DisplayName("性能测试")
void testPerformance() {
    long startTime = System.currentTimeMillis();
    // 执行测试逻辑
    long endTime = System.currentTimeMillis();
    log.info("执行时间: {}ms", endTime - startTime);
}
```

## 总结

本测试套件全面覆盖了Elasticsearch的主要功能：

1. **基础操作**: CRUD、批量操作、文档管理
2. **搜索功能**: 精确匹配、模糊搜索、复合查询、分页排序
3. **聚合分析**: 分组统计、数值统计、条件统计
4. **地理位置**: 距离查询、范围查询、空间分析

通过这些测试，可以验证ES集群的功能完整性、性能表现和数据一致性，为生产环境的应用提供可靠的保障。 