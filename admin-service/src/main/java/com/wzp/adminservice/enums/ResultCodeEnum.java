package com.wzp.adminservice.enums;

/**
 * @Author: zp.wei
 * @DATE: 2020/9/1 10:31
 */
public enum ResultCodeEnum {

    RESULT_SUCCESS(true, 0, "请求成功"),
    BUSINESS_FAIL(false, 1, "业务处理失败"),
    FORBIDDEN(false, 400, "授权失败"),
    UNAUTHORIZED(false, 401, "未经授权的请求"),
    INVALID_TOKEN(false, 402, "无效的token"),
    ACCESS_DENIED(false, 403, "无权访问"),

    SYSTEM_ERROR(false, 500, "系统错误"),

    USERNAME_ERROR(false, 10001, "用户名和密码不能为空"),
    USER_HAS_EXIST(false, 10002, "用户已存在"),
    USER_ERROR(false, 10003, "用户不存在"),
    ERROR_USERNAME_OR_PASSWORD(false, 10004, "账户名或密码错误"),
    ;


    private Boolean success;

    private Integer code;

    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    private ResultCodeEnum(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }


}
