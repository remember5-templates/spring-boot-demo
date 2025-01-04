package com.remember5.aio.service;

import com.remember5.aio.domain.TPerson;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
@DisplayName("PG和Redisson集成测试类")
class TPersonServiceTest {

    @Resource
    private TPersonService tPersonService;

    @Resource
    private RedisService redisService;
    @Test
    @DisplayName("测试保存数据")
    void testSave() {
        final TPerson tPerson = new TPerson();
        tPerson.setName("张三");
        tPerson.setAge(18);
        tPerson.setAddress("北京");
        tPerson.setCreateDate(new java.util.Date());
        tPerson.setUpdateDate(new java.util.Date());
        tPersonService.save(tPerson);

        Assertions.assertEquals("张三", tPerson.getName());
        Assertions.assertNotNull(tPerson.getId());
    }

    @Test
    @DisplayName("测试查询数据")
    void testQuery() {
        final TPerson tPerson = tPersonService.getById(1);
        Assertions.assertEquals("张三", tPerson.getName());
    }

    @Test
    @DisplayName("测试更新数据")
    void testUpdate() {
        final TPerson tPerson = tPersonService.getById(1);
        tPerson.setAge(19);
        tPersonService.updateById(tPerson);
        Assertions.assertEquals(19, tPersonService.getById(1).getAge());
    }

    @Test
    @DisplayName("测试redis")
    void testSaveRedis(){

        final TPerson tPerson = tPersonService.getById(1L);
        final String key = "testPerson";
        redisService.setString(key, tPerson, 30, TimeUnit.SECONDS);

        final Object string = redisService.getString(key);
        Assertions.assertEquals(tPerson, string);
    }


}
