package com.remember5.demo.springboot3.es7;

import com.remember5.demo.springboot3.es7.entity.ESBusinessInfo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.aggregations.metrics.Min;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.Arrays;
import java.util.Date;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * 简化度量聚合测试
 * 确保数据正确写入和执行顺序
 *
 * @author wangjiahao
 * @date 2025/1/27
 */
@Slf4j
@DisplayName("简化度量聚合测试")
@SpringBootTest(classes = DemoSpringboot3Es7Application.class)
class SimpleMetricsAggregationTests {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Test
    @DisplayName("测试度量聚合 - 完整流程")
    void testMetricsAggregations() throws InterruptedException {
        log.info("=== 开始度量聚合测试 ===");
        
        // 1. 清理现有数据
        log.info("1. 清理现有数据");
        elasticsearchTemplate.delete(new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(), ESBusinessInfo.class);
        Thread.sleep(1000);
        
        // 2. 创建测试数据
        log.info("2. 创建测试数据");
        ESBusinessInfo business1 = ESBusinessInfo.builder()
                .id(1)
                .name("川菜馆")
                .businessType("餐饮服务")
                .area("高新区")
                .rating(4.5)
                .salesVolume(1000L)
                .reviewCount(200)
                .favoriteCount(150L)
                .isVip(true)
                .deliverySupport(true)
                .minOrderAmount(20.0)
                .avgDeliveryTime(30)
                .createTime(new Date())
                .build();

        ESBusinessInfo business2 = ESBusinessInfo.builder()
                .id(2)
                .name("火锅店")
                .businessType("餐饮服务")
                .area("锦江区")
                .rating(4.8)
                .salesVolume(2000L)
                .reviewCount(500)
                .favoriteCount(300L)
                .isVip(true)
                .deliverySupport(true)
                .minOrderAmount(50.0)
                .avgDeliveryTime(45)
                .createTime(new Date())
                .build();

        ESBusinessInfo business3 = ESBusinessInfo.builder()
                .id(3)
                .name("服装店")
                .businessType("零售服务")
                .area("武侯区")
                .rating(4.2)
                .salesVolume(500L)
                .reviewCount(100)
                .favoriteCount(80L)
                .isVip(false)
                .deliverySupport(false)
                .minOrderAmount(0.0)
                .avgDeliveryTime(0)
                .createTime(new Date())
                .build();

        // 3. 保存数据到ES
        log.info("3. 保存数据到ES");
        Iterable<ESBusinessInfo> savedBusinesses = elasticsearchTemplate.save(Arrays.asList(business1, business2, business3));
        
        int savedCount = 0;
        for (ESBusinessInfo saved : savedBusinesses) {
            savedCount++;
            log.info("  保存商家: ID={}, 名称={}, 评分={}, 销量={}", 
                    saved.getId(), saved.getName(), saved.getRating(), saved.getSalesVolume());
        }
        log.info("  共保存 {} 条数据", savedCount);
        
        // 4. 等待索引刷新
        log.info("4. 等待索引刷新");
        Thread.sleep(2000);
        
        // 5. 验证数据是否写入成功
        log.info("5. 验证数据写入");
        SearchHits<ESBusinessInfo> countHits = elasticsearchTemplate.search(
                new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(), 
                ESBusinessInfo.class
        );
        log.info("  ES中的数据数量: {}", countHits.getTotalHits());
        
        if (countHits.getTotalHits() == 0) {
            log.error("数据写入失败！");
            return;
        }
        
        // 6. 执行聚合查询
        log.info("6. 执行聚合查询");
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .addAggregation(AggregationBuilders.avg("avg_rating").field("rating"))
                .addAggregation(AggregationBuilders.max("max_sales").field("salesVolume"))
                .addAggregation(AggregationBuilders.min("min_order_amount").field("minOrderAmount"))
                .addAggregation(AggregationBuilders.sum("total_sales").field("salesVolume"))
                .addAggregation(AggregationBuilders.stats("rating_stats").field("rating"));

        SearchHits<ESBusinessInfo> searchHits = elasticsearchTemplate.search(queryBuilder.build(), ESBusinessInfo.class);
        Aggregations aggregations = searchHits.getAggregations();

        // 7. 解析聚合结果
        log.info("7. 解析聚合结果");
        if (aggregations != null) {
            // 基础聚合
            Avg avgRating = aggregations.get("avg_rating");
            log.info("  平均评分: {}", avgRating.getValue());

            Max maxSales = aggregations.get("max_sales");
            log.info("  最高销量: {}", maxSales.getValue());

            Min minOrderAmount = aggregations.get("min_order_amount");
            log.info("  最低起送金额: {}", minOrderAmount.getValue());

            Sum totalSales = aggregations.get("total_sales");
            log.info("  总销量: {}", totalSales.getValue());

            // 统计聚合
            Stats ratingStats = aggregations.get("rating_stats");
            log.info("  评分统计:");
            log.info("    平均值: {}", ratingStats.getAvg());
            log.info("    最大值: {}", ratingStats.getMax());
            log.info("    最小值: {}", ratingStats.getMin());
            log.info("    总和: {}", ratingStats.getSum());
            log.info("    数量: {}", ratingStats.getCount());
        } else {
            log.error("聚合结果为空！");
        }
        
        // 8. 清理测试数据
        log.info("8. 清理测试数据");
        elasticsearchTemplate.delete(new NativeSearchQueryBuilder().withQuery(matchAllQuery()).build(), ESBusinessInfo.class);
        
        log.info("=== 度量聚合测试完成 ===");
    }
} 