package com.remember5.aio.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_person")
public class TPerson {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "\"name\"")
    private String name;

    @TableField(value = "age")
    private Integer age;

    @TableField(value = "address")
    private String address;

    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "update_date")
    private Date updateDate;
}
