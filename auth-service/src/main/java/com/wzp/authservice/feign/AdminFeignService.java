package com.wzp.authservice.feign;

import com.wzp.adminservice.dao.Admin;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "admin-service")
public interface AdminFeignService {


    @GetMapping("/admin/findByUsername/{username}")
    Admin findByUsername(@PathVariable("username") String username);

}
