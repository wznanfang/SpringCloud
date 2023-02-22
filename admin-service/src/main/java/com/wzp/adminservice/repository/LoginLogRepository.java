package com.wzp.adminservice.repository;

import com.wzp.adminservice.es.LoginLogEs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface LoginLogRepository extends ElasticsearchRepository<LoginLogEs, String> {

    /**
     * 查询满足条件的总条数
     *
     * @param username
     * @return
     */
    Long countAllByUsernameLike(String username);

    /**
     * 分页查询满足条件的数据
     *
     * @param username
     * @param pageable
     * @return
     */
    List<LoginLogEs> findAllByUsernameLike(String username, Pageable pageable);

}
