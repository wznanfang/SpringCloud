package com.wzp.adminservice.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AdminAddVO implements Serializable {
    private static final long serialVersionUID = 10300087551480759L;
    private String name;

    private String username;

    private String password;

}
