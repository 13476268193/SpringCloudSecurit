package com.feign.client;

import com.anji.captcha.model.vo.CaptchaVO;
import com.base.core.model.UserInfo;
import com.base.core.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import static com.base.core.constant.SecurityConstants.FROM;

@FeignClient(value = "system-service", contextId = "user-service")
public interface UserClient {

    @GetMapping("/feign/user/queryUserInfo")
    UserInfo queryUserInfo(@RequestParam("account")String account, @RequestHeader(FROM) String from);

    @PostMapping("/feign/user/captcha")
    R captcha(@RequestBody CaptchaVO captchaVO, @RequestHeader(FROM) String from);

}
