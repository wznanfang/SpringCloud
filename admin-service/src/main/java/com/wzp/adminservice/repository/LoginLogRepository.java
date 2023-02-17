package com.wzp.adminservice.repository;

import com.wzp.adminservice.es.LoginLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author zp.wei
 * @date 2021/6/28 15:34
 */

public interface LoginLogRepository extends ElasticsearchRepository<LoginLog, String> {

    Long countAllByUsernameLike(String username);

    List<LoginLog> findAllByUsernameLike(String username, Pageable pageable);

}
