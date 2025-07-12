# 电商平台搜索功能指南

## 概述

本指南详细介绍了基于Elasticsearch 7的大型电商平台搜索功能实现，包括商家搜索、商品搜索、综合搜索等完整用例。项目已配置IK中文分词器和拼音分词器，支持中文用户的搜索需求。

## 实体设计

### 1. 商家信息实体 (EsBusinessInfo)

#### 基础信息
- **商家ID**: 唯一标识
- **商家名称**: 支持IK分词搜索
- **联系方式**: 电话、地址等
- **商家类型**: 餐饮、电子产品、服装等
- **商家状态**: 正常、暂停、关闭等

#### 地理位置
- **省市区街道**: 支持地理位置搜索
- **详细地址**: 支持IK分词搜索
- **坐标**: GeoPoint类型，支持地理距离搜索

#### 业务数据
- **销量**: 总销量、月销量
- **评分**: 用户评分、评价数量
- **等级**: 商家等级（1-5星）
- **认证状态**: 已认证、未认证、认证中
- **信用分**: 商家信用评分

#### 服务信息
- **服务承诺**: 7天无理由退货、48小时发货等
- **支付方式**: 支付宝、微信、银行卡等
- **配送方式**: 自提、快递、同城配送等
- **营业时间**: 营业时间段
- **特色服务**: 商家特色服务列表

#### 标签系统
- **标签**: 连锁、老字号、24小时等
- **标签详情**: 嵌套类型，包含标签权重和描述
- **荣誉**: 金牌商家、诚信商家等
- **活动**: 参与的活动列表

### 2. 商品信息实体 (EsGoodsInfo)

#### 基础信息
- **商品ID**: 唯一标识
- **商家ID**: 关联商家
- **商品名称**: 支持IK分词搜索
- **副标题**: 商品简短描述
- **商品描述**: 详细描述，支持IK分词
- **品牌**: 商品品牌
- **型号**: 商品型号

#### 分类信息
- **分类ID**: 商品分类
- **分类名称**: 分类名称
- **分类路径**: 完整分类路径（如：电子产品/手机/智能手机）
- **商品类型**: 实物、虚拟、服务

#### 价格信息
- **原价**: 商品原价
- **现价**: 当前售价
- **最低价格**: 用于价格区间搜索
- **最高价格**: 用于价格区间搜索
- **运费**: 配送费用
- **包邮条件**: 满多少包邮

#### 库存销量
- **库存数量**: 当前库存
- **销量**: 总销量
- **月销量**: 月度销量
- **评分**: 用户评分
- **评价数量**: 评价总数
- **好评率**: 好评比例

#### 商品属性
- **重量**: 商品重量（克）
- **体积**: 商品体积（立方厘米）
- **尺寸**: 长x宽x高
- **颜色**: 可选颜色列表
- **尺寸**: 可选尺寸列表
- **材质**: 商品材质
- **产地**: 生产地

#### 服务支持
- **支持退货**: 是否支持7天无理由退货
- **支持货到付款**: 是否支持COD
- **支持分期**: 是否支持分期付款
- **预计发货时间**: 发货时间（小时）
- **预计到达时间**: 到达时间（天）

#### 商品状态
- **商品状态**: 上架、下架、删除
- **是否新品**: 新品标识
- **是否热销**: 热销标识
- **是否推荐**: 推荐标识
- **是否限时特价**: 特价标识

#### 规格SKU
- **商品规格**: 嵌套类型，包含规格名称和值
- **SKU列表**: 嵌套类型，包含SKU详细信息

## 搜索功能

### 1. 商家搜索

#### 按名称搜索
```java
// 搜索包含"餐厅"的商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.matchQuery("name", "餐厅"))
    .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
    .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

#### 按地理位置搜索
```java
// 搜索北京市的商家
BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
    .must(QueryBuilders.termQuery("city", "北京市"))
    .must(QueryBuilders.termQuery("status", "正常"));
