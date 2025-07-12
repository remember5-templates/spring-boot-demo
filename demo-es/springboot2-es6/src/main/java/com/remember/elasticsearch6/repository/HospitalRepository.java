package com.remember.elasticsearch6.repository;

import com.remember.elasticsearch6.entity.Hospital;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @author wangjiahao
 * @date 2020/12/18
 */
@Component
public interface HospitalRepository extends ElasticsearchRepository<Hospital, Long> {
}
