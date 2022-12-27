package com.feign.client;

import com.base.core.constant.SecurityConstants;
import com.base.core.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "authentication-server", contextId = "authorizationService")
public interface AuthorizationClient {

//    @GetMapping(value = "/feign/authorization/findAccessToken", produces = "application/json; charset=UTF-8")
//    Map<String, Object> findAccessToken(@RequestParam("token") String token, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping(value = "/feign/authorization/findAccessToken", produces = "application/json; charset=UTF-8")
    R findAccessToken(@RequestParam("token") String token, @RequestHeader(SecurityConstants.FROM) String from);

}
