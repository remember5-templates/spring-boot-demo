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

import java.util.Date;

/**
 * 联想词
 *
 * @author wangjiahao
 * @date 2020/5/30
 */
@Data
@Builder
@Document(indexName = "associate_word", shards = 1, replicas = 0)
@AllArgsConstructor
@NoArgsConstructor
public class AssociateWord {

    /**
     * id
     */
    @Id
    private int id;

    /**
     * 活动名称
     */
//	@Field(type = FieldType.Text, analyzer = "ik_smart")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String keyword;

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
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private Date createTime;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private Date updateTime;


}
