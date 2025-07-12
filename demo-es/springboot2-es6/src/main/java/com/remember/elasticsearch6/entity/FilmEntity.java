package com.remember.elasticsearch6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "new_film", type = "new")
@ToString
public class FilmEntity {
    private Long id;
    private String name;
    private String director;
    private Date created;

}
