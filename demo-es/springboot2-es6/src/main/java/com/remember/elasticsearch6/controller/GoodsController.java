package com.remember.elasticsearch6.controller;

import com.remember.elasticsearch6.entity.GoodsInfo;
import com.remember.elasticsearch6.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsRepository goodsRepository;


    //http://localhost:8080/save?des=
    @GetMapping("save")
    public String save(String des) {
        GoodsInfo goodsInfo = new GoodsInfo(System.currentTimeMillis(),
                "商品" + System.currentTimeMillis(), des);
        goodsRepository.save(goodsInfo);
        return "success";
    }

    //http://localhost:8080/delete?id=
    @GetMapping("delete")
    public String delete(long id) {
        goodsRepository.deleteById(id);
        return "success";
    }

    //http://localhost:8080/update?name=修改&des=修改&id=
    @GetMapping("update")
    public String update(long id, String name, String description) {
        GoodsInfo goodsInfo = new GoodsInfo(id,
                name, description);
        goodsRepository.save(goodsInfo);
        return "success";
    }

    //http://localhost:8080/getOne?id=
    @GetMapping("getOne")
    public GoodsInfo getOne(long id) {
        GoodsInfo goodsInfo = goodsRepository.findById(id).orElse(null);
        return goodsInfo;
    }


}
