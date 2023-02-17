package com.wzp.adminservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzp.adminservice.dao.Admin;
import com.wzp.adminservice.vo.AdminAddVO;
import com.wzp.adminservice.vo.LoginVO;

import java.util.Map;

/**
 * @author zp.wei
 * @date 2023/2/15 21:39
 */
public interface AdminService extends IService<Admin> {

    Admin findByUsername(String username);

    Map login(LoginVO loginVO);

    /**
     * 新增用户
     *
     * @param addVO
     */
    void add(AdminAddVO addVO);



}
