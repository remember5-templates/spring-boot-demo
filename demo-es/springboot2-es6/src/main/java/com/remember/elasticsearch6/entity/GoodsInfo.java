package com.remember.elasticsearch6.entity;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "contents", type = "content")
//indexName索引名称 可以理解为数据库名 必须为小写 不然会报org.elasticsearch.indices.InvalidIndexNameException异常
//type类型 可以理解为表名
public class GoodsInfo {
    private Long id;
    private String name;
    private String des;
}
