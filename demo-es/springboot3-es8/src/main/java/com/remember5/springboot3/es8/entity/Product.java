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

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品实体
 *
 * @author wangjiahao
 * @date 2025/7/12 21:31
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "product")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    /**
     * 商品标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    /**
     * 关联商家ID
     */
    @Field(type = FieldType.Keyword)
    private String merchantId;

    /**
     * 商品价格
     */
    @Field(type = FieldType.Double)
    private Double price;

    /**
     * 库存数量
     */
    @Field(type = FieldType.Integer)
    private Integer stock;

    /**
     * 商品分类
     */
    @Field(type = FieldType.Keyword)
    private List<String> categories;

    /**
     * 商品描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String description;

    /**
     * 规格参数
     */
    @Field(type = FieldType.Nested)
    private List<Specification> specifications;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;

    /**
     * 规格参数嵌套对象
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Specification implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 参数名 (如"颜色")
         */
        @Field(type = FieldType.Keyword)
        private String key;

        /**
         * 参数值 (如"黑色")
         */
        @Field(type = FieldType.Keyword)
        private String value;
    }

}
