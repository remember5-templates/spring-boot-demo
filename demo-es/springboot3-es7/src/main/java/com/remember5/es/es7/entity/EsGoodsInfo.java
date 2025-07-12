/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.es.es7.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商品信息
 *
 * @author wangjiahao
 * @date 2025/7/11 15:50
 */
@Data
@Document(indexName = "goods_info")
@Setting(shards = 1, replicas = 0)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EsGoodsInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public EsGoodsInfo(Integer id) {
        this.id = id;
    }

    /**
     * 商品ID
     */
    @Id
    private Integer id;

    /**
     * 商家ID
     */
    @Field(type = FieldType.Integer)
    private Integer businessId;

    /**
     * 商品名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String name;

    /**
     * 商品副标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String subtitle;

    /**
     * 商品描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String description;

    /**
     * 商品品牌
     */
    @Field(type = FieldType.Keyword)
    private String brand;

    /**
     * 商品型号
     */
    @Field(type = FieldType.Keyword)
    private String model;

    /**
     * 商品分类ID
     */
    @Field(type = FieldType.Integer)
    private Integer categoryId;

    /**
     * 商品分类名称
     */
    @Field(type = FieldType.Keyword)
    private String categoryName;

    /**
     * 商品分类路径（如：电子产品/手机/智能手机）
     */
    @Field(type = FieldType.Keyword)
    private List<String> categoryPath;

    /**
     * 商品标签
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /**
     * 商品规格（嵌套类型）
     */
    @Field(type = FieldType.Nested)
    private List<GoodsSpec> specifications;

    /**
     * 商品SKU列表（嵌套类型）
     */
    @Field(type = FieldType.Nested)
    private List<GoodsSku> skus;

    /**
     * 商品主图
     */
    @Field(type = FieldType.Keyword)
    private String mainImage;

    /**
     * 商品图片列表
     */
    @Field(type = FieldType.Keyword)
    private List<String> images;

    /**
     * 商品视频
     */
    @Field(type = FieldType.Keyword)
    private String videoUrl;

    /**
     * 商品状态（上架/下架/删除）
     */
    @Field(type = FieldType.Keyword)
    private String status;

    /**
     * 商品类型（实物/虚拟/服务）
     */
    @Field(type = FieldType.Keyword)
    private String goodsType;

    /**
     * 是否新品
     */
    @Field(type = FieldType.Boolean)
    private Boolean isNew;

    /**
     * 是否热销
     */
    @Field(type = FieldType.Boolean)
    private Boolean isHot;

    /**
     * 是否推荐
     */
    @Field(type = FieldType.Boolean)
    private Boolean isRecommend;

    /**
     * 是否限时特价
     */
    @Field(type = FieldType.Boolean)
    private Boolean isFlashSale;

    /**
     * 原价
     */
    @Field(type = FieldType.Double)
    private Double originalPrice;

    /**
     * 现价
     */
    @Field(type = FieldType.Double)
    private Double currentPrice;

    /**
     * 最低价格（用于价格区间搜索）
     */
    @Field(type = FieldType.Double)
    private Double minPrice;

    /**
     * 最高价格（用于价格区间搜索）
     */
    @Field(type = FieldType.Double)
    private Double maxPrice;

    /**
     * 库存数量
     */
    @Field(type = FieldType.Integer)
    private Integer stock;

    /**
     * 销量
     */
    @Field(type = FieldType.Long)
    private Long salesVolume;

    /**
     * 月销量
     */
    @Field(type = FieldType.Long)
    private Long monthlySales;

    /**
     * 评分
     */
    @Field(type = FieldType.Double)
    private Double rating;

    /**
     * 评价数量
     */
    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    /**
     * 好评率
     */
    @Field(type = FieldType.Double)
    private Double positiveRate;

    /**
     * 商品重量（克）
     */
    @Field(type = FieldType.Double)
    private Double weight;

    /**
     * 商品体积（立方厘米）
     */
    @Field(type = FieldType.Double)
    private Double volume;

    /**
     * 商品尺寸（长x宽x高，厘米）
     */
    @Field(type = FieldType.Keyword)
    private String dimensions;

    /**
     * 保质期（天）
     */
    @Field(type = FieldType.Integer)
    private Integer shelfLife;

    /**
     * 生产日期
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date productionDate;

    /**
     * 产地
     */
    @Field(type = FieldType.Keyword)
    private String origin;

    /**
     * 材质
     */
    @Field(type = FieldType.Keyword)
    private String material;

    /**
     * 颜色
     */
    @Field(type = FieldType.Keyword)
    private List<String> colors;

    /**
     * 尺寸
     */
    @Field(type = FieldType.Keyword)
    private List<String> sizes;

    /**
     * 是否支持7天无理由退货
     */
    @Field(type = FieldType.Boolean)
    private Boolean supportReturn;

    /**
     * 是否支持货到付款
     */
    @Field(type = FieldType.Boolean)
    private Boolean supportCod;

    /**
     * 是否支持分期付款
     */
    @Field(type = FieldType.Boolean)
    private Boolean supportInstallment;

    /**
     * 运费模板ID
     */
    @Field(type = FieldType.Integer)
    private Integer freightTemplateId;

    /**
     * 运费
     */
    @Field(type = FieldType.Double)
    private Double freight;

    /**
     * 是否包邮
     */
    @Field(type = FieldType.Boolean)
    private Boolean freeShipping;

    /**
     * 包邮条件（满多少包邮）
     */
    @Field(type = FieldType.Double)
    private Double freeShippingThreshold;

    /**
     * 预计发货时间（小时）
     */
    @Field(type = FieldType.Integer)
    private Integer estimatedShippingTime;

    /**
     * 预计到达时间（天）
     */
    @Field(type = FieldType.Integer)
    private Integer estimatedArrivalTime;

    /**
     * 商品创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date createTime;

    /**
     * 商品更新时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date updateTime;

    /**
     * 上架时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date onlineTime;

    /**
     * 商品规格信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoodsSpec implements Serializable {
        @Field(type = FieldType.Keyword)
        private String specName;
        @Field(type = FieldType.Keyword)
        private String specValue;
        @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
        private String description;
    }

    /**
     * 商品SKU信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoodsSku implements Serializable {
        @Field(type = FieldType.Keyword)
        private String skuId;
        @Field(type = FieldType.Keyword)
        private String skuName;
        @Field(type = FieldType.Double)
        private Double price;
        @Field(type = FieldType.Integer)
        private Integer stock;
        @Field(type = FieldType.Keyword)
        private String image;
        @Field(type = FieldType.Keyword)
        private List<String> attributes; // 如：["红色", "XL"]
        @Field(type = FieldType.Keyword)
        private String barcode;
    }
}
