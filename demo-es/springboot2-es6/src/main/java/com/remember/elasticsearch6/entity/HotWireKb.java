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

import java.util.Date;

/**
 * ik_max_word 尽可能多的分词
 * ik_smart 尽可能少的分词
 *
 * @author wangjiahao
 * @date 2020/3/25
 */
@Data
@TableName("hot_wire_kb")
@Document(indexName = "hot_wire_kb", type = "hot_wire_kb", shards = 1, replicas = 0)
@AllArgsConstructor
@NoArgsConstructor
public class HotWireKb {

    /**
     *
     */
    @Id
    @TableId(type = IdType.NONE)
    private int id;

    /**
     * 标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @TableField(value = "title")
    private String title;

    /**
     * 等级
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "top_level")
    private Integer topLevel;

    /**
     * 状态
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "status")
    private Integer status;

    /**
     * 正文
     */
    @Field(type = FieldType.Text, analyzer = "ik _smart")
    @TableField(value = "texts1")
    private String text;

    /**
     * 绑定一级标签
     */
    @Field(type = FieldType.Text)
    @TableField(value = "attrs_secondTag")
    private String attrsSecondTag;

    /**
     * 绑定二级标签
     */
    @Field(type = FieldType.Text)
    @TableField(value = "attrs_firstTag")
    private String attrsFirstTag;

    /**
     * 绑定渠道名称
     */
    @Field(type = FieldType.Text)
    @TableField(value = "attrs_channelName")
    private String attrsChannelName;

    /**
     * 关键词
     */
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    @TableField(value = "attrs_keyword")
    private String attrsKeyword;

    /**
     * 渠道id
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "channel_id")
    private Integer channelId;

    /**
     * 渠道名称
     */
    @Field(type = FieldType.Text)
    @TableField(value = "channel_name")
    private String channelName;

    /**
     * 类型id
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "type_id")
    private Integer typeId;

    /**
     * 类型名称
     */
    @Field(type = FieldType.Text)
    @TableField(value = "type_name")
    private String typeName;

    /**
     * 模板id
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "model_id")
    private Integer modelId;

    /**
     * 模板名称
     */
    @Field(type = FieldType.Text)
    @TableField(value = "model_name")
    private String modelName;

    /**
     * 状态
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "synth_flag")
    private Integer synthFlag;

    /**
     * 状态
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "masterFlag")
    private Integer masterFlag;

    /**
     * 发布时间
     */
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    @TableField(value = "release_date")
    private Date releaseDate;

    /**
     * 创建时间
     */
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    @TableField(value = "create_date")
    private Date createDate;

    /**
     * 创建人id
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "create_by")
    private Integer createBy;

    /**
     * 更新时间
     */
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    @TableField(value = "update_date")
    private Date updateDate;

    /**
     * 更新人id
     */
    @Field(type = FieldType.Integer)
    @TableField(value = "update_by")
    private Integer updateBy;

}
