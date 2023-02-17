package com.wzp.zuulservice.security;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zp.wei
 * @date 2023/2/15 20:17
 */
@Component
public class OAuth2Filter extends ZuulFilter {

    @Autowired
    private RestTemplate restTemplate;

    //过滤类型
    @Override
    public String filterType() {
        return "pre";
    }

    //过滤器优先级
    @Override
    public int filterOrder() {
        return 1;
    }

    //是否要进行过滤（即该过滤器是否要生效）
    @Override
    public boolean shouldFilter() {
        return true;
    }

    //具体的过滤操作 => 认证逻辑
    @Override
    public Object run() {
        System.out.println("认证开始=========>>>>>>>");
        //获取当前请求的上下文 => 为了获取当前的请求对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        //获取当前的请求对象
        HttpServletRequest request = requestContext.getRequest();
        //① 判断请求是否需要认证
        if (StringUtils.startsWith(request.getRequestURI(), "/auth")) {
            //以 '/token' 开头的请求是发往认证服务器的，是不需要经过认证的
            return null;
        }
        //② 需要认证的话是否携带认证信息了
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.isBlank(authHeader)) {
            //没带信息，往下走
            return null;
        }
        //③ 带的认证信息类型是否正确 => 需要以  bearer 开头
        if (!StringUtils.startsWithIgnoreCase(authHeader, "bearer ")) {
            //OAuth2.0 是 bearer 类型的，如果不是，那也继续往下走
            return null;
        }
        //④ 类型正确的话解析后看看是否有权限
        try {
            //将令牌传到认证服务器去进行鉴别，并将鉴别结果封装到我们自定义的封装类 TokenInfo 中
            TokenInfo info = getTokenInfo(authHeader);
            //正常获取到 tokenInfo 的话，放到请求域中
            request.setAttribute("tokenInfo", info);
        } catch (Exception e) {
            //如果获取 tokenInfo 过程抛出异常
            System.out.println("Get token info failed");
        }
        return null;
    }

    //将令牌转发到认证服务器去验证，并将验证信息并放到我们自定义的封装类 TokenInfo 中
    private TokenInfo getTokenInfo(String authHeader) {
        //去掉 token 的前缀 bearer
        String token = StringUtils.substringAfter(authHeader, "bearer ");
        //认证服务器检查 token 的地址
        String oauthServiceCheckTokenUrl = "http://localhost:10000/oauth/check_token";
        //封装请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //网关也是一个客户端，我们也需要将它注册到认证服务器中
        headers.setBasicAuth("zuul", "123456");
        //封装请求实体
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", token);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<TokenInfo> response = restTemplate.exchange(oauthServiceCheckTokenUrl, HttpMethod.POST, entity, TokenInfo.class);
        //拿到响应实体
        TokenInfo tokenInfo = response.getBody();
        System.out.println("tokenInfo is " + tokenInfo);
        return tokenInfo;
    }


}

