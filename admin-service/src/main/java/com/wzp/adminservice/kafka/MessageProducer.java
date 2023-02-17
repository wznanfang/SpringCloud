package com.wzp.adminservice.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * @author zp.wei
 * @date 2022/8/7 9:08
 */
@Configuration
public class MessageProducer {


    @Autowired
    private KafkaTemplate kafkaTemplate;


    public void send(String message){
        kafkaTemplate.send("bootTopic", message).addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                System.out.println("bootTopic发送消息成功：" + result.getRecordMetadata().topic() + "-"
                        + result.getRecordMetadata().partition() + "-" + result.getRecordMetadata().offset());
            }
            @Override
            public void onFailure(Throwable ex) {
               System.out.println("bootTopic发送消息失败："+ex.getMessage());
            }
        });
    }



}
