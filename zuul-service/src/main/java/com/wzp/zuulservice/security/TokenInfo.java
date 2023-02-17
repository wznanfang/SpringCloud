package com.wzp.zuulservice.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author zp.wei
 * @date 2023/2/15 20:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenInfo {

    private boolean active;       //令牌是否可用
    private String client_id;     //令牌是发给哪个客户端应用的
    private String[] scope;       //令牌拥有的对资源服务器的操作权限
    private String user_name;     //令牌发送给哪个用户
    private String[] aud;         //令牌可以访问哪些资源服务器
    private Date exp;             //令牌过期时间
    private String[] authorities; //令牌拥有的所有权限信息

}
