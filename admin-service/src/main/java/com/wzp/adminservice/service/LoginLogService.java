package com.wzp.adminservice.service;

import com.alibaba.fastjson.JSONObject;
import com.wzp.adminservice.dao.LoginLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wzp.adminservice.vo.LoginLogVO;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
* @author Administrator
* @description 针对表【login_log】的数据库操作Service
* @createDate 2023-02-16 14:54:52
*/
public interface LoginLogService extends IService<LoginLog> {

    JSONObject findAll(LoginLogVO loginLogVO);

    void loginLogExcel(LoginLogVO loginLogVO, HttpServletResponse response) throws UnsupportedEncodingException;


}
