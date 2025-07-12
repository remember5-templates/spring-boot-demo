package com.remember.elasticsearch6.repository;

import com.google.common.collect.Lists;
import com.remember.elasticsearch6.ElasticSearchDemoApplicationTests;
import com.remember.elasticsearch6.entity.FilmEntity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */
@Slf4j
public class FilmRepositoryTest extends ElasticSearchDemoApplicationTests {

    @Autowired
    FilmRepository repo;

    /**
     * 测试新增
     */
    @Test
    public void test() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        Date date = Date.from(instant);
        FilmEntity filmEntity1 = new FilmEntity(System.currentTimeMillis(), "测吗", "中国加油", date);
        FilmEntity filmEntity2 = new FilmEntity(System.currentTimeMillis(), "小明", "为了更美好的明天而战", date);
        FilmEntity filmEntity3 = new FilmEntity(System.currentTimeMillis(), "小红", "明天就是周五了", date);
        FilmEntity filmEntity4 = new FilmEntity(System.currentTimeMillis(), "ceshi", "明天吃点啥呢", date);
        FilmEntity filmEntity5 = new FilmEntity(System.currentTimeMillis(), "测试", "晚饭怎么吃", date);
        repo.saveAll(Lists.newArrayList(filmEntity1, filmEntity2, filmEntity3, filmEntity4, filmEntity5));
    }

    @Test
    public void test2() {
        String content = "hong";

        //使用dis_max直接取多个query中，分数最高的那一个query的分数即可
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();

        //boost 设置权重,只搜索匹配name和disrector字段
        QueryBuilder ikNameQuery = QueryBuilders.matchQuery("name", content).boost(2f);
        QueryBuilder pinyinNameQuery = QueryBuilders.matchQuery("name.pinyin", content);
        QueryBuilder ikDirectorQuery = QueryBuilders.matchQuery("director", content).boost(2f);

        disMaxQueryBuilder.add(ikNameQuery);
        disMaxQueryBuilder.add(pinyinNameQuery);
        disMaxQueryBuilder.add(ikDirectorQuery);

        // 聚合查询
        NativeSearchQuery build = new NativeSearchQueryBuilder()
                .withQuery(disMaxQueryBuilder)
                .build();
        Page<FilmEntity> search = repo.search(build);
        search.forEach(System.err::println);
    }
}
