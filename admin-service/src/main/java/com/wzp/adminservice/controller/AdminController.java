package com.wzp.adminservice.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wzp.adminservice.config.Result;
import com.wzp.adminservice.config.TokenInfo;
import com.wzp.adminservice.dao.Admin;
import com.wzp.adminservice.dto.LoginLogDTO;
import com.wzp.adminservice.es.LoginLog;
import com.wzp.adminservice.kafka.MessageProducer;
import com.wzp.adminservice.service.AdminService;
import com.wzp.adminservice.vo.AdminAddVO;
import com.wzp.adminservice.vo.LoginVO;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.wzp.adminservice.enums.ResultCodeEnum.*;

/**
 * @author zp.wei
 * @date 2023/2/15 21:39
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MessageProducer messageProducer;


    /**
     * 根据用户名查询admin用户
     *
     * @param username
     * @return
     */
    @GetMapping("/findByUsername/{username}")
    public Admin findByUsername(@PathVariable("username") String username) {
        return adminService.getOne(Wrappers.<Admin>lambdaQuery().eq(Admin::getUsername, username));
    }


    /**
     * 登录
     *
     * @param loginVO
     * @param request
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginVO loginVO, HttpServletRequest request) {
        if (StringUtils.isEmpty(loginVO.getUsername()) || StringUtils.isEmpty(loginVO.getPassword())) {
            return Result.error(USERNAME_ERROR);
        }
        Admin admin = adminService.getOne(Wrappers.<Admin>lambdaQuery().eq(Admin::getUsername, loginVO.getUsername()));
        if (ObjectUtils.isEmpty(admin)) {
            return Result.error(USER_ERROR);
        }
        boolean flag = passwordEncoder.matches(loginVO.getPassword(), admin.getPassword());
        if (!flag) {
            return Result.error(ERROR_USERNAME_OR_PASSWORD);
        }
        //获取token
        ResponseEntity<TokenInfo> response = getTokenInfoResponseEntity(loginVO, request);
        Map<String, Object> map = new HashMap<>(2);
        map.put("admin", admin);
        map.put("token", response.getBody());
        admin.setLoginTime(new Date());
        adminService.updateById(admin);
        //处理登录日志
        LoginLogDTO loginLogDTO = new LoginLogDTO();
        loginLogDTO.setAdminId(admin.getId());
        loginLogDTO.setUsername(admin.getUsername());
        messageProducer.send(JSONObject.toJSONString(loginLogDTO));
        return Result.ok(map);
    }

    private ResponseEntity<TokenInfo> getTokenInfoResponseEntity(LoginVO loginVO, HttpServletRequest request) {
        String oauthServiceUrl = "http://localhost:9999/auth/oauth/token";
        //封装参数
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setBasicAuth("admin", "123456");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", loginVO.getUsername());
        params.add("password", loginVO.getPassword());
        params.add("grant_type", "password");
        params.add("scope", "all");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, httpHeaders);
        //发送请求
        ResponseEntity<TokenInfo> response = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
        request.getSession().setAttribute("token", response.getBody());
        System.out.println(response.getBody());
        return response;
    }


    /**
     * 注册
     *
     * @param addVO
     * @return
     */
    @PostMapping("/register")
    public Result<Admin> register(@RequestBody AdminAddVO addVO) {
        Admin admin = adminService.getOne(Wrappers.<Admin>lambdaQuery().eq(Admin::getUsername, addVO.getUsername()));
        if (ObjectUtils.isNotEmpty(admin)) {
            return Result.error(USER_HAS_EXIST);
        }
        admin = new Admin();
        admin.setName(addVO.getName());
        admin.setUsername(addVO.getUsername());
        admin.setPassword(new BCryptPasswordEncoder().encode(addVO.getPassword()));
        adminService.save(admin);
        return Result.ok(admin);
    }




}
