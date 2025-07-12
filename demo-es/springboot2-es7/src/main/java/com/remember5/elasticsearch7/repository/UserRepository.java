package com.remember5.elasticsearch7.repository;

import com.remember5.elasticsearch7.entity.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Max_Qiu
 */
@Repository
public interface UserRepository extends ElasticsearchRepository<User, Integer> {

    List<User> findUsersByNameAndAddress(String name, String address);
}
