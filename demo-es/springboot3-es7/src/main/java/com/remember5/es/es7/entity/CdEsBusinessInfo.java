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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.io.Serializable;


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
public class CdEsBusinessInfo implements Serializable {

    private static final long serialVersionUID=1L;

    public CdEsBusinessInfo(Integer id) {
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
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String address;


    @GeoPointField
    private GeoPoint coordinate;

    // 商家信息
    /**
     * 商家名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
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


}