```

#### 按商家类型搜索
```java
// 搜索餐饮类商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("businessType", "餐饮"))
    .withSort(SortBuilders.fieldSort("salesVolume").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

#### 按评分和销量搜索
```java
// 搜索评分大于4.5且销量大于1000的商家
BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
    .must(QueryBuilders.rangeQuery("rating").gte(4.5))
    .must(QueryBuilders.rangeQuery("salesVolume").gte(1000));
```

#### 按标签搜索
```java
// 搜索有"连锁"标签的商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("tags", "连锁"))
    .withSort(SortBuilders.fieldSort("businessLevel").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

### 2. 商品搜索

#### 按名称搜索
```java
// 搜索包含"手机"的商品
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.matchQuery("name", "手机"))
    .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
    .withSort(SortBuilders.fieldSort("salesVolume").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

#### 按价格区间搜索
```java
// 搜索价格在1000-5000之间的商品
BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
    .must(QueryBuilders.rangeQuery("currentPrice").gte(1000).lte(5000))
    .must(QueryBuilders.termQuery("status", "上架"));
```

#### 按分类搜索
```java
// 搜索电子产品分类的商品
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("categoryName", "电子产品"))
    .withSort(SortBuilders.fieldSort("rating").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

#### 按品牌搜索
```java
// 搜索苹果品牌的商品
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("brand", "苹果"))
    .withSort(SortBuilders.fieldSort("salesVolume").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

#### 按商品属性搜索
```java
// 搜索红色商品
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("colors", "红色"))
    .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

### 3. 综合搜索

#### 智能推荐搜索
```java
// 基于用户行为的智能推荐
BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
    .must(QueryBuilders.rangeQuery("rating").gte(4.5))
    .must(QueryBuilders.rangeQuery("salesVolume").gte(500))
    .should(QueryBuilders.termQuery("isNew", true))
    .should(QueryBuilders.termQuery("isHot", true))
    .should(QueryBuilders.termQuery("isRecommend", true))
    .minimumShouldMatch(1);
```

#### 热门搜索
```java
// 搜索热销商品
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("isHot", true))
    .withSort(SortBuilders.fieldSort("monthlySales").order(SortOrder.DESC))
    .withPageable(PageRequest.of(0, 10))
    .build();
```

#### 搜索建议
```java
// 基于商品名称的搜索建议
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.prefixQuery("name", "手"))
    .withPageable(PageRequest.of(0, 5))
    .build();
```

## 聚合分析

### 商家聚合
```java
// 按城市统计商家数量
TermsAggregationBuilder cityAgg = AggregationBuilders.terms("city_stats").field("city");

// 按商家类型统计平均评分
TermsAggregationBuilder typeAgg = AggregationBuilders.terms("type_stats").field("businessType");
AvgAggregationBuilder avgRatingAgg = AggregationBuilders.avg("avg_rating").field("rating");
typeAgg.subAggregation(avgRatingAgg);
```

### 商品聚合
```java
// 按品牌统计商品数量和平均价格
TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_stats").field("brand");
AvgAggregationBuilder avgPriceAgg = AggregationBuilders.avg("avg_price").field("currentPrice");
SumAggregationBuilder totalSalesAgg = AggregationBuilders.sum("total_sales").field("salesVolume");
brandAgg.subAggregation(avgPriceAgg);
brandAgg.subAggregation(totalSalesAgg);
```

## 中文搜索优化

### IK分词器配置
- **ik_max_word**: 最细粒度分词，用于索引
- **ik_smart**: 智能分词，用于搜索
- **pinyin**: 拼音分词，支持拼音搜索

### 字段分析器配置
```java
// 商家名称：支持IK分词和拼音搜索
@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
private String name;

// 商品描述：支持IK分词
@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
private String description;
```

## 测试用例

### 运行测试
```bash
# 运行所有电商搜索测试
./mvnw test -Dtest=EcommerceSearchTests

# 运行简化版测试
./mvnw test -Dtest=SimpleEcommerceSearchTests

# 按标签运行测试
./mvnw test -Dgroups=ecommerce
```

### 测试覆盖范围
1. **数据准备**: 创建索引、插入测试数据
2. **商家搜索**: 名称、地理位置、类型、评分销量、标签搜索
3. **商品搜索**: 名称、价格、分类、品牌、属性搜索
4. **综合搜索**: 智能推荐、热门搜索、搜索建议
5. **数据清理**: 删除测试数据

## 性能优化建议

### 索引优化
1. **分片设置**: 根据数据量设置合适的分片数
2. **副本设置**: 生产环境建议设置副本
3. **字段映射**: 合理设置字段类型和分析器

### 查询优化
1. **使用filter**: 对于不需要评分的查询使用filter
2. **分页优化**: 使用search_after代替深度分页
3. **聚合优化**: 合理使用聚合缓存

### 缓存策略
1. **查询缓存**: 缓存常用查询结果
2. **聚合缓存**: 缓存聚合分析结果
3. **索引缓存**: 合理设置索引缓存大小

## 扩展功能

### 地理位置搜索
- 支持按距离搜索商家
- 支持地理围栏搜索
- 支持路线规划相关搜索

### 个性化推荐
- 基于用户历史行为的推荐
- 基于协同过滤的推荐
- 基于内容的推荐

### 实时搜索
- 支持搜索建议
- 支持搜索历史
- 支持热门搜索词

### 多语言支持
- 支持中英文混合搜索
- 支持拼音搜索
- 支持模糊搜索

## 总结

本电商平台搜索功能提供了完整的商家和商品搜索解决方案，支持中文用户的搜索需求，具备良好的扩展性和性能。通过合理的实体设计和搜索策略，可以满足大型电商平台的各种搜索场景。 