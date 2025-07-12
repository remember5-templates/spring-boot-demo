package com.remember.elasticsearch6.repository;

import cn.hutool.json.JSONUtil;
import com.remember.elasticsearch6.ElasticSearchDemoApplicationTests;
import com.remember.elasticsearch6.entity.HotWireKb;
import com.remember.elasticsearch6.service.HotWireKbService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;

@Slf4j
class HotWireKbRepositoryTest extends ElasticSearchDemoApplicationTests {
    @Autowired
    private HotWireKbRepository repo;

    @Autowired
    private HotWireKbService hotWireKbService;

    /**
     * 测试新增
     */
    @Test
    public void save() {
//        Person person = new Person(1L, "刘备", "蜀国", 18, DateUtil.parse("1990-01-02 03:04:05"), "刘备（161年－223年6月10日），即汉昭烈帝（221年－223年在位），又称先主，字玄德，东汉末年幽州涿郡涿县（今河北省涿州市）人，西汉中山靖王刘胜之后，三国时期蜀汉开国皇帝、政治家。\n刘备少年时拜卢植为师；早年颠沛流离，备尝艰辛，投靠过多个诸侯，曾参与镇压黄巾起义。先后率军救援北海相孔融、徐州牧陶谦等。陶谦病亡后，将徐州让与刘备。赤壁之战时，刘备与孙权联盟击败曹操，趁势夺取荆州。而后进取益州。于章武元年（221年）在成都称帝，国号汉，史称蜀或蜀汉。《三国志》评刘备的机权干略不及曹操，但其弘毅宽厚，知人待士，百折不挠，终成帝业。刘备也称自己做事“每与操反，事乃成尔”。\n章武三年（223年），刘备病逝于白帝城，终年六十三岁，谥号昭烈皇帝，庙号烈祖，葬惠陵。后世有众多文艺作品以其为主角，在成都武侯祠有昭烈庙为纪念。");
//        Person save = repo.save(person);
//        log.info("【save】= {}", save2);
    }

    /**
     * 测试批量新增
     */
    @Test
    public void saveList() {
        List<HotWireKb> list = hotWireKbService.list();
        System.err.println(list.size());
        System.err.println(list.get(1));
        repo.saveAll(list);
//        List<Person> personList = Lists.newArrayList();
//        personList.add(new Person(2L, "曹操", "魏国", 20, DateUtil.parse("1988-01-02 03:04:05"), "曹操（155年－220年3月15日），字孟德，一名吉利，小字阿瞒，沛国谯县（今安徽亳州）人。东汉末年杰出的政治家、军事家、文学家、书法家，三国中曹魏政权的奠基人。\n曹操曾担任东汉丞相，后加封魏王，奠定了曹魏立国的基础。去世后谥号为武王。其子曹丕称帝后，追尊为武皇帝，庙号太祖。\n东汉末年，天下大乱，曹操以汉天子的名义征讨四方，对内消灭二袁、吕布、刘表、马超、韩遂等割据势力，对外降服南匈奴、乌桓、鲜卑等，统一了中国北方，并实行一系列政策恢复经济生产和社会秩序，扩大屯田、兴修水利、奖励农桑、重视手工业、安置流亡人口、实行“租调制”，从而使中原社会渐趋稳定、经济出现转机。黄河流域在曹操统治下，政治渐见清明，经济逐步恢复，阶级压迫稍有减轻，社会风气有所好转。曹操在汉朝的名义下所采取的一些措施具有积极作用。\n曹操军事上精通兵法，重贤爱才，为此不惜一切代价将看中的潜能分子收于麾下；生活上善诗歌，抒发自己的政治抱负，并反映汉末人民的苦难生活，气魄雄伟，慷慨悲凉；散文亦清峻整洁，开启并繁荣了建安文学，给后人留下了宝贵的精神财富，鲁迅评价其为“改造文章的祖师”。同时曹操也擅长书法，唐朝张怀瓘在《书断》将曹操的章草评为“妙品”。"));
//        personList.add(new Person(3L, "孙权", "吴国", 19, DateUtil.parse("1989-01-02 03:04:05"), "孙权（182年－252年5月21日），字仲谋，吴郡富春（今浙江杭州富阳区）人。三国时代孙吴的建立者（229年－252年在位）。\n孙权的父亲孙坚和兄长孙策，在东汉末年群雄割据中打下了江东基业。建安五年（200年），孙策遇刺身亡，孙权继之掌事，成为一方诸侯。建安十三年（208年），与刘备建立孙刘联盟，并于赤壁之战中击败曹操，奠定三国鼎立的基础。建安二十四年（219年），孙权派吕蒙成功袭取刘备的荆州，使领土面积大大增加。\n黄武元年（222年），孙权被魏文帝曹丕册封为吴王，建立吴国。同年，在夷陵之战中大败刘备。黄龙元年（229年），在武昌正式称帝，国号吴，不久后迁都建业。孙权称帝后，设置农官，实行屯田，设置郡县，并继续剿抚山越，促进了江南经济的发展。在此基础上，他又多次派人出海。黄龙二年（230年），孙权派卫温、诸葛直抵达夷州。\n孙权晚年在继承人问题上反复无常，引致群下党争，朝局不稳。太元元年（252年）病逝，享年七十一岁，在位二十四年，谥号大皇帝，庙号太祖，葬于蒋陵。\n孙权亦善书，唐代张怀瓘在《书估》中将其书法列为第三等。"));
//        personList.add(new Person(4L, "诸葛亮", "蜀国", 16, DateUtil.parse("1992-01-02 03:04:05"), "诸葛亮（181年-234年10月8日），字孔明，号卧龙，徐州琅琊阳都（今山东临沂市沂南县）人，三国时期蜀国丞相，杰出的政治家、军事家、外交家、文学家、书法家、发明家。\n早年随叔父诸葛玄到荆州，诸葛玄死后，诸葛亮就在襄阳隆中隐居。后刘备三顾茅庐请出诸葛亮，联孙抗曹，于赤壁之战大败曹军。形成三国鼎足之势，又夺占荆州。建安十六年（211年），攻取益州。继又击败曹军，夺得汉中。蜀章武元年（221年），刘备在成都建立蜀汉政权，诸葛亮被任命为丞相，主持朝政。蜀后主刘禅继位，诸葛亮被封为武乡侯，领益州牧。勤勉谨慎，大小政事必亲自处理，赏罚严明；与东吴联盟，改善和西南各族的关系；实行屯田政策，加强战备。前后六次北伐中原，多以粮尽无功。终因积劳成疾，于蜀建兴十二年（234年）病逝于五丈原（今陕西宝鸡岐山境内），享年54岁。刘禅追封其为忠武侯，后世常以武侯尊称诸葛亮。东晋政权因其军事才能特追封他为武兴王。\n诸葛亮散文代表作有《出师表》《诫子书》等。曾发明木牛流马、孔明灯等，并改造连弩，叫做诸葛连弩，可一弩十矢俱发。诸葛亮一生“鞠躬尽瘁、死而后已”，是中国传统文化中忠臣与智者的代表人物。"));
//        Iterable<Person> people = repo.saveAll(personList);
//        log.info("【people】= {}", people);
    }

