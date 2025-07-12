package com.remember5.elasticsearch7.logaccess;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.github.javafaker.Faker;
import com.remember5.elasticsearch7.DemoElasticsearch7Application;
import com.remember5.elasticsearch7.entity.LogAccessEntity;
import com.remember5.elasticsearch7.repository.LogAccessRepository;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author wangjiahao
 * @date 2022/5/27 00:08
 */
@SpringBootTest(classes = DemoElasticsearch7Application.class)
public class TestLogAccessRepository {

    @Resource
    LogAccessRepository repository;
    @Resource
    private ElasticsearchRestTemplate template;

    @Test
    public void testLogAccessRepository() {
        generatorData();
//        find();
    }

    /**
     * 精确查询
     * 精确，指的是查询关键字（或者关键字分词后），必须与目标分词结果完全匹配。
     */
    private void testBoolQuery() {
        // 1.指定字符串作为关键词查询，关键词支持分词
        //查询title字段中，包含 ”开发”、“开放" 这个字符串的document；相当于把"浦东开发开放"分词了，再查询；
        QueryBuilders.queryStringQuery("开发开放").defaultField("title");
        //不指定feild，查询范围为所有feild
        QueryBuilders.queryStringQuery("青春");
        //指定多个feild
        QueryBuilders.queryStringQuery("青春").field("title").field("content");


        // 2. 以关键字“开发开放”，关键字不支持分词
        QueryBuilders.termQuery("title", "开发开放");
        QueryBuilders.termsQuery("fieldName", "fieldlValue1", "fieldlValue2...");

        // 3.以关键字“开发开放”，关键字支持分词
        QueryBuilders.matchQuery("title", "开发开放");
        QueryBuilders.multiMatchQuery("fieldlValue", "fieldName1", "fieldName2", "fieldName3");


    }

    /**
     * 范围查询
     */
    private void testRangeQuery() {
        //闭区间查询
        QueryBuilders.rangeQuery("fieldName").from("fieldValue1").to("fieldValue2");
        //开区间查询，默认是true，也就是包含
        QueryBuilders.rangeQuery("fieldName").from("fieldValue1").to("fieldValue2").includeUpper(false).includeLower(false);
        //大于
        QueryBuilders.rangeQuery("fieldName").gt("fieldValue");
        //大于等于
        QueryBuilders.rangeQuery("fieldName").gte("fieldValue");
        //小于
        QueryBuilders.rangeQuery("fieldName").lt("fieldValue");
        //小于等于
        QueryBuilders.rangeQuery("fieldName").lte("fieldValue");
    }

    /**
     * 模糊查询
     * 模糊，是指查询关键字与目标关键字可以模糊匹配。
     * 注意， 在分词的情况下，针对fuzzyQuery、prefixQuery、wildcardQuery不支持分词查询，即使有这种doucment数据，也不一定能查出来，因为分词后，不一定有“开发开放”这个词；
     * 查询关键词	    开发开放	                            放	            开
     * queryStringQuery	查询目标中含有开发、开放、开发开放的	无	            无
     * matchQuery	    同queryStringQuery	                无	            无
     * termQuery	    无结果，因为它不支持分词	            无	            无
     * prefixQuery	    无结果，因为它不支持分词	            无	            有，目标分词中以”开“开头的
     * fuzzyQuery	    无结果，但是与fuzziness参数有关系	    无	            无
     * wildcardQuery	开发开放*无结果	                    开*，有	        放*，无
     */
    private void testWildcardQuery() {
        // 1.左右模糊查询，其中fuzziness的参数作用是在查询时，es动态的将查询关键词前后增加或者删除一个词，然后进行匹配
        QueryBuilders.fuzzyQuery("title", "开发开放").fuzziness(Fuzziness.ONE);
        // 2.前缀查询，查询title中以“开发开放”为前缀的document；
        QueryBuilders.prefixQuery("title", "开发开放");
        // 3.通配符查询，支持*和？，？表示单个字符；注意不建议将通配符作为前缀，否则导致查询很慢
        QueryBuilders.wildcardQuery("title", "开*放");
        QueryBuilders.wildcardQuery("title", "开？放");


    }


