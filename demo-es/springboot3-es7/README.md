# Spring Boot 3 + Elasticsearch 7 完整功能演示

## 📋 项目概述

这是一个基于 Spring Boot 3 和 Elasticsearch 7 的完整功能演示项目，涵盖了 ES7 的绝大部分核心功能和高级特性。

## 🎯 主要功能

### 1. 基础CRUD操作
- ✅ 文档的创建、读取、更新、删除
- ✅ 批量操作支持
- ✅ 索引管理（创建、删除、映射）

### 2. 搜索功能
- ✅ 全文搜索、术语搜索、范围搜索
- ✅ 模糊搜索、通配符搜索、正则表达式搜索
- ✅ 多字段搜索、布尔查询、复合查询
- ✅ 地理位置搜索（距离、边界框、多边形）
- ✅ 嵌套文档搜索
- ✅ 脚本查询（Painless脚本）

### 3. 聚合功能
- ✅ 度量聚合（avg、max、min、sum、stats等）
- ✅ 桶聚合（terms、histogram、range等）
- ✅ 嵌套聚合
- ✅ 地理聚合（geo_distance、geohash_grid等）
- ✅ 过滤器聚合
- ✅ 管道聚合（部分支持）

### 4. 高级功能
- ✅ 中文分词支持（IK分词器）
- ✅ 拼音搜索支持
- ✅ 高亮搜索
- ✅ 排序和分页
- ✅ 索引别名管理
- ✅ 数据同步方案

### 5. 电商平台搜索
- ✅ 商家信息搜索（名称、地理位置、类型、评分等）
- ✅ 商品信息搜索（名称、价格、分类、品牌、属性等）
- ✅ 综合搜索（智能推荐、热门搜索、搜索建议）
- ✅ 聚合分析（商家统计、商品统计、销售分析）
- ✅ 中文用户优化（IK分词、拼音搜索）

## 🏗️ 项目结构

```
demo-springboot3-es7/
├── src/main/java/
│   └── com/remember5/demo/springboot3/es7/
│       ├── DemoSpringboot3Es7Application.java
│       ├── entity/
│       │   ├── EsBusinessInfo.java          # 商家信息实体
│       │   └── EsGoodsInfo.java             # 商品信息实体
│       ├── repository/
│       │   └── BusinessInfoRepository.java  # 数据访问层
│       └── service/
│           └── BusinessInfoService.java     # 业务服务层
├── src/test/java/
│   └── com/remember5/demo/springboot3/es7/
│       ├── es/                              # ES功能测试
│       │   ├── BusinessInfoTests.java       # 基础CRUD和查询测试
│       │   ├── MetricsAggregationTests.java # 度量聚合测试
│       │   ├── SimpleMetricsAggregationTests.java # 简化聚合测试
│       │   ├── PipelineAggregationTests.java # 聚合功能测试
│       │   ├── SearchSuggestTests.java      # 搜索功能测试
│       │   ├── AdvancedES7FeaturesTests.java # 高级功能测试
│       │   └── BusinessInfoSyncTests.java   # 数据同步测试
│       └── biz/                             # 业务功能测试
│           ├── BusinessInfoTests.java       # 商家信息测试
│           ├── BusinessInfoSyncTests.java   # 商家同步测试
│           └── SimpleEcommerceSearchTests.java # 电商搜索测试
└── src/main/resources/
    └── application.yml                      # 配置文件
```

## 🚀 快速开始

### 环境要求
- Java 17+
- Elasticsearch 7.x
- Maven 3.6+

### 1. 启动Elasticsearch
```bash
# 使用Docker启动ES7
docker run -d --name elasticsearch \
  -p 9200:9200 -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "xpack.security.enabled=false" \
  elasticsearch:7.17.0
```

### 2. 安装IK和拼音插件
```bash
# 进入ES容器
docker exec -it elasticsearch bash

# 安装IK分词器
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.17.0/elasticsearch-analysis-ik-7.17.0.zip

# 安装拼音分词器
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v7.17.0/elasticsearch-analysis-pinyin-7.17.0.zip

# 重启ES
exit
docker restart elasticsearch
```

### 3. 运行测试
```bash
# 进入项目目录
cd demo-springboot3-es7

# 运行所有测试
./mvnw test -DskipTests=false

# 按功能分类运行测试
./mvnw test -Dgroups=basic      # 基础功能测试
./mvnw test -Dgroups=search     # 搜索功能测试
./mvnw test -Dgroups=aggregation # 聚合功能测试
./mvnw test -Dgroups=crud       # CRUD操作测试

# 运行特定测试类
./mvnw test -Dtest=BusinessInfoTests
./mvnw test -Dtest=MetricsAggregationTests
./mvnw test -Dtest=AdvancedES7FeaturesTests
./mvnw test -Dtest=SimpleEcommerceSearchTests

# 运行特定测试方法
./mvnw test -Dtest=BusinessInfoTests#testCreateDocument

# 运行电商搜索测试
./mvnw test -Dtest=SimpleEcommerceSearchTests
./mvnw test -Dgroups=ecommerce-simple
```

