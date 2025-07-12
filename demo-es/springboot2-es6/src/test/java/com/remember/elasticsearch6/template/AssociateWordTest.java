package com.remember.elasticsearch6.template;

import com.remember.elasticsearch6.ElasticSearchDemoApplicationTests;
import com.remember.elasticsearch6.entity.AssociateWord;
import com.remember.elasticsearch6.repository.AssociateWordRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2020/5/30
 */
public class AssociateWordTest extends ElasticSearchDemoApplicationTests {

    @Autowired
    ElasticsearchTemplate esTemplate;

    @Autowired
    AssociateWordRepository repository;

    /**
     * 创建索引
     */
    @Test
    void createIndex() {
        // 创建索引，会根据Item类的@Document注解信息来创建
        esTemplate.createIndex(AssociateWord.class);
        // 配置映射，会根据Item类中的id、Field等字段来自动完成映射
        esTemplate.putMapping(AssociateWord.class);
    }

    /**
     * 删除索引
     */
    @Test
    void deleteIndex() {
        esTemplate.deleteIndex(AssociateWord.class);
    }

    @Test
    void batchSave() {
        List<String> list = Arrays.asList(
                "手机", "8p支架", "手机支架", "支架", "全新", "全新手机", "苹果手机", "手机华为",
                "苹果7p", "手机壳", "手机话费", "话费充值", "充值话费", "7p手机壳", "手机壳7p",
                "7p钢化膜", "7p支架", "iphone11", "iphone");
        for (int i = 0; i < list.size(); i++) {
            repository.save(AssociateWord.builder().id(i + 1).keyword(list.get(i)).sort(1).build());
        }
    }


}
