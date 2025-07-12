package com.remember.elasticsearch6.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wangjiahao
 * @date 2020/4/16
 */
@Data
@TableName("customer_complain")
@Document(indexName = "customer_complain", type = "customer_complain", shards = 1, replicas = 0)
@AllArgsConstructor
@NoArgsConstructor
public class CustomerComplain implements Serializable {

    private static final long serialVersionUID = 3165817856610508374L;

    /**
     * id
     */
    @Id
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 活动名称
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @TableField(value = "activity_name")
    private String activityName;

    /**
     * 投诉描述
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @TableField(value = "complain_describe")
    private String complainDescribe;

    /**
     * 投诉原因
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @TableField(value = "complain_reason")
    private String complainReason;

    /**
     * 投诉结果
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @TableField(value = "dispose_result")
    private String disposeResult;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    @TableField(value = "update_time")
    private Date updateTime;
}
