package com.remember.elasticsearch6.repository;

import com.remember.elasticsearch6.ElasticSearchDemoApplicationTests;
import com.remember.elasticsearch6.entity.GoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */
@Slf4j
public class GoodsRepositoryTest extends ElasticSearchDemoApplicationTests {

    @Autowired
    private GoodsRepository repo;


    /**
     * 测试新增
     */
    @Test
    public void save() {
        GoodsInfo goodsInfo = new GoodsInfo(1L, "为了更美好的明天而战", "");
        GoodsInfo save = repo.save(goodsInfo);
        log.info("【save】= {}", save);
    }


}
