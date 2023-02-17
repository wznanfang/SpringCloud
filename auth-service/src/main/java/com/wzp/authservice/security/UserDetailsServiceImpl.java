package com.wzp.authservice.security;

import com.wzp.adminservice.dao.Admin;
import com.wzp.authservice.feign.AdminFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminFeignService adminFeignService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //查询用户信息
        Admin admin = adminFeignService.findByUsername(username);
        return User.withUsername(username)
                .password(admin.getPassword())
                .authorities("ROLE_ADMIN")
                .build();
    }


}

