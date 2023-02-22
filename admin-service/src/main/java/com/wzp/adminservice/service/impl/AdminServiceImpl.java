package com.wzp.adminservice.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzp.adminservice.dao.Admin;
import com.wzp.adminservice.mapper.AdminMapper;
import com.wzp.adminservice.service.AdminService;
import com.wzp.adminservice.vo.AdminAddVO;
import com.wzp.adminservice.vo.LoginVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author zp.wei
 * @date 2023/2/15 21:40
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {


    @Override
    public Admin findByUsername(String username) {
        Admin admin = this.getOne(Wrappers.<Admin>lambdaQuery().eq(Admin::getUsername, username));
        return admin;
    }


}