## 📊 功能覆盖情况

### 总体覆盖率: 95%

| 功能类别 | 覆盖率 | 状态 |
|---------|--------|------|
| 基础搜索 | 100% | ✅ |
| 聚合功能 | 95% | ✅ |
| 文档操作 | 100% | ✅ |
| 地理位置 | 100% | ✅ |
| 嵌套文档 | 100% | ✅ |
| 脚本功能 | 90% | ✅ |
| 管道聚合 | 80% | ⚠️ |
| 搜索建议 | 85% | ⚠️ |

详细覆盖情况请参考：[ES_FEATURES_COVERAGE.md](ES_FEATURES_COVERAGE.md)

电商平台搜索功能详细说明请参考：[ECOMMERCE_SEARCH_GUIDE.md](ECOMMERCE_SEARCH_GUIDE.md)

## 🔧 核心特性

### 1. 中文分词支持
```java
// 实体字段配置
@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
private String businessName;

@Field(type = FieldType.Text, analyzer = "pinyin", searchAnalyzer = "ik_smart")
private String businessNamePinyin;
```

### 2. 嵌套文档支持
```java
// 嵌套字段定义
@Field(type = FieldType.Nested)
private List<TagInfo> tagsDetail;

// 嵌套查询示例
nestedQuery("tagsDetail", 
    boolQuery()
        .must(matchQuery("tagsDetail.tag", "热门"))
        .must(rangeQuery("tagsDetail.weight").gte(0.8)),
    ScoreMode.Avg)
```

### 3. 地理位置功能
```java
// 地理点字段
@GeoPointField
private GeoPoint coordinate;

// 地理距离查询
geoDistanceQuery("coordinate")
    .point(30.58, 104.06)
    .distance("5km")
```

### 4. 脚本查询
```java
// Painless脚本查询
scriptQuery(new Script(ScriptType.INLINE, "painless", 
    "doc['rating'].value * doc['salesVolume'].value > 10000", null))
```

## 📝 测试用例说明

### 测试文件组织结构

按照功能模块组织测试文件，提供完整的Elasticsearch功能覆盖：

```
📁 测试文件组织
├── ESBasicOperationsTests.java      # 基础操作测试
│   ├── testCreateDocument()         # 创建文档
│   ├── testReadDocument()           # 读取文档
│   ├── testUpdateDocument()         # 更新文档
│   ├── testDeleteDocument()         # 删除文档
│   └── testBulkOperations()         # 批量操作
│
├── ESSearchFeatureTests.java        # 搜索功能测试
│   ├── testBasicSearch()            # 基础搜索
│   ├── testAdvancedSearch()         # 高级搜索
│   ├── testGeoSearch()              # 地理位置搜索
│   ├── testNestedSearch()           # 嵌套文档搜索
│   └── testScriptSearch()           # 脚本搜索
│
├── ESAggregationFeatureTests.java   # 聚合功能测试
│   ├── testMetricsAggregations()    # 度量聚合
│   ├── testBucketAggregations()     # 桶聚合
│   ├── testNestedAggregations()     # 嵌套聚合
│   └── testGeoAggregations()        # 地理聚合
│
├── ESAdvancedFeatureTests.java      # 高级功能测试
│   ├── testIndexManagement()        # 索引管理
│   ├── testAliasManagement()        # 别名管理
│   ├── testMappingManagement()      # 映射管理
│   └── testPerformanceOptimization() # 性能优化
│
└── ESDataSyncTests.java             # 数据同步测试
    ├── testEventDrivenSync()        # 事件驱动同步
    ├── testBatchSync()              # 批量同步
    └── testForceSync()              # 强制同步
```

### 运行测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试类
./mvnw test -Dtest=ESBasicOperationsTests
./mvnw test -Dtest=ESSearchFeatureTests
./mvnw test -Dtest=ESAggregationFeatureTests
./mvnw test -Dtest=ESAdvancedFeatureTests
./mvnw test -Dtest=ESDataSyncTests

# 运行特定测试方法
./mvnw test -Dtest=ESBasicOperationsTests#testCreateDocument

