package com.remember.elasticsearch6.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2020/4/17
 */
@Data
@RequiredArgsConstructor
public class LabelTree implements Serializable {

    private static final long serialVersionUID = -7799118625995909021L;
    private String title;

    private List<SearchLabel> children;

    public void initChildren() {
        this.children = new ArrayList<>();
    }

}
