# Spring Boot 3 + Elasticsearch 7 中文分词示例

## 项目简介

本项目演示了在Spring Boot 3环境中使用Elasticsearch 7，并结合IK和拼音分词插件进行中文搜索的最佳实践。

## 技术栈

- Spring Boot 3.5.3
- Elasticsearch 7.10.1
- IK分词器 (ik_max_word, ik_smart)
- 拼音分词器 (pinyin, pinyin_first_letter)

## 中文分词配置

### 1. IK分词器安装

```bash
# 下载对应版本的IK分词器
wget https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.10.1/elasticsearch-analysis-ik-7.10.1.zip

# 解压到ES插件目录
unzip elasticsearch-analysis-ik-7.10.1.zip -d /path/to/elasticsearch/plugins/ik/

# 重启ES服务
```

### 2. 拼音分词器安装

```bash
# 下载拼音分词器
wget https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v7.10.1/elasticsearch-analysis-pinyin-7.10.1.zip

# 解压到ES插件目录
unzip elasticsearch-analysis-pinyin-7.10.1.zip -d /path/to/elasticsearch/plugins/pinyin/

# 重启ES服务
```

### 3. 分词器类型说明

#### IK分词器
- **ik_max_word**: 最细粒度分词，会将文本做最细粒度的拆分
- **ik_smart**: 智能分词，会做最粗粒度的拆分

#### 拼音分词器
- **pinyin**: 将中文转换为拼音
- **pinyin_first_letter**: 提取拼音首字母

## 字段分词策略

| 字段 | 分词策略 | 说明 |
|------|----------|------|
| name | ik_max_word | 商家名称支持中文分词搜索 |
| address | ik_max_word | 地址支持中文分词搜索 |
| description | ik_max_word | 商家描述全文搜索 |
| tags | keyword | 标签精确匹配 |
| mainProducts | keyword | 主营商品精确匹配 |

## 测试用例说明

### 基础操作 (Order 1-2)
- 创建索引
- 插入测试数据

### 基础查询 (Order 3-4)
- 查询所有商家
- 按ID精确查询

### 中文分词查询 (Order 5-8)
- **商家名称分词查询**: 测试"按摩"关键词搜索
- **地址分词查询**: 测试"高新区"地址搜索
- **商家描述分词查询**: 测试"专业"描述搜索
- **多字段分词查询**: 在名称和地址中搜索"连锁"

### 排序和分页 (Order 9-10)
- 按ID升序排序
- 分页查询

### 聚合查询 (Order 11-12)
- 按区域聚合统计
- 按评分区间聚合

### 地理查询 (Order 13-14)
- 地理距离查询
- 地理距离排序

### 高级查询 (Order 15-16)
- 复合查询（布尔查询）
- 高亮查询

### 业务场景查询 (Order 17-25)
- 按销量排序
- 查询VIP商家
- 查询支持配送且起送金额低于30元的商家
- 查询某地理范围内销量最高的商家
- 查询新入驻商家
- 查询评价数最多的商家
- 查询主营某商品的商家
- 查询收藏数前N的商家
- 按标签筛选商家

### 清理操作 (Order 26)
- 清理测试数据

### 度量聚合测试
- `SimpleMetricsAggregationTests.testMetricsAggregations()`: 完整流程测试（推荐）
- `MetricsAggregationTests`: 分步骤测试（带执行顺序）

### 数据同步测试
- `testEventDrivenSyncCreate()`: 事件驱动同步-创建测试
- `testEventDrivenSyncUpdate()`: 事件驱动同步-更新测试
- `testEventDrivenSyncDelete()`: 事件驱动同步-删除测试
- `testBatchSync()`: 批量同步测试
- `testForceSync()`: 强制同步测试

## 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=BusinessInfoTests#chineseNameQuery

# 运行中文分词相关测试
mvn test -Dtest=BusinessInfoTests#chineseNameQuery,chineseAddressQuery,chineseDescriptionQuery,multiFieldChineseQuery
```

## 中文分词效果示例

### 输入文本
```
"筋骨堂热敷推拿按摩（锦城万达店）"
```

### IK分词结果
```
筋骨堂, 热敷, 推拿, 按摩, 锦城, 万达, 店
```

### 搜索效果
- 搜索"按摩" → 匹配成功
- 搜索"推拿" → 匹配成功
- 搜索"筋骨堂" → 匹配成功
- 搜索"万达" → 匹配成功

## 注意事项

1. **ES版本兼容性**: 确保IK和拼音分词器版本与ES版本一致
2. **词典更新**: 可以自定义词典来优化分词效果
3. **性能考虑**: ik_max_word分词粒度更细，但索引体积更大
4. **搜索优化**: 建议对重要字段使用多字段映射（text + keyword）

## 数据同步方案

### 1. 事件驱动同步（推荐）

#### 核心组件
- `BusinessInfoEvent`: 商家信息事件类
- `ESBusinessInfoSyncService`: ES同步服务
- `BusinessInfoService`: 业务服务层
- `AsyncConfig`: 异步配置

#### 工作原理
1. 业务操作触发事件发布
2. 异步事件监听器处理ES同步
3. 独立的线程池处理同步任务
4. 支持重试和错误处理

#### 优势
- **解耦**: 业务逻辑与ES同步完全分离
- **异步**: 不阻塞主业务流程
- **可靠**: 支持重试和错误处理
- **灵活**: 支持批量操作和手动同步

### 2. 消息队列方案（高并发场景）

#### 适用场景
- 高并发写入场景
- 需要消息持久化
- 跨服务数据同步

#### 实现方式
- 使用RabbitMQ、Kafka等消息队列
- 消息包含操作类型和数据
- 支持消息重试和死信队列

### 3. 定时任务同步

#### 适用场景
- 数据一致性要求不高
- 批量数据同步
- 数据修复场景

#### 实现方式
- 定时扫描数据库变更
- 批量同步到ES
- 支持增量同步

## 同步策略选择

| 场景 | 推荐方案 | 原因 |
|------|----------|------|
| 一般业务 | 事件驱动 | 简单可靠，性能好 |
| 高并发 | 消息队列 | 削峰填谷，可靠性高 |
| 数据修复 | 定时任务 | 实现简单，成本低 |
| 实时性要求高 | 事件驱动 | 延迟最低 |

## 扩展功能

- 支持同义词搜索
- 支持拼音搜索
- 支持首字母搜索
- 支持模糊匹配
- 支持高亮显示
- 支持数据同步监控
- 支持同步失败告警
- 支持数据一致性校验

## 常见问题

### Q: 分词器安装后不生效？
A: 检查插件目录是否正确，重启ES服务

### Q: 中文搜索无结果？
A: 检查字段是否使用了正确的分词器，确认索引映射

### Q: 如何优化搜索性能？
A: 合理使用keyword字段，避免过度分词

### Q: 数据同步失败怎么办？
A: 检查ES连接，查看同步日志，使用强制同步功能

### Q: 如何保证数据一致性？
A: 定期运行数据一致性校验，设置监控告警


