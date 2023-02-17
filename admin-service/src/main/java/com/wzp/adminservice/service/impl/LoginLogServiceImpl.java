package com.wzp.adminservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzp.adminservice.dao.LoginLog;
import com.wzp.adminservice.service.LoginLogService;
import com.wzp.adminservice.mapper.LoginLogMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【login_log】的数据库操作Service实现
* @createDate 2023-02-16 14:54:52
*/
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog>
    implements LoginLogService{

}




