package com.remember.elasticsearch6.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * @author wangjiahao
 * @date 2020/12/18
 */
@Builder
@Data
@Document(indexName = "hospital")
@AllArgsConstructor
@NoArgsConstructor
public class Hospital {

    @Id
    private Integer id;

    /**
     * 名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String name;

    /**
     * 地址
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String address;

    /**
     * 星级
     */
    @Field(type = FieldType.Integer)
    private Integer countStar;

    /**
     * 状态
     */
    @Field(type = FieldType.Integer)
    private Integer state;

    /**
     * 排序
     */
    @Field(type = FieldType.Integer)
    private Integer sort;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


}
