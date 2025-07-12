package com.remember.elasticsearch6.template;

import com.remember.elasticsearch6.ElasticSearchDemoApplicationTests;
import com.remember.elasticsearch6.entity.CustomerComplain;
import com.remember.elasticsearch6.entity.GoodsInfo;
import com.remember.elasticsearch6.entity.HotWireKb;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */
public class TemplateTest extends ElasticSearchDemoApplicationTests {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    /**
     * 测试 ElasticTemplate 创建 index HotWireKb
     */
    @Test
    public void testCreateIndex() {
        // 创建索引，会根据Item类的@Document注解信息来创建
        esTemplate.createIndex(HotWireKb.class);

        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        esTemplate.putMapping(HotWireKb.class);
    }

    /**
     * 测试 ElasticTemplate 删除 index HotWireKb
     */
    @Test
    public void testDeleteIndex() {
        esTemplate.deleteIndex(HotWireKb.class);
    }

    /**
     * 测试 ElasticTemplate 创建 index GoodsInfo
     */
    @Test
    public void testCreateIndex1() {
        // 创建索引，会根据Item类的@Document注解信息来创建
        esTemplate.createIndex(GoodsInfo.class);

        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        esTemplate.putMapping(GoodsInfo.class);
    }


    /**
     * 测试 ElasticTemplate 删除 index GoodsInfo
     */
    @Test
    public void testDeleteIndex1() {
        esTemplate.deleteIndex(GoodsInfo.class);
    }


    /**
     * 测试 ElasticTemplate 创建 index CustomerComplain
     */
    @Test
    public void testCreateIndex2() {
        // 创建索引，会根据Item类的@Document注解信息来创建
        esTemplate.createIndex(CustomerComplain.class);

        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        esTemplate.putMapping(CustomerComplain.class);
    }

    /**
     * 测试 ElasticTemplate 删除 index CustomerComplain
     */
    @Test
    public void testDeleteIndex2() {
        esTemplate.deleteIndex(CustomerComplain.class);
    }


}
