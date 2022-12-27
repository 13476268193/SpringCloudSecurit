package com.system.modular.user.feign;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.base.core.annotation.Inner;
import com.base.core.model.UserInfo;
import com.base.core.result.R;
import com.system.modular.user.service.IUserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequiredArgsConstructor
@RequestMapping("feign/user")
public class UserFeign {

    @Autowired
    private  IUserService userService;

    @Autowired
    private CaptchaService captchaService;

    @Inner
//    @PreAuthorize("@pms.hasPermission('message.read')")
    @GetMapping("/queryUserInfo")
    public UserInfo queryUserInfo(@RequestParam("account") String account) {
        return userService.queryUserInfo(UserInfo.builder().account(account).build());
    }

    @Inner
    @ApiOperation("生成滑动验证码")
    @PostMapping("/captcha")
    public R captcha(@RequestBody CaptchaVO captchaVO) {
        try{
            ResponseModel responseModel = captchaService.get(captchaVO);
            return R.ok(responseModel);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return R.ok("获取验证码失败");
    }


    @Inner
    @ApiOperation("滑动验证码验证")
    @PostMapping("/captcha/check")
    public R captchaCheck(@RequestBody CaptchaVO captchaVO) {
        ResponseModel responseModel = captchaService.check(captchaVO);
        return R.ok(responseModel);
    }

}
