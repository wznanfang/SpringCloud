package com.wzp.zuulservice.security;

import com.netflix.zuul.ZuulFilter;
import org.springframework.stereotype.Component;


/**
 * @author zp.wei
 * @date 2023/2/15 20:22
 */
@Component
public class AuditLogFilter extends ZuulFilter {

    //在请求进来前执行
    @Override
    public String filterType() {
        return "pre";
    }

    //在认证后面
    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    //打印审计信息
    @Override
    public Object run() {
        System.out.println("审计开始 =======>>>>>>>");
        return null;
    }

}