    /**
     * 测试更新
     */
    @Test
    public void update() {
        repo.findById(1L).ifPresent(hotWireKb -> {
            hotWireKb.setTopLevel(1);
            HotWireKb save = repo.save(hotWireKb);
            log.info("【save】= {}", save);
        });
    }

    /**
     * 测试删除
     */
    @Test
    public void delete() {
        // 主键删除
        repo.deleteById(1L);
        // 对象删除
        repo.findById(2L).ifPresent(hotWireKb -> repo.delete(hotWireKb));
        // 批量删除
        repo.deleteAll(repo.findAll());
    }

    /**
     * 测试普通查询，按id倒序
     */
    @Test
    public void select() {
        Iterable<HotWireKb> id = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        for (HotWireKb HotWireKb : id) {
            System.err.println(HotWireKb);
        }
//        System.err.println(id);
//        id.forEach(HotWireKb -> log.info("{}",HotWireKb));
    }

    /**
     * 自定义查询，根据积分范围查询 200～1000
     */
    @Test
    public void customSelectRangeOfId() {
//        List<HotWireKb> byPointsValueBetween = repo.findByIdBetween(200, 1000);
//        System.err.println(byPointsValueBetween);
//        repo.findByAgeBetween(18, 19).forEach(person -> log.info("{} 年龄: {}", person.getName(), person.getAge()));
    }

    //  ============================== 精确查询 ==============================


    /**
     * 数字
     */
    @Test
    public void test1() {
        // 单个
        TermQueryBuilder id = QueryBuilders.termQuery("id", 1);
        repo.search(id).forEach(System.err::println);

        //批量
        TermsQueryBuilder id1 = QueryBuilders.termsQuery("id", Lists.newArrayList(1, 2, 3));
        repo.search(id1).forEach(System.err::println);
    }


