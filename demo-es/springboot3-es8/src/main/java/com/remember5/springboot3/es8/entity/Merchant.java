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
package com.remember5.springboot3.es8.entity;

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
import java.time.LocalDateTime;

/**
 * 商户实体
 *
 * @author wangjiahao
 * @date 2025/7/12 21:12
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "merchant")
public class Merchant implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 商家名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String name;

    /**
     * 联系人姓名
     */
    @Field(type = FieldType.Keyword)
    private String contactPerson;

    /**
     * 联系电话
     */
    @Field(type = FieldType.Keyword)
    private String phone;

    /**
     * 商家地址
     */
    @Field(type = FieldType.Text)
    private String address;

    /**
     * 注册时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime registrationDate;

    /**
     * 商家经纬度
     */
    @GeoPointField
    private transient GeoPoint location;

    /**
     * 信用评级 (1-5星)
     */
    @Field(type = FieldType.Integer)
    private Integer creditRating;

    /**
     * 是否认证商家
     */
    @Field(type = FieldType.Boolean)
    private Boolean isVerified;
}