# 按标签运行测试
./mvnw test -Dgroups=basic
./mvnw test -Dgroups=search
./mvnw test -Dgroups=aggregation
./mvnw test -Dgroups=advanced
./mvnw test -Dgroups=management
./mvnw test -Dgroups=sync
```

### 测试分类

- **基础测试** (`@Tag("basic")`): 基础CRUD操作、批量操作
- **搜索测试** (`@Tag("search")`): 各种搜索功能、地理位置搜索、嵌套查询、脚本查询
- **聚合测试** (`@Tag("aggregation")`): 度量聚合、桶聚合、嵌套聚合、地理聚合
- **高级测试** (`@Tag("advanced")`): 高级搜索功能、脚本查询、函数评分
- **管理测试** (`@Tag("management")`): 索引管理、别名管理、映射管理、性能优化
- **同步测试** (`@Tag("sync")`): 事件驱动同步、批量同步、强制同步、增量同步

### 原有测试类说明

### 1. BusinessInfoTests
- 基础CRUD操作
- 各种查询类型演示
- 中文分词和拼音搜索
- 地理位置查询

### 2. MetricsAggregationTests
- 度量聚合（avg、max、min、sum、stats）
- 扩展统计聚合
- 百分位聚合
- 基数聚合

### 3. AdvancedES7FeaturesTests
- 嵌套文档查询和聚合
- 脚本查询和排序
- 地理网格聚合
- 索引别名管理
- 性能优化演示

### 4. PipelineAggregationTests
- 基础聚合功能
- 嵌套聚合
- 地理聚合
- 过滤器聚合

### 5. SearchSuggestTests
- 模糊搜索
- 通配符搜索
- 多字段搜索
- 复合查询
- 地理位置搜索
- 嵌套文档搜索
- 脚本查询
- 排序和分页

## 🔄 数据同步方案

项目实现了完整的数据同步方案：

### 1. 事件驱动同步
```java
// 发布事件
applicationEventPublisher.publishEvent(new BusinessInfoEvent(businessInfo));

// 监听事件
@EventListener
public void handleBusinessInfoEvent(BusinessInfoEvent event) {
    // 异步同步到ES
}
```

### 2. 批量同步
```java
// 批量索引
elasticsearchTemplate.save(businessInfoList);
```

### 3. 强制同步
```java
// 强制刷新索引
elasticsearchTemplate.indexOps(ESBusinessInfo.class).refresh();
```

## 🎨 实体设计

### ESBusinessInfo 实体包含：
- **基础信息**: ID、名称、类型、描述
- **业务数据**: 评分、销量、VIP状态
- **地理位置**: 坐标、地区
- **时间信息**: 创建时间、更新时间
- **嵌套文档**: 标签详情（支持复杂查询）
- **搜索优化**: 多字段映射、分词器配置

## 🔍 查询示例

### 1. 复合查询
```java
boolQuery()
    .must(matchQuery("businessType", "餐饮服务"))
    .should(matchQuery("area", "成都"))
    .mustNot(matchQuery("isVip", false))
    .filter(rangeQuery("rating").gte(4.0))
```

### 2. 地理位置查询
```java
geoDistanceQuery("coordinate")
    .point(30.58, 104.06)
    .distance("5km")
```

### 3. 嵌套文档查询
```java
nestedQuery("tagsDetail",
    boolQuery()
        .must(matchQuery("tagsDetail.tag", "热门"))
        .must(rangeQuery("tagsDetail.weight").gte(0.8)),
    ScoreMode.Avg)
```

### 4. 聚合查询
```java
// 按业务类型分组，计算平均评分
terms("business_types")
    .field("businessType")
    .subAggregation(avg("avg_rating").field("rating"))
```

## 🚨 注意事项

### 1. 插件安装
- 确保IK和拼音插件版本与ES版本匹配
- 安装插件后需要重启ES

### 2. 索引映射
- 首次运行会自动创建索引和映射
- 如需修改映射，需要重建索引

### 3. 测试数据
- 测试会自动创建和清理数据
- 确保ES连接正常

### 4. 性能优化
- 大量数据操作建议使用批量API
- 复杂查询注意性能影响

## 📚 参考资料

- [Elasticsearch 7.x 官方文档](https://www.elastic.co/guide/en/elasticsearch/reference/7.17/index.html)
- [Spring Data Elasticsearch 文档](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [IK分词器](https://github.com/medcl/elasticsearch-analysis-ik)
- [拼音分词器](https://github.com/medcl/elasticsearch-analysis-pinyin)

## 🤝 贡献

欢迎提交Issue和Pull Request来改进这个项目！

## 📄 许可证

本项目采用 MIT 许可证。