    /**
     * 字符串：完全匹配
     */
    @Test
    public void test2() {
        // 单个
        TermQueryBuilder goodsName = QueryBuilders.termQuery("attrsKeyword", "稻草");
        Iterable<HotWireKb> search = repo.search(goodsName);
        //批量
        TermsQueryBuilder goodsNames = QueryBuilders
                .termsQuery("attrsKeyword",
                        Lists.newArrayList("稻草;平菇", "大白菜"));
        repo.search(goodsNames).forEach(System.err::println);
    }

    //  ============================== 精确查询 ==============================


    /**
     * 模糊查询 * = %  || ? = _
     * 数字查询都为精确查询
     * 字符串
     */
    @Test
    public void test3() {
        // 使用* 和 ?
        WildcardQueryBuilder goodsName = QueryBuilders.wildcardQuery("attrsKeyword", "*春番茄*");
        Iterable<HotWireKb> search1 = repo.search(goodsName);
        search1.forEach(System.out::println);

//        MoreLikeThisQueryBuilder moreLikeThisQueryBuilder
//                = QueryBuilders.moreLikeThisQuery(
//                new String[]{"goodsName"}, new String[]{"“春蕾计划”捐赠500积分", "“春蕾计划”捐赠1000积分"}, null);
//        Iterable<HotWireKb> search = repo.search(moreLikeThisQueryBuilder);
//        search.forEach(System.out::println);
    }

    /**
     * 分词查询
     */
    @Test
    public void test4() {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        String prefix = "复合肥,我想吃番茄";
        // Java分词器 jieba


        boolQueryBuilder.should(QueryBuilders.prefixQuery("attrsKeyword", prefix))
                .should(QueryBuilders.prefixQuery("text", prefix))
                .should(QueryBuilders.prefixQuery("title", prefix))
                .must(QueryBuilders.rangeQuery("id").from(107802).to(108000));

        Iterable<HotWireKb> search = repo.search(boolQueryBuilder);
        search.forEach(System.err::println);

    }


    /**
     * 使用QueryBuilder：
     * termQuery("key", obj) 完全匹配
     * termsQuery("key", obj1, obj2..) 一次匹配多个值
     * matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
     * multiMatchQuery("text", "field1", "field2"..); 匹配多个字段, field有通配符不行 //搜索field1中或field1中包含有text的文档（必须与text一致）
     * matchAllQuery(); 匹配所有文件 组合查询：：
     * wildcardQuery("field","val"); 模糊查询，?匹配单个字符，*匹配多个字符
     * BoolQueryBuilder 进行复合查询 结合must mustNot should等用
     * must(QueryBuilders) : AND：
     * mustNot(QueryBuilders): NOT：
     * should: : OR：
     */
    @Test
    public void customSelectRangeOfAge() {
        // 根据字段 byXXXXBetween 来搜索
//        List<HotWireKb> byPointsValueBetween = repo.findByIdBetween(107800, 107900);
//        List<HotWireKb> byGoodsPriceBetween = repo.findByIdBetween(100, 200);
//        System.err.println(byPointsValueBetween);
//        System.err.println(byGoodsPriceBetween);

        // 数字
        // 闭区间查询
        RangeQueryBuilder pointsValue = QueryBuilders.rangeQuery("createBy").from(200).to(1000);
        Iterable<HotWireKb> search = repo.search(pointsValue);
        System.err.println(search);

        // 开区间查询
        RangeQueryBuilder pointsValue1 = QueryBuilders.rangeQuery("createBy").from(200, false).to(1000, false);
        Iterable<HotWireKb> search1 = repo.search(pointsValue1);
        System.err.println(search1);

        // 大于
        RangeQueryBuilder pointsValue2 = QueryBuilders.rangeQuery("createBy").gt(200);
        Iterable<HotWireKb> search2 = repo.search(pointsValue2);
        System.err.println(search2);

        RangeQueryBuilder pointsValue3 = QueryBuilders.rangeQuery("createBy").gte(200);
        Iterable<HotWireKb> search3 = repo.search(pointsValue3);
        System.err.println(search3);
        // 小于
        RangeQueryBuilder pointsValue4 = QueryBuilders.rangeQuery("createBy").lt(200);
        Iterable<HotWireKb> search4 = repo.search(pointsValue4);
        System.err.println(search4);

        RangeQueryBuilder pointsValue5 = QueryBuilders.rangeQuery("createBy").lte(200);
        Iterable<HotWireKb> search5 = repo.search(pointsValue5);
        System.err.println(search5);

        // 多条件查询
        WildcardQueryBuilder goodsName = QueryBuilders.wildcardQuery("createBy", "*积分*");
        RangeQueryBuilder pointsValue6 = QueryBuilders.rangeQuery("createBy").lt(200);
        RangeQueryBuilder pointsValue7 = QueryBuilders.rangeQuery("createBy").gt(500);
        BoolQueryBuilder should = QueryBuilders.boolQuery().must(goodsName).should(pointsValue6).should(pointsValue7);
        Iterable<HotWireKb> search6 = repo.search(should);
        System.err.println(search6);


    }


