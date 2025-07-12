package com.remember.elasticsearch6.repository;


import com.remember.elasticsearch6.entity.HotWireKb;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author wangjiahao
 * @date 2020/3/26
 */
public interface HotWireKbRepository extends ElasticsearchRepository<HotWireKb, Long> {

//    /**
//     * 根据年龄区间查询
//     *
//     * @param min 最小值
//     * @param max 最大值
//     * @return 满足条件的用户列表
//     */
//    List<HotWireKb> findByIdBetween(Integer min, Integer max);
}
