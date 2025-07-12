/**
 * Copyright [2022] [remember5]
 * <p>
 * Licensed under the Apache License Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing software
 * distributed under the License is distributed on an "AS IS" BASIS
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.remember5.springboot3.es8.entity;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.remember5.springboot3.es8.util.DateConverter;
import com.remember5.springboot3.es8.util.GeoPointConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wangjiahao
 * @date 2025/7/12 23:27
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "burger_king_merchant")
public class BurgerKingMerchant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @CsvBindByName(column = "storeId")
    private Integer storeId;

    /**
     * 省
     */
    @CsvBindByName(column = "storeProvince")
    @Field(type = FieldType.Keyword)
    private String storeProvince;

    /**
     * 市
     */
    @CsvBindByName(column = "storeCity")
    @Field(type = FieldType.Keyword)
    private String storeCity;

    /**
     * 区
     */
    @CsvBindByName(column = "storeArea")
    @Field(type = FieldType.Keyword)
    private String storeArea;

    /**
     * 大区
     */
    @CsvBindByName(column = "storeAreaCn")
    @Field(type = FieldType.Keyword)
    private String storeAreaCn;

    /**
     * 店名
     */
    @CsvBindByName(column = "storeName")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String storeName;

    /**
     * 门店地址
     */
    @CsvBindByName(column = "storeAddress")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String storeAddress;

    /**
     * 管理公司
     */
    @CsvBindByName(column = "storeBrandManage")
    @Field(type = FieldType.Keyword)
    private String storeBrandManage;

    /**
     * 营业时间
     */
    @CsvBindByName(column = "storeBusinessHours")
    @Field(type = FieldType.Keyword)
    private String storeBusinessHours;

    /**
     * 门店联系人
     */
    @CsvBindByName(column = "storeContactName")
    @Field(type = FieldType.Keyword)
    private String storeContactName;

    /**
     * 门店联系电话
     */
    @CsvBindByName(column = "storeContactPhone")
    @Field(type = FieldType.Keyword)
    private String storeContactPhone;

    /**
     * 门店ip地址
     */
    @CsvBindByName(column = "storeIPaddress")
    @Field(type = FieldType.Ip)
    private String storeIPaddress;

    /**
     * storeLatitude,storeLongitude; 组成的经纬度
     */
    @GeoPointField
    @CsvBindByName(column = "location")
    @CsvCustomBindByName(converter = GeoPointConverter.class)
    private transient GeoPoint location;

    /**
     * 门店编号
     */
    @CsvBindByName(column = "storeNo")
    @Field(type = FieldType.Integer)
    private Integer storeNo;


    /**
     * 门店电话
     */
    @CsvBindByName(column = "storePhone")
    @Field(type = FieldType.Keyword)
    private String storePhone;

    /**
     * 未知
     */
    @CsvBindByName(column = "storePrecision")
    @Field(type = FieldType.Keyword)
    private String storePrecision;

    /**
     * 门店状态
     */
    @CsvBindByName(column = "storePublish")
    @Field(type = FieldType.Boolean)
    private Boolean storePublish;

    /**
     * 门店状态
     */
    @CsvBindByName(column = "storeState")
    @Field(type = FieldType.Boolean)
    private Boolean storeState;

    /**
     * 未知
     */
    @CsvBindByName(column = "storeTimestamp")
    @Field(type = FieldType.Keyword)
    private Long storeTimestamp;

    /**
     * 未知
     */
    @CsvBindByName(column = "storeZip")
    @Field(type = FieldType.Integer)
    private Integer storeZip;

    /**
     * 可以用卡
     */
    @CsvBindByName(column = "useCard")
    @Field(type = FieldType.Boolean)
    private Boolean useCard;

    /**
     * 是否有早餐
     */
    @CsvBindByName(column = "hasBreakfast")
    @Field(type = FieldType.Boolean)
    private Boolean hasBreakfast;

    /**
     * 门店创建时间
     */
    @CsvBindByName(column = "storeAddtime")
    @CsvCustomBindByName(converter = DateConverter.class)
    @Field(type = FieldType.Date)
    private Date storeAddtime;

}
