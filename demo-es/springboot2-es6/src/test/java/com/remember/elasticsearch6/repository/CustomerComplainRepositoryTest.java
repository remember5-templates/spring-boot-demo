package com.remember.elasticsearch6.repository;

import com.remember.elasticsearch6.ElasticSearchDemoApplicationTests;
import com.remember.elasticsearch6.entity.CustomerComplain;
import com.remember.elasticsearch6.service.CustomerComplainService;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author wangjiahao
 * @date 2020/4/16
 */
public class CustomerComplainRepositoryTest extends ElasticSearchDemoApplicationTests {

    @Autowired
    private CustomerComplainService customerComplainService;

    @Autowired
    private CustomerComplainRepository repository;

    /**
     * 测试查询所有
     */
    @Test
    public void query() {
        List<CustomerComplain> list = customerComplainService.list();
        list.forEach(System.out::println);
    }


    /**
     * 保存所有数据
     */
    @Test
    public void saveAll() {
        repository.saveAll(customerComplainService.list());
    }

    /**
     * 活动名称
     * 根据ActivityName字段查询
     */
    @Test
    public void queryByActivityName() {
        String keyword = "公众号";
//        QueryBuilders.prefixQuery("")
    }

    /**
     * 投诉描述
     * 根据ComplainDescribe字段查询
     * 对于matchPhrasePrefixQuery("address", address).slop(0)。
     * matchPhrasePrefixQuery表示短语查询。该短语会被分词产生一个词条列表。
     * slop(n)表示使用该词条列表查询时，包含该词条列表的内容但是每个词条相隔的其他词条个数为n。
     * slop(0)就是使用该词条列表 查询时，每个词条必须紧接在一起，是一个完整的短语，效果相当于不分词
     * 但是，这种方式也无法实现精确查询，比如address为”宜兴埠红旗路北7号“，查询时，”北辰区宜兴埠红旗路北7号“
     * 也会被匹配，只要包含“宜兴埠红旗路北7号”的短语或者句子，都会被匹配。因此使用字符串比对的方式要再做一次过滤，才能实现分词后的精确查询
     */
    @Test
    public void queryByComplainDescribe() {
        String keyword = "公众号";
        // 方式1
        Iterable<CustomerComplain> search = repository.search(QueryBuilders.matchPhrasePrefixQuery("complainDescribe", keyword).slop(0));
        search.forEach(System.out::println);


        // 方式2


    }


}
