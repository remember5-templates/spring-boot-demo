package com.remember.elasticsearch6.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wangjiahao
 * @date 2020/4/17
 */
@Data
@AllArgsConstructor
public class SearchLabel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private Integer count;
}
