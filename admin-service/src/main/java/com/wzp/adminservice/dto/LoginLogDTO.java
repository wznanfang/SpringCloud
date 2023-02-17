package com.wzp.adminservice.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginLogDTO implements Serializable {
    private static final long serialVersionUID = 10311087461480759L;

    private Integer adminId;

    private String username;


}
