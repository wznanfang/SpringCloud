package com.wzp.adminservice.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;

@Data
@Document(indexName = "login_log")
public class LoginLogEs implements Serializable {

    /**
     * id
     */
    @Id
    private Integer id;

    /**
     * admin用户id
     */
    @Field
    private Integer adminId;

    /**
     * 用户名
     */
    @Field
    private String username;

    /**
     * 登录时间
     */
    @Field
    private String loginTime;

    /**
     * 创建时间
     */
    @Field
    private String createTime;

    /**
     * 修改时间
     */
    @Field
    private String updateTime;

}
