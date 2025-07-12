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

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 商家信息
 *
 * @author wangjiahao
 * @date 2025/7/11 15:50
 */
@Data
@Document(indexName = "business_info")
@Setting(shards = 1, replicas = 0)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EsBusinessInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public EsBusinessInfo(Integer id) {
        this.id = id;
    }

    /**
     * 商家id
     */
    @Id
    private Integer id;

    /**
     * 省
     */
    @Field(type = FieldType.Keyword)
    private String province;

    /**
     * 市
     */
    @Field(type = FieldType.Keyword)
    private String city;

    /**
     * 区
     */
    @Field(type = FieldType.Keyword)
    private String area;

    /**
     * 街道
     */
    @Field(type = FieldType.Keyword)
    private String street;

    /**
     * 详细地址
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String address;


    @GeoPointField
    @GeoShapeField
    private GeoPoint coordinate;

    // 商家信息
    /**
     * 商家名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String name;
    /**
     * 商家联系方式
     */
    @Field(type = FieldType.Keyword)
    private String contactInfo;

    /**
     * 行业类型
     */
    @Field(type = FieldType.Keyword)
    private String businessType;

    /**
     * 商家状态
     */
    @Field(type = FieldType.Keyword)
    private String status;

    /**
     * 商家LOGO
     */
    @Field(type = FieldType.Keyword)
    private String logoUrl;

    /**
     * 商家简介
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String description;

    /**
     * 营业时间（如09:00-22:00）
     */
    @Field(type = FieldType.Keyword)
    private String openTime;

    /**
     * 预计到达时间（天）
     */
    @Field(type = FieldType.Integer)
    private Integer estimatedArrivalTime;

    /**
     * 商家等级（1-5星）
     */
    @Field(type = FieldType.Integer)
    private Integer businessLevel;

    /**
     * 商家认证状态（已认证/未认证/认证中）
     */
    @Field(type = FieldType.Keyword)
    private String certificationStatus;

    /**
     * 认证类型（个人认证/企业认证）
     */
    @Field(type = FieldType.Keyword)
    private String certificationType;

    /**
     * 认证时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date certificationTime;

    /**
     * 商家保证金
     */
    @Field(type = FieldType.Double)
    private Double deposit;

    /**
     * 商家信用分
     */
    @Field(type = FieldType.Integer)
    private Integer creditScore;

    /**
     * 服务承诺（如：7天无理由退货、48小时发货等）
     */
    @Field(type = FieldType.Keyword)
    private List<String> servicePromises;

    /**
     * 支付方式（支付宝/微信/银行卡等）
     */
    @Field(type = FieldType.Keyword)
    private List<String> paymentMethods;

    /**
     * 配送方式（自提/快递/同城配送等）
     */
    @Field(type = FieldType.Keyword)
    private List<String> deliveryMethods;

    /**
     * 商家公告
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String announcement;

    /**
     * 商家客服电话
     */
    @Field(type = FieldType.Keyword)
    private String customerServicePhone;

    /**
     * 商家客服QQ
     */
    @Field(type = FieldType.Keyword)
    private String customerServiceQQ;

    /**
     * 商家客服微信
     */
    @Field(type = FieldType.Keyword)
    private String customerServiceWechat;

    /**
     * 商家营业证照
     */
    @Field(type = FieldType.Keyword)
    private List<String> businessCertificates;

    /**
     * 商家特色服务
     */
    @Field(type = FieldType.Keyword)
    private List<String> specialServices;

    /**
     * 商家荣誉（如：金牌商家、诚信商家等）
     */
    @Field(type = FieldType.Keyword)
    private List<String> honors;

    /**
     * 商家活动参与情况
     */
    @Field(type = FieldType.Keyword)
    private List<String> activities;

    /**
     * 商家粉丝数
     */
    @Field(type = FieldType.Long)
    private Long followerCount;

    /**
     * 商家关注数
     */
    @Field(type = FieldType.Long)
    private Long followingCount;

    /**
     * 商家动态数
     */
    @Field(type = FieldType.Long)
    private Long postCount;

    /**
     * 商家商品总数
     */
    @Field(type = FieldType.Integer)
    private Integer goodsCount;

    /**
     * 商家在线商品数
     */
    @Field(type = FieldType.Integer)
    private Integer onlineGoodsCount;

    /**
     * 商家商品创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date createTime;

    /**
     * 信息更新时间
     */
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Date updateTime;

    /**
     * 总销量
     */
    @Field(type = FieldType.Long)
    private Long salesVolume;

    /**
     * 评分（如4.8）
     */
    @Field(type = FieldType.Double)
    private Double rating;

    /**
     * 评价数
     */
    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    /**
     * 标签（如"连锁""24小时"）
     */
    @Field(type = FieldType.Keyword)
    private List<String> tags;

    /**
     * 标签详情（嵌套类型）
     */
    @Field(type = FieldType.Nested)
    private List<TagInfo> tagsDetail;

    /**
     * 是否VIP商家
     */
    @Field(type = FieldType.Boolean)
    private Boolean isVip;

    /**
     * 被收藏数
     */
    @Field(type = FieldType.Long)
    private Long favoriteCount;

    /**
     * 营业执照编号
     */
    @Field(type = FieldType.Keyword)
    private String businessLicense;

    /**
     * 主营商品/服务
     */
    @Field(type = FieldType.Keyword)
    private List<String> mainProducts;

    /**
     * 商家图片
     */
    @Field(type = FieldType.Keyword)
    private List<String> images;

    /**
     * 是否支持配送
     */
    @Field(type = FieldType.Boolean)
    private Boolean deliverySupport;

    /**
     * 起送金额
     */
    @Field(type = FieldType.Double)
    private Double minOrderAmount;

    /**
     * 平均配送时长（分钟）
     */
    @Field(type = FieldType.Integer)
    private Integer avgDeliveryTime;

    /**
     * 嵌套标签信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class TagInfo implements Serializable{

        @Field(type = FieldType.Keyword)
        private String tag;

        @Field(type = FieldType.Integer)
        private Integer weight;

        @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
        private String description;
    }
}
