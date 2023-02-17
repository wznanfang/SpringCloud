package com.wzp.adminservice.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wzp.adminservice.dao.LoginLog;
import com.wzp.adminservice.service.LoginLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaConsumer {

    @Autowired
    private LoginLogService loginLogService;


    @KafkaListener(topics = "bootTopic")
    public void processMessage(String content) {
        JSONObject jsonObject =  JSON.parseObject(content);
        LoginLog loginLog = jsonObject.toJavaObject(LoginLog.class);
        loginLogService.save(loginLog);
        System.out.println("dto:" + loginLog);
    }


}