    /**
     * moreLikeThisQuery: 实现基于内容推荐, 支持实现一句话相似文章查询
     * {
     * "more_like_this" : {
     * "fields" : ["title", "content"],   // 要匹配的字段, 不填默认_all
     * "like_text" : "text like this one",   // 匹配的文本
     * }
     * }
     * percent_terms_to_match：匹配项（term）的百分比，默认是0.3
     * min_term_freq：一篇文档中一个词语至少出现次数，小于这个值的词将被忽略，默认是2
     * max_query_terms：一条查询语句中允许最多查询词语的个数，默认是25
     * stop_words：设置停止词，匹配时会忽略停止词
     * min_doc_freq：一个词语最少在多少篇文档中出现，小于这个值的词会将被忽略，默认是无限制
     * max_doc_freq：一个词语最多在多少篇文档中出现，大于这个值的词会将被忽略，默认是无限制
     * min_word_len：最小的词语长度，默认是0
     * max_word_len：最多的词语长度，默认无限制
     * boost_terms：设置词语权重，默认是1
     * boost：设置查询权重，默认是1
     * analyzer：设置使用的分词器，默认是使用该字段指定的分词器
     */
    @Test
    public void testMoreLikeThisQuery() {
        QueryBuilder queryBuilder = QueryBuilders
                .moreLikeThisQuery(new String[]{"attrsKeyword"}, new String[]{"小葱;变黄", "花菜;育苗", "毛豆;豆荚螟"}, null)
                .minTermFreq(1)         //最少出现的次数
                .maxQueryTerms(12);        // 最多允许查询的词语
        Iterable<HotWireKb> search = repo.search(queryBuilder);
        search.forEach(System.out::println);
    }

    /**
     * 使用QueryBuilder
     * termQuery("key", obj) 完全匹配
     * termsQuery("key", obj1, obj2..)   一次匹配多个值
     * matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
     * multiMatchQuery("text", "field1", "field2"..);  匹配多个字段, field有通配符忒行
     * matchAllQuery();         匹配所有文件
     * fuzzyQuery("key",obj)  模糊查询 不能用通配符, 不知道干啥用
     */
    @Test
    public void testQueryBuilder() {
        TermQueryBuilder goodsName = QueryBuilders.termQuery("goodsName", "“春蕾计划”捐赠200积分");
        log.info("【goodsName】= {}", goodsName.toString());
        Iterable<HotWireKb> search1 = repo.search(goodsName);

        TermsQueryBuilder goodsNames = QueryBuilders.termsQuery("goodsName", "“春蕾计划”捐赠200积分", "200积分");
        log.info("【goodsNames】= {}", goodsNames.toString());
        Iterable<HotWireKb> search2 = repo.search(goodsNames);

        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("goodsName", "“春蕾计划”捐赠200积分");
        log.info("【matchQueryBuilder】= {}", matchQueryBuilder.toString());
        Iterable<HotWireKb> search3 = repo.search(matchQueryBuilder);

        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("“春蕾计划”捐赠200积分", "goodsName");
        log.info("【multiMatchQueryBuilder】= {}", multiMatchQueryBuilder.toString());
        Iterable<HotWireKb> search4 = repo.search(multiMatchQueryBuilder);

        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        log.info("【matchAllQueryBuilder】= {}", matchAllQueryBuilder.toString());
        Iterable<HotWireKb> search5 = repo.search(matchAllQueryBuilder);

        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("goodsName", "*春蕾计划*");
        log.info("【fuzzyQueryBuilder】= {}", fuzzyQueryBuilder.toString());
        Iterable<HotWireKb> search6 = repo.search(fuzzyQueryBuilder);
    }


