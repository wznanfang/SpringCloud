package com.wzp.adminservice;

import com.alibaba.fastjson.JSONObject;
import com.wzp.adminservice.es.LoginLog;
import com.wzp.adminservice.repository.LoginLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@SpringBootTest
class AdminServiceApplicationTests {


    @Autowired
    private LoginLogRepository loginLogRepository;

    @Test
    void contextLoads() {
    }


    @Test
    void findAll() {
        /*Pageable pageable = PageRequest.of(0, 10);
        Page<LoginLog> page = loginLogRepository.findAll(pageable);
        page.getContent().forEach(book -> {
            System.out.println(book);
        });*/
        List<LoginLog> list = loginLogRepository.findAllByUsernameLike("nf",PageRequest.of(0, 10));
        System.out.println(JSONObject.toJSON(list));


    }

}
