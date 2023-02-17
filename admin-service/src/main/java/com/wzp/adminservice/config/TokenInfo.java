package com.wzp.adminservice.config;

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

    private String access_token;
    private String token_type;
    private Long expires_in;
    private String scope;

}
