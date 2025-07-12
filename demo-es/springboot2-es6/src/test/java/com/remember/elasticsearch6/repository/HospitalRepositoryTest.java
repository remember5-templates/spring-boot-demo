package com.remember.elasticsearch6.repository;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.remember.elasticsearch6.ElasticSearchDemoApplicationTests;
import com.remember.elasticsearch6.entity.Hospital;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author wangjiahao
 * @date 2020/12/18
 */
@Slf4j
public class HospitalRepositoryTest extends ElasticSearchDemoApplicationTests {

    @Autowired
    HospitalRepository repository;

    @Test
    public void save() {
        List<String> nameList = Arrays.asList(
                "上海市松江区中心医院",
                "上海市松江区泗泾医院",
                "上海曙光医院(松江分院)",
                "上海市第五康复医院",
                "上海市松江区松江区妇幼保健院",
                "上海市松江区精神卫生中心",
                "上海市松江区九亭医院",
                "上海市松江区岳阳街道社区卫生服务中心",
                "上海市松江区永丰街道社区卫生服务中心",
                "上海市松江区中山街道社区卫生服务中心",
                "上海市松江区方松街道社区卫生服务中心",
                "上海市松江区车墩镇社区卫生服务中心",
                "上海市松江区新桥镇社区卫生服务中心",
                "上海市松江区洞泾镇社区卫生服务中心",
                "上海市松江区九亭镇社区卫生服务中心",
                "上海市松江区泗泾镇社区卫生服务中心",
                "上海市松江区佘山镇社区卫生服务中心",
                "上海市松江区小昆山镇社区卫生服务中心",
                "上海市松江区石湖荡镇社区卫生服务中心",
                "上海市松江区新浜镇社区卫生服务中心",
                "上海市松江区泖港镇社区卫生服务中心",
                "上海市松江区叶榭镇社区卫生服务中心");

        List<String> addressList = Arrays.asList(
                "上海市松江区中山中路746号",
                "上海市松江区泗泾镇泗通路389号(近9号线泗泾站)",
                "上海市松江区中山东路39号",
                "上海市松江区乐都路279号",
                "上海市松江区西林北路1010号",
                "上海市松江区塔汇路209号",
                "上海市松江区九新公路155号",
                "上海市松江区阔街21号",
                "上海市松江区荣乐西路1039号",
                "上海市松江区施惠路451号",
                "上海市松江区文诚路805号",
                "上海市松江区虬长路168号",
                "上海市松江区新南街268号",
                "上海市松江区长兴东路1566号",
                "上海市松江区九亭镇易富路128号",
                "上海市松江区泗泾镇江川北路108号",
                "上海市松江区佘新路18号",
                "上海市松江区文翔路6300号",
                "上海市松江区石湖新路105号",
                "上海市松江区共青路1237号",
                "上海市松江区新宾路436号",
                "上海市松江区叶权路210号");
        Date date = new Date();
        int size = addressList.size();
        for (int i = 0; i < size; i++) {
            Hospital build = Hospital.builder().id(i)
                    .name(nameList.get(i))
                    .address(addressList.get(i))
                    .countStar(RandomUtil.randomInt(1, 5))
                    .sort(i)
                    .state(RandomUtil.randomInt(1, 2))
                    .createTime(DateUtil.toLocalDateTime(new Date()))
                    .updateTime(DateUtil.toLocalDateTime(new Date())).build();
            repository.save(build);

        }


    }


    @Test
    public void query() {
        // 查询所有，按照id排序
        for (Hospital id : repository.findAll(Sort.by(Sort.Direction.DESC, "id"))) {
            log.info("查询所有，按照id排序 {}", id);
        }

        // 模糊匹配
        for (Hospital search : repository.search(QueryBuilders.wildcardQuery("name", String.format("*%s*", "服务中心")))) {
            log.info("模糊匹配 {}", search);
        }

        // 分词1
        for (Hospital search : repository.search(QueryBuilders.prefixQuery("name", "服务中心"))) {
            log.info("分词1 {}", search);
        }

//        log.error("-----");

        // 分词
        for (Hospital search : repository.search(QueryBuilders.prefixQuery("name", "街道"))) {
            log.info("分词2 {}", search);
        }


    }


}
