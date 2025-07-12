package com.remember.elasticsearch6.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.remember.elasticsearch6.entity.AssociateWord;
import com.remember.elasticsearch6.repository.AssociateWordRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */
@RequestMapping("as")
@RestController
@RequiredArgsConstructor
public class AssociateWordController {

    private final AssociateWordRepository repository;

    /**
     * 保存
     *
     * @param associateWord 具体内容
     * @return 保存结果
     */
    @GetMapping("save")
    public String save(AssociateWord associateWord) {
        repository.save(associateWord);
        return "success";
    }

    /**
     * 删除
     *
     * @param id id
     * @return 删除结果
     */
    @GetMapping("delete")
    public String delete(long id) {
        repository.deleteById(id);
        return "success";
    }

    /**
     * 更新
     *
     * @param associateWord 具体内容
     * @return 更新结果
     */
    @GetMapping("update")
    public String update(AssociateWord associateWord) {
        repository.save(associateWord);
        return "success";
    }

    /**
     * 获取
     *
     * @param id id
     * @return 获取结果
     */
    @GetMapping("getOne")
    public AssociateWord getOne(long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * 关键词查询
     *
     * @param associateWord 具体内容
     * @return 查询结果
     */
    @GetMapping("search")
    public JSONObject search(AssociateWord associateWord) {
        JSONObject result = new JSONObject();
        String keyword = associateWord.getKeyword();

        queryStringQueryBuilder(keyword, result);
        termQuery(keyword, result);
        matchQuery(keyword, result);
        fuzzyQuery(keyword, result);
        prefixQuery(keyword, result);
        wildcardQuery(keyword, result);

        return result;
    }

    /**
     * 关键词查询
     *
     * @param ids ids
     * @return 查询结果
     */
    @GetMapping("searchByIds")
    public JSONObject searchByIds(String keyword, String ids) {
        JSONObject result = new JSONObject();
        TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("id", ids.split(","));
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(termsQueryBuilder);
        boolQueryBuilder.should(QueryBuilders.prefixQuery("keyword", keyword));
        ArrayList<AssociateWord> list = Lists.newArrayList(repository.search(boolQueryBuilder));
        list.forEach(System.err::println);
        return result;
    }

    /**
     * <b>精确查询</b>
     * QueryStringQueryBuilder 指定字符串作为关键词查询，关键词支持分词
     * 查询title字段中，包含 ”开发”、“开放" 这个字符串的document；相当于把"浦东开发开放"分词了，再查询；
     * QueryBuilders.queryStringQuery("开发开放").defaultField("title");
     * 不指定feild，查询范围为所有feild
     * QueryBuilders.queryStringQuery("青春");
     * 指定多个feild
     * QueryBuilders.queryStringQuery("青春").field("title").field("content");
     *
     * @param keyword 查询关键字
     * @param result  结果
     */
    private void queryStringQueryBuilder(String keyword, JSONObject result) {
        System.err.println("queryStringQueryBuilder");
        ArrayList<AssociateWord> list = Lists.newArrayList(repository.search(QueryBuilders.queryStringQuery(keyword).defaultField("keyword")).iterator());
        list.forEach(System.err::println);
        result.put("queryStringQueryBuilder", list);
    }

    /**
     * <b>精确查询</b>
     * 以关键字“开发开放”，关键字不支持分词
     * QueryBuilders.termQuery("title", "开发开放")
     * QueryBuilders.termsQuery("fieldName", "fieldlValue1","fieldlValue2...")
     *
     * @param keyword 查询关键字
     * @param result  结果
     */
    private void termQuery(String keyword, JSONObject result) {
        System.err.println("termQuery");
        ArrayList<AssociateWord> list = Lists.newArrayList(repository.search(QueryBuilders.termQuery("keyword", keyword)).iterator());
        list.forEach(System.err::println);
        result.put("termQuery", list);
    }

    /**
     * <b>精确查询</b>
     * 以关键字“开发开放”，关键字支持分词
     * QueryBuilders.matchQuery("title", "开发开放")
     * QueryBuilders.multiMatchQuery("fieldlValue", "fieldName1", "fieldName2", "fieldName3")
     *
     * @param keyword 查询关键字
     * @param result  结果
     */
    private void matchQuery(String keyword, JSONObject result) {
        System.err.println("matchQuery");
        ArrayList<AssociateWord> list = Lists.newArrayList(repository.search(QueryBuilders.matchQuery("keyword", keyword)).iterator());
        list.forEach(System.err::println);
        result.put("matchQuery", list);
    }

    /**
     * <b>模糊查询</b>
     * 模糊，是指查询关键字与目标关键字可以模糊匹配
     * QueryBuilders.fuzzyQuery("title", "开发开放").fuzziness(Fuzziness.ONE)。
     *
     * @param keyword 查询关键字
     * @param result  结果
     */
    private void fuzzyQuery(String keyword, JSONObject result) {
        System.err.println("fuzzyQuery");
        ArrayList<AssociateWord> list = Lists.newArrayList(repository.search(QueryBuilders.fuzzyQuery("keyword", keyword)).iterator());
        list.forEach(System.err::println);
        result.put("fuzzyQuery", list);
    }

    /**
     * <b>模糊查询</b>
     * 前缀查询，查询title中以“开发开放”为前缀的document；
     * QueryBuilders.prefixQuery("title", "开发开放")
     *
     * @param keyword 查询关键字
     * @param result  结果
     */
    private void prefixQuery(String keyword, JSONObject result) {
        System.err.println("prefixQuery");
        ArrayList<AssociateWord> list = Lists.newArrayList(repository.search(QueryBuilders.prefixQuery("keyword", keyword)).iterator());
        list.forEach(System.err::println);
        result.put("prefixQuery", list);
    }


    /**
     * <b>模糊查询</b>
     * 通配符查询，支持*和？，？表示单个字符；注意不建议将通配符作为前缀，否则导致查询很慢
     * QueryBuilders.wildcardQuery("title", "开*放")
     * QueryBuilders.wildcardQuery("title", "开？放")
     *
     * @param keyword 查询关键字
     * @param result  结果
     */
    private void wildcardQuery(String keyword, JSONObject result) {
        System.err.println("wildcardQuery");
        ArrayList<AssociateWord> list = Lists.newArrayList(repository.search(QueryBuilders.wildcardQuery("keyword", keyword)).iterator());
        list.forEach(System.err::println);
        result.put("wildcardQuery", list);
    }


}
