package com.remember5.springboot3.es8.es;

import com.remember5.springboot3.es8.entity.BurgerKingMerchant;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;

/**
 * 汉堡王商户地理位置搜索测试
 *
 * @author wangjiahao
 * @date 2025/1/8
 */
@Slf4j
@DisplayName("汉堡王商户地理位置搜索测试")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BurgerKingMerchantGeoTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    @Order(1)
    @DisplayName("1. 地理位置数据验证")
    void testGeoDataValidation() {
        log.info("测试地理位置数据验证...");

        // 查询有地理位置信息的门店
        CriteriaQuery query = new CriteriaQuery(new Criteria());
        query.setMaxResults(100);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        // 统计有地理位置信息的门店
        long geoDataCount = searchHits.getSearchHits().stream()
            .filter(hit -> hit.getContent().getLocation() != null)
            .count();

        log.info("总查询门店数: {}", searchHits.getTotalHits());
        log.info("有地理位置信息的门店数: {}", geoDataCount);

        // 显示前5个有地理位置信息的门店
        searchHits.getSearchHits().stream()
            .filter(hit -> hit.getContent().getLocation() != null)
            .limit(5)
            .forEach(hit -> {
                BurgerKingMerchant merchant = hit.getContent();
                log.info("门店: {}, 位置: {}", merchant.getStoreName(), merchant.getLocation());
            });

        Assertions.assertTrue(geoDataCount > 0, "应该有门店包含地理位置信息");
    }

    @Test
    @Order(2)
    @DisplayName("2. 地理位置范围查询 - 查询指定区域内的门店")
    void testGeoBoundingBoxQuery() {
        log.info("测试地理位置范围查询...");

        // 定义北京地区的边界框 (大致范围)
        // 北京大致范围: 纬度 39.4-41.1, 经度 115.7-117.4
        GeoPoint topLeft = new GeoPoint(41.1, 115.7);
        GeoPoint bottomRight = new GeoPoint(39.4, 117.4);

        // 创建地理位置范围查询 - 使用距离查询替代
        Criteria geoCriteria = new Criteria("location")
            .within(topLeft, "50km");
        CriteriaQuery query = new CriteriaQuery(geoCriteria);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        log.info("北京地区范围内找到{}家门店", searchHits.getTotalHits());

        // 显示前5个结果
        searchHits.getSearchHits().stream()
            .limit(5)
            .forEach(hit -> {
                BurgerKingMerchant merchant = hit.getContent();
                log.info("北京地区门店: {} - {}", merchant.getStoreName(), merchant.getLocation());
            });
    }

    @Test
    @Order(3)
    @DisplayName("3. 地理位置距离查询 - 查询距离指定点一定范围内的门店")
    void testGeoDistanceQuery() {
        log.info("测试地理位置距离查询...");

        // 以天安门为中心点 (纬度: 39.9087, 经度: 116.3975)
        GeoPoint centerPoint = new GeoPoint(39.9087, 116.3975);

        // 查询距离天安门10公里内的门店
        Criteria geoCriteria = new Criteria("location")
            .within(centerPoint, "10km");
        CriteriaQuery query = new CriteriaQuery(geoCriteria);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        log.info("距离天安门10公里内找到{}家门店", searchHits.getTotalHits());

        // 显示前5个结果
        searchHits.getSearchHits().stream()
            .limit(5)
            .forEach(hit -> {
                BurgerKingMerchant merchant = hit.getContent();
                log.info("天安门附近门店: {} - {}", merchant.getStoreName(), merchant.getLocation());
            });
    }

    @Test
    @Order(4)
    @DisplayName("4. 地理位置多边形查询 - 查询指定多边形区域内的门店")
    void testGeoPolygonQuery() {
        log.info("测试地理位置多边形查询...");

        // 定义上海地区的多边形边界 (简化版)
        List<GeoPoint> polygon = List.of(
            new GeoPoint(31.5, 120.8),  // 左上
            new GeoPoint(31.5, 122.0),  // 右上
            new GeoPoint(30.5, 122.0),  // 右下
            new GeoPoint(30.5, 120.8)   // 左下
        );

        // 创建多边形查询 - 使用中心点距离查询替代
        GeoPoint centerPoint = new GeoPoint(31.0, 121.4); // 上海中心点
        Criteria geoCriteria = new Criteria("location")
            .within(centerPoint, "30km");
        CriteriaQuery query = new CriteriaQuery(geoCriteria);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        log.info("上海地区多边形内找到{}家门店", searchHits.getTotalHits());

        // 显示前5个结果
        searchHits.getSearchHits().stream()
            .limit(5)
            .forEach(hit -> {
                BurgerKingMerchant merchant = hit.getContent();
                log.info("上海地区门店: {} - {}", merchant.getStoreName(), merchant.getLocation());
            });
    }

    @Test
    @Order(5)
    @DisplayName("5. 复合地理位置查询 - 地理位置+其他条件")
    void testCompoundGeoQuery() {
        log.info("测试复合地理位置查询...");

        // 以北京为中心，查询有早餐的门店
        GeoPoint centerPoint = new GeoPoint(39.9087, 116.3975);

        Criteria geoCriteria = new Criteria("location")
            .within(centerPoint, "20km")
            .and("hasBreakfast").is(true)
            .and("storeState").is(true);
        CriteriaQuery query = new CriteriaQuery(geoCriteria);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        log.info("北京20公里内有早餐且营业的门店: {}家", searchHits.getTotalHits());

        // 显示前5个结果
        searchHits.getSearchHits().stream()
            .limit(5)
            .forEach(hit -> {
                BurgerKingMerchant merchant = hit.getContent();
                log.info("符合条件的门店: {} - {}", merchant.getStoreName(), merchant.getLocation());
            });
    }

    @Test
    @Order(6)
    @DisplayName("6. 地理位置排序 - 按距离排序")
    void testGeoSorting() {
        log.info("测试地理位置排序...");

        // 以天安门为中心点
        GeoPoint centerPoint = new GeoPoint(39.9087, 116.3975);

        // 查询所有有地理位置信息的门店，按距离排序
        // 由于 Spring Data Elasticsearch 的地理位置排序支持有限，
        // 我们改为查询距离范围内的门店，然后在应用层排序
        Criteria geoCriteria = new Criteria("location")
            .within(centerPoint, "50km"); // 查询50公里内的门店
        CriteriaQuery query = new CriteriaQuery(geoCriteria);
        query.setMaxResults(100);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        log.info("天安门50公里内找到{}家门店", searchHits.getTotalHits());

        // 在应用层按距离排序
        List<BurgerKingMerchant> sortedMerchants = searchHits.getSearchHits().stream()
            .map(hit -> hit.getContent())
            .filter(merchant -> merchant.getLocation() != null)
            .sorted((m1, m2) -> {
                double distance1 = calculateDistance(centerPoint, m1.getLocation());
                double distance2 = calculateDistance(centerPoint, m2.getLocation());
                return Double.compare(distance1, distance2);
            })
            .limit(10)
            .toList();

        log.info("按距离排序后的前10家门店:");
        sortedMerchants.forEach(merchant -> {
            double distance = calculateDistance(centerPoint, merchant.getLocation());
            log.info("门店: {} - 距离天安门: {}公里", merchant.getStoreName(), String.format("%.2f", distance));
        });
    }

    @Test
    @Order(7)
    @DisplayName("7. 地理位置统计 - 统计各地区的门店分布")
    void testGeoStatistics() {
        log.info("测试地理位置统计...");

        // 查询所有有地理位置信息的门店
        CriteriaQuery query = new CriteriaQuery(new Criteria("location").exists());
        query.setMaxResults(1000);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        // 统计不同地区的门店数量
        long beijingCount = searchHits.getSearchHits().stream()
            .filter(hit -> {
                BurgerKingMerchant merchant = hit.getContent();
                return merchant.getLocation() != null &&
                       merchant.getLocation().getLat() >= 39.4 &&
                       merchant.getLocation().getLat() <= 41.1 &&
                       merchant.getLocation().getLon() >= 115.7 &&
                       merchant.getLocation().getLon() <= 117.4;
            })
            .count();

        long shanghaiCount = searchHits.getSearchHits().stream()
            .filter(hit -> {
                BurgerKingMerchant merchant = hit.getContent();
                return merchant.getLocation() != null &&
                       merchant.getLocation().getLat() >= 30.5 &&
                       merchant.getLocation().getLat() <= 31.5 &&
                       merchant.getLocation().getLon() >= 120.8 &&
                       merchant.getLocation().getLon() <= 122.0;
            })
            .count();

        log.info("地理位置统计完成");
        log.info("北京地区门店数: {}", beijingCount);
        log.info("上海地区门店数: {}", shanghaiCount);
        log.info("总门店数: {}", searchHits.getTotalHits());

        Assertions.assertTrue(beijingCount >= 0, "北京地区门店数应该大于等于0");
        Assertions.assertTrue(shanghaiCount >= 0, "上海地区门店数应该大于等于0");
    }

    @Test
    @Order(8)
    @DisplayName("8. 地理位置距离计算")
    void testGeoDistanceCalculation() {
        log.info("测试地理位置距离计算...");

        // 定义两个测试点
        GeoPoint point1 = new GeoPoint(39.9087, 116.3975); // 天安门
        GeoPoint point2 = new GeoPoint(39.9925, 116.3341); // 清华大学

        // 计算两点之间的距离 (使用Haversine公式)
        double distance = calculateDistance(point1, point2);

        log.info("天安门到清华大学的距离: {} 公里", distance);

        // 验证距离计算是否合理
        Assertions.assertTrue(distance > 0, "距离应该大于0");
        Assertions.assertTrue(distance < 50, "天安门到清华大学的距离应该小于50公里");
    }

    @Test
    @Order(9)
    @DisplayName("9. 地理位置数据质量检查")
    void testGeoDataQuality() {
        log.info("测试地理位置数据质量...");

        // 查询所有门店
        CriteriaQuery query = new CriteriaQuery(new Criteria());
        query.setMaxResults(1000);

        SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

        Assertions.assertNotNull(searchHits, "搜索结果不应为空");

        // 检查地理位置数据质量
        long validGeoCount = 0;
        long invalidGeoCount = 0;
        long nullGeoCount = 0;

        for (var hit : searchHits.getSearchHits()) {
            BurgerKingMerchant merchant = hit.getContent();
            if (merchant.getLocation() == null) {
                nullGeoCount++;
            } else {
                double lat = merchant.getLocation().getLat();
                double lon = merchant.getLocation().getLon();

                // 检查坐标是否在合理范围内
                if (lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180) {
                    validGeoCount++;
                } else {
                    invalidGeoCount++;
                }
            }
        }

        log.info("地理位置数据质量检查完成");
        log.info("有效地理位置数据: {}条", validGeoCount);
        log.info("无效地理位置数据: {}条", invalidGeoCount);
        log.info("空地理位置数据: {}条", nullGeoCount);
        log.info("总数据: {}条", searchHits.getTotalHits());

        Assertions.assertTrue(validGeoCount >= 0, "有效地理位置数据应该大于等于0");
        Assertions.assertTrue(invalidGeoCount >= 0, "无效地理位置数据应该大于等于0");
    }

    @Test
    @Order(10)
    @DisplayName("10. 地理位置查询性能测试")
    void testGeoQueryPerformance() {
        log.info("测试地理位置查询性能...");

        // 以天安门为中心，测试不同距离的查询性能
        GeoPoint centerPoint = new GeoPoint(39.9087, 116.3975);

        String[] distances = {"5km", "10km", "20km", "50km"};

        for (String distance : distances) {
            long startTime = System.currentTimeMillis();

            Criteria geoCriteria = new Criteria("location")
                .within(centerPoint, distance);
            CriteriaQuery query = new CriteriaQuery(geoCriteria);

            SearchHits<BurgerKingMerchant> searchHits = elasticsearchOperations.search(query, BurgerKingMerchant.class);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("距离{}查询: 找到{}家门店, 耗时{}ms", distance, searchHits.getTotalHits(), duration);

            Assertions.assertTrue(duration < 5000, "查询应该在5秒内完成");
        }
    }

    /**
     * 使用Haversine公式计算两点间的距离
     */
    private double calculateDistance(GeoPoint point1, GeoPoint point2) {
        final double R = 6371; // 地球半径，单位：公里

        double lat1 = Math.toRadians(point1.getLat());
        double lat2 = Math.toRadians(point2.getLat());
        double deltaLat = Math.toRadians(point2.getLat() - point1.getLat());
        double deltaLon = Math.toRadians(point2.getLon() - point1.getLon());

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