    /**
     * 组合查询
     * must(QueryBuilders) :   AND
     * mustNot(QueryBuilders): NOT
     * should:                  : OR
     */
    @Test
    public void testQueryBuilder2() {
        QueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("goodsId", "H199981"))
                .mustNot(QueryBuilders.termQuery("message", "nihao"))
                .should(QueryBuilders.termQuery("gender", "male"));
        repo.search(queryBuilder);
    }

    /**
     * 包裹查询, 高于设定分数, 不计算相关性
     */
    @Test
    public void testConstantScoreQuery() {
        QueryBuilder queryBuilder = QueryBuilders.constantScoreQuery(
                QueryBuilders.termQuery("name", "kimchy")).boost(2.0f);
        repo.search(queryBuilder);
        // 过滤查询
//        QueryBuilders.constantScoreQuery(FilterBuilders.termQuery("name", "kimchy")).boost(2.0f);

    }

    /**
     * disMax查询
     * 对子查询的结果做union, score沿用子查询score的最大值,
     * 广泛用于muti-field查询
     */
    @Test
    public void testDisMaxQuery() {
        QueryBuilder queryBuilder = QueryBuilders.disMaxQuery()
                .add(QueryBuilders.termQuery("user", "kimch"))  // 查询条件
                .add(QueryBuilders.termQuery("message", "hello"))
                .boost(1.3f)
                .tieBreaker(0.7f);
        repo.search(queryBuilder);
    }

    /**
     * 高级查询
     */
    @Test
    public void advanceSelect() {
        // QueryBuilders 提供了很多静态方法，可以实现大部分查询条件的封装
        MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("title", "菜");
        log.info("【queryBuilder】= {}", queryBuilder.toString());

        repo.search(queryBuilder).forEach(HotWireKb -> log.info("【HotWireKb】= {}", HotWireKb));

    }


    /**
     * 通配符查询, 支持 *
     * 匹配任何字符序列, 包括空
     * 避免* 开始, 会检索大量内容造成效率缓慢
     */
    @Test
    public void testWildCardQuery() {
        WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery("title", String.format("*%s*", "菜"));
        log.info("【wildcardQueryBuilder】= {}", wildcardQueryBuilder.toString());
        Iterable<HotWireKb> search = repo.search(wildcardQueryBuilder);
        search.forEach(System.err::println);
    }


    /**
     * 自定义高级查询
     */
    @Test
    public void customAdvanceSelect() {
        // 构造查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 添加基本的分词条件
        queryBuilder.withQuery(QueryBuilders.wildcardQuery("goodsName", String.format("*%s*", "春蕾计划")));
        // 排序条件
        queryBuilder.withSort(SortBuilders.fieldSort("id").order(SortOrder.DESC));
        // 分页条件
        queryBuilder.withPageable(PageRequest.of(0, 2));
        Page<HotWireKb> HotWireKbs = repo.search(queryBuilder.build());
        log.info("【HotWireKbs】总条数 = {}", HotWireKbs.getTotalElements());
        log.info("【HotWireKbs】总页数 = {}", HotWireKbs.getTotalPages());
        HotWireKbs.forEach(System.out::println);
    }

    /**
     * 测试聚合，测试平均年龄
     */
    @Test
    public void agg() {
        // 构造查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 不查询任何结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));

        // 平均年龄
        queryBuilder.addAggregation(AggregationBuilders.avg("avg").field("goodsPrice"));

        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));

        AggregatedPage<HotWireKb> people = (AggregatedPage<HotWireKb>) repo.search(queryBuilder.build());
        Aggregation avg = people.getAggregation("avg");
        log.info("【avgAge】= {}", avg);
    }

    /**
     * 测试高级聚合查询，每个国家的人有几个，每个国家的平均年龄是多少
     */
    @Test
    public void advanceAgg() {
        // 构造查询条件
//        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
//        // 不查询任何结果
//        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{""}, null));
//
//        // 1. 添加一个新的聚合，聚合类型为terms，聚合名称为country，聚合字段为age
//        queryBuilder.addAggregation(AggregationBuilders.terms("country").field("country")
//                // 2. 在国家聚合桶内进行嵌套聚合，求平均年龄
//                .subAggregation(AggregationBuilders.avg("avg").field("age")));
//
//        log.info("【queryBuilder】= {}", JSONUtil.toJsonStr(queryBuilder.build()));
//
//        // 3. 查询
//        AggregatedPage<Person> people = (AggregatedPage<Person>) repo.search(queryBuilder.build());
//
//        // 4. 解析
//        // 4.1. 从结果中取出名为 country 的那个聚合，因为是利用String类型字段来进行的term聚合，所以结果要强转为StringTerm类型
//        StringTerms country = (StringTerms) people.getAggregation("country");
//        // 4.2. 获取桶
//        List<StringTerms.Bucket> buckets = country.getBuckets();
//        for (StringTerms.Bucket bucket : buckets) {
//            // 4.3. 获取桶中的key，即国家名称  4.4. 获取桶中的文档数量
//            log.info("{} 总共有 {} 人", bucket.getKeyAsString(), bucket.getDocCount());
//            // 4.5. 获取子聚合结果：
//            InternalAvg avg = (InternalAvg) bucket.getAggregations().asMap().get("avg");
//            log.info("平均年龄：{}", avg);
//        }
    }
}