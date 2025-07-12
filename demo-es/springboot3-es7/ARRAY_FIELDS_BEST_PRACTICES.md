# Elasticsearch 数组字段最佳实践

## 概述

在Elasticsearch中，数组字段不能为null，这是一个重要的设计原则。本文档介绍如何正确处理数组字段以避免常见问题。

## 核心原则

### 1. 数组字段不能为null
- Elasticsearch中的数组字段不能设置为null值
- 如果字段没有值，应该使用空数组`[]`
- Elasticsearch会自动将单个值转换为数组

### 2. 字段类型映射
```json
{
  "mappings": {
    "properties": {
      "tags": {
        "type": "keyword"  // 数组字段的类型
      },
      "mainProducts": {
        "type": "keyword"
      }
    }
  }
}
```

## 解决方案

### 1. 实体类字段初始化

在Java实体类中，为所有数组字段提供默认值：

```java
@Field(type = FieldType.Keyword)
private List<String> tags = new ArrayList<>();

@Field(type = FieldType.Keyword)
private List<String> mainProducts = new ArrayList<>();

@Field(type = FieldType.Keyword)
private List<String> paymentMethods = new ArrayList<>();
```

### 2. 使用工具类

创建工具类来处理数组字段的null值问题：

```java
public class ElasticsearchUtils {
    
    // 确保List不为null
    public static <T> List<T> ensureNotNull(List<T> list) {
        return list != null ? list : new ArrayList<>();
    }
    
    // 安全地添加元素
    public static <T> List<T> safeAdd(List<T> list, T item) {
        List<T> result = ensureNotNull(list);
        if (item != null) {
            result.add(item);
        }
        return result;
    }
}
```

### 3. 查询时的注意事项

#### 精确匹配查询
```java
// 查询包含特定标签的商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("tags", "连锁"))
    .build();
```

#### 存在性查询
```java
// 查询有标签的商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.existsQuery("tags"))
    .build();
```

#### 数组大小查询
```java
// 查询标签数量大于1的商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.scriptQuery(
        new Script(ScriptType.INLINE, "painless", 
            "doc['tags'].size() > 1", Collections.emptyMap())))
    .build();
```

## 常见问题及解决方案

### 1. NullPointerException
**问题**: 尝试访问null的数组字段
```java
// 错误示例
List<String> tags = business.getTags();
tags.add("新标签"); // 可能抛出NullPointerException
```

**解决方案**: 使用工具类或默认值
```java
// 正确示例
List<String> tags = ElasticsearchUtils.ensureNotNull(business.getTags());
tags.add("新标签");
```

### 2. 映射错误
**问题**: 字段映射为数组但传入null值
```java
// 错误示例
EsBusinessInfo.builder()
    .tags(null) // 这会导致问题
    .build();
```

**解决方案**: 使用空数组
```java
// 正确示例
EsBusinessInfo.builder()
    .tags(new ArrayList<>()) // 或使用默认值
    .build();
```

### 3. 查询不匹配
**问题**: 查询null数组字段
```java
// 可能不工作的查询
QueryBuilders.termQuery("tags", "连锁") // 如果tags为null
```

**解决方案**: 先检查字段存在性
```java
// 正确的查询方式
BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
    .must(QueryBuilders.existsQuery("tags"))
    .must(QueryBuilders.termQuery("tags", "连锁"));
```

## 最佳实践总结

1. **初始化**: 所有数组字段都应该有默认值（空ArrayList）
2. **工具类**: 使用工具类安全地操作数组字段
3. **查询**: 查询前检查字段存在性
4. **文档**: 在代码注释中说明数组字段的处理方式
5. **测试**: 编写测试验证数组字段的正确处理

## 示例代码

### 实体类定义
```java
@Data
@Document(indexName = "business_info")
public class EsBusinessInfo {
    
    @Field(type = FieldType.Keyword)
    private List<String> tags = new ArrayList<>();
    
    @Field(type = FieldType.Keyword)
    private List<String> mainProducts = new ArrayList<>();
    
    @Field(type = FieldType.Keyword)
    private List<String> paymentMethods = new ArrayList<>();
}
```

### 查询示例
```java
// 查询支持支付宝的商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.termQuery("paymentMethods", "支付宝"))
    .build();

// 查询有标签的商家
NativeSearchQuery query = new NativeSearchQueryBuilder()
    .withQuery(QueryBuilders.existsQuery("tags"))
    .build();
```

### 数据插入示例
```java
EsBusinessInfo business = EsBusinessInfo.builder()
    .id(1)
    .name("测试商家")
    .tags(Arrays.asList("连锁", "24小时"))
    .mainProducts(Arrays.asList("按摩", "理疗"))
    .paymentMethods(Arrays.asList("支付宝", "微信"))
    .build();
```

## 注意事项

1. **性能**: 数组字段的查询可能比普通字段慢
2. **索引**: 数组中的每个元素都会被索引
3. **存储**: 数组字段会占用更多存储空间
4. **聚合**: 数组字段的聚合结果可能不符合预期

通过遵循这些最佳实践，可以避免Elasticsearch数组字段的常见问题，确保应用程序的稳定性和性能。 