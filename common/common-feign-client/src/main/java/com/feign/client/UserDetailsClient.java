package com.feign.client;


import com.base.core.model.AuthUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static com.base.core.constant.SecurityConstants.FROM;

@FeignClient(value = "system-service", contextId = "userDetailsService")
public interface UserDetailsClient {

    @GetMapping(value = "/feign/userDetails/loadUserByUsername", produces = "application/json; charset=UTF-8")
    AuthUserDto loadUserByUsername(@RequestParam("userName") String userName,
                                   @RequestHeader(FROM) String from);

}
