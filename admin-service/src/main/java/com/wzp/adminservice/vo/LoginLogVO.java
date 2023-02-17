package com.wzp.adminservice.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginLogVO implements Serializable {

    private String username;

    private Integer pageSize;

    private Integer pageNumber;

}
