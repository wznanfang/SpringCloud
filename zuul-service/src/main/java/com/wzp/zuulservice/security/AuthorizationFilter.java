package com.wzp.zuulservice.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.wzp.zuulservice.config.IgnorURL;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * @author zp.wei
 * @date 2023/2/15 20:23
 */
@Component
public class AuthorizationFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    //授权
    @Override
    public Object run() {
        System.out.println("授权开始=============>>>>>>>>>>>>>>>>>>");
        //拿到当前请求上下文
        RequestContext requestContext = RequestContext.getCurrentContext();
        //拿到请求体
        HttpServletRequest request = requestContext.getRequest();
        //① 该请求是否需要权限
        if (isNeedAuth(request)) {
            //② 需要认证，那就拿出 tokenInfo
            TokenInfo tokenInfo = (TokenInfo) request.getAttribute("tokenInfo");
            //③ tokenInfo 是否存在，存在是否可用
            if (tokenInfo != null && tokenInfo.isActive()) {
                //④ 认证成功（能拿到用户信息且有效） => 是否有权限
                if (hasPermissions(tokenInfo, request)) {
                    //有权限就放行
                    //将用户信息放进header中
                    requestContext.addZuulRequestHeader("username", tokenInfo.getUser_name());
                } else {
                    //没有权限，记录审计日志
                    System.out.println("异常审计===============>>>>>403 错误，授权失败....");
                    handleError(403, requestContext);
                }
            } else {
                if (StringUtils.startsWith(request.getRequestURI(), "/auth")) {
                    // '/auth' 开头的请求放行
                } else {
                    System.out.println("异常审计=============>>>>>401 错误，认证失败...");
                    //处理异常
                    handleError(401, requestContext);
                }
            }
        }
        return null;
    }

    //判断是否拥有权限
    private boolean hasPermissions(TokenInfo tokenInfo, HttpServletRequest request) {
        //这里按道理是要查询数据库然后进行对比的
        return true;
    }

    //处理异常 => 发送异常信息，并且停止前行
    private void handleError(int status, RequestContext requestContext) {
        requestContext.getResponse().setContentType("application/json");
        requestContext.setResponseStatusCode(status);
        requestContext.setResponseBody("{\"message: \": \"auth fail\"}");
        requestContext.setSendZuulResponse(false);
    }

    //判断当前请求是否需要认证
    private boolean isNeedAuth(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        for (String s: IgnorURL.urls){
            if (s.equals(getUrl(url))){
                return false;
            }
        }
        return true;
    }

    private String getUrl(String url) {
        String[] urls = url.split("/");
        StringBuffer newUrl = new StringBuffer();
        for (int i = 0; i < urls.length; i++){
            if (i >= 3){
                newUrl.append("/"+urls[i]);
            }
        }
        return newUrl.toString();
    }
}