    /**
     * 多条件查询
     * QueryBuilders.boolQuery()
     * QueryBuilders.boolQuery().must();//文档必须完全匹配条件，相当于and
     * QueryBuilders.boolQuery().mustNot();//文档必须不匹配条件，相当于not
     * QueryBuilders.boolQuery().should();//至少满足一个条件，这个文档就符合should，相当于or
     */
    private void moreQuery() {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name", "小李"))
                .must(QueryBuilders.rangeQuery("age")
                        .gte(10)
                        .lte(50));

        // 查询地址在北京、上海、杭州，并且年龄在10至50，名字叫做李明的文档
        List<String> list = Arrays.asList("北京", "上海", "杭州");
        BoolQueryBuilder queryBuilder1 = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name", "李明"))
                .must(QueryBuilders.termsQuery("address", list))
                .must(QueryBuilders.rangeQuery("age")
                        .gte(10)
                        .lte(50));

        // 查询姓名包含小李或者是地址是北京的记录，should相当于或者or
        BoolQueryBuilder queryBuilder2 = QueryBuilders.boolQuery()
                .should(QueryBuilders.wildcardQuery("name", "*小李*"))
                .should(QueryBuilders.termQuery("address", "北京"));


        // should和must配合查询
        // 查询性别为男，姓名包含小李或地址为北京的记录，**minimumShouldMatch(1)**表示最少要匹配到一个should条件。
        BoolQueryBuilder queryBuilder3 = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("sex", "男"))
                .should(QueryBuilders.wildcardQuery("name", "*小李*"))
                .should(QueryBuilders.termQuery("address", "北京"))
                .minimumShouldMatch(1);

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.termQuery("title", "开发"))
                        .should(QueryBuilders.termQuery("title", "青春"))
                        .mustNot(QueryBuilders.termQuery("title", "潮头"))
                )
                // 按照id字段降序 .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                // 注意排序时，有个坑，就是在以id排序时，比如降序，结果可能并不是我们想要的。因为根据id排序，es实际上会根据_id进行排序，但是_id是string类型的，排序后的结果会与整型不一致。
                // 建议： 在创建es的索引mapping时，将es的id和业务的id分开，比如业务id叫做myId：
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                // 如果不指定分页参数，es默认只显示10条。
                .withPageable(PageRequest.of(0, 50))
                .build();



        final SearchHits<LogAccessEntity> search = template.search(nativeSearchQuery, LogAccessEntity.class);
        for (SearchHit<LogAccessEntity> searchHit : search.getSearchHits()) {
            System.err.println(searchHit);
        }
    }

    /**
     * 有值查询
     */
    private void hasValue() {
        // 查询name有值，tag不存在值的文档
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.existsQuery("name"))
                .mustNot(QueryBuilders.existsQuery("tag"));
    }
    private void highlight() {
        String preTag = "<font color='#dd4b39'>";
        String postTag = "</font>";
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", "开发"))
                .withPageable(PageRequest.of(0, 50))
                .withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC))
                .withHighlightFields(new HighlightBuilder.Field("title").preTags(preTag).postTags(postTag))
                .build();

        final SearchHits<LogAccessEntity> search = template.search(nativeSearchQuery, LogAccessEntity.class);
        for (SearchHit<LogAccessEntity> searchHit : search.getSearchHits()) {
            System.err.println(searchHit);
        }
    }



    /**
     * 生成数据
     */
    private void generatorData() {
        Faker faker = new Faker(new Locale("zh-CN"));
        Date now = new Date();
        int count = 10;
        for (int i = 0; i < count; i++) {

            Date between = faker.date().between(DateUtil.beginOfYear(now), now);
            LocalDateTime of = LocalDateTimeUtil.of(between);
            Timestamp timestamp = Timestamp.valueOf(of);
            LogAccessEntity logAccess = LogAccessEntity.builder()
                    .vMethod(RandomUtil.randomEle(CollUtil.newArrayList("GET", "POST", "PUT", "DELETE")))
                    .vUri(faker.internet().url())
                    .vIp(faker.internet().ipV4Address())
                    .iStatus(RandomUtil.randomEle(CollUtil.newArrayList(200, 302, 400, 401, 403, 404, 413, 500)))
                    .vType(RandomUtil.randomEle(CollUtil.newArrayList("01", "02", "03", "04", "05", "06", "07")))
                    .vBrowser(RandomUtil.randomEle(CollUtil.newArrayList("Chrome 8", "Chrome 9", "Chrome 10", "IE", "FireFox")))
                    .bSuccess(RandomUtil.randomBoolean())
                    .vApplication(RandomUtil.randomString(10))
                    .vDataId(RandomUtil.randomString(20))
                    .vAliasAtAppModule(RandomUtil.randomString(20))
                    .vAliasAtAppModuleFunction(RandomUtil.randomString(20))
                    .bSkip(RandomUtil.randomBoolean())
                    .idAtAuthUser(UUID.randomUUID().toString(true))
                    // 今年的第一天
                    .tCreate(timestamp)
                    .vBody(faker.shakespeare().asYouLikeItQuote())
                    .idAtAppModule(RandomUtil.randomString(20))
                    .vDevice(RandomUtil.randomEle(CollUtil.newArrayList("PC", "Android", "iOS")))
                    .build();
//            System.err.println(JSON.toJSONString(logAccess));
            repository.save(logAccess);
        }
    }


    private void query(Query query) {
        // 查询
        final SearchHits<LogAccessEntity> search = template.search(query, LogAccessEntity.class);
        for (SearchHit<LogAccessEntity> searchHit : search.getSearchHits()) {
            System.err.println(searchHit);
        }
    }


}
