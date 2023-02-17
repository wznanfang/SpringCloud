package com.wzp.adminservice.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 10300087461480759L;

    private String username;

    private String password;

}
