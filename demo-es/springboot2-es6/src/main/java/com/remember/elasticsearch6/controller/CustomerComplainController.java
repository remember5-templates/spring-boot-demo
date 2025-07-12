package com.remember.elasticsearch6.controller;

import com.remember.elasticsearch6.entity.CustomerComplain;
import com.remember.elasticsearch6.entity.LabelTree;
import com.remember.elasticsearch6.entity.SearchLabel;
import com.remember.elasticsearch6.repository.CustomerComplainRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangjiahao
 * @date 2020/4/17
 */
@RestController
@RequestMapping("complain")
@RequiredArgsConstructor
public class CustomerComplainController {

    private final CustomerComplainRepository repository;

    @PostMapping
    public Map<String, Object> queryByKeyword(String keyword) {
        // 方式1
        HashMap<String, Object> result = new HashMap<String, Object>(8);
        ArrayList<LabelTree> labelTrees = new ArrayList<LabelTree>();

        LabelTree complainDescribe = new LabelTree();
        complainDescribe.setTitle("投诉描述");
        complainDescribe.initChildren();
        Iterable<CustomerComplain> complainDescribe1 = repository.search(QueryBuilders.matchPhrasePrefixQuery("complainDescribe", keyword).slop(0));
        complainDescribe1.forEach(e -> complainDescribe.getChildren().add(new SearchLabel(e.getActivityName(), e.getId())));
        labelTrees.add(complainDescribe);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", labelTrees);
        return result;
    }


}
