package com.system.modular.user.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.core.annotation.Inner;
import com.base.core.model.UserInfo;
import com.base.core.page.model.PageResult;
import com.base.core.result.R;
import com.feign.client.MawbTrackingClient;
import com.feign.client.UserClient;
import com.securit.model.UserPrincipal;
import com.system.modular.system.model.SysResource;
import com.system.modular.system.service.IResourceService;
import com.system.modular.user.model.dto.UserInfoDto;
import com.system.modular.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;

/**
 * @ClassName: UserController
 * @Description: User前端控制器
 * @author GitEgg
 * @date 2019年5月18日 下午4:03:58
 */
@RestController
@RequestMapping(value = "/user")
//@RequiredArgsConstructor
@Api(value = "UserController|用户相关的前端控制器")
@RefreshScope
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IResourceService resourceService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private MawbTrackingClient mawbTrackingClient;

    /**
     * 查询所有用户
     */
    @Inner
    @GetMapping("/list")
    @ApiOperation(value = "查询用户列表")
    public PageResult<UserInfo> list(UserInfo user, @ApiIgnore Page<UserInfo> page) {
        Page<UserInfo> pageUser = userService.selectUserList(page, user);
        PageResult<UserInfo> pageResult = new PageResult<>(pageUser.getTotal(), pageUser.getRecords());
        return pageResult;
    }

    /**
     * 获取登录后的用户信息
     */
    @GetMapping("/userInfo")
    @ApiOperation(value = "登录后获取用户个人信息")
    public R<UserInfo> userInfo(@AuthenticationPrincipal UserPrincipal principal) {
        UserInfo user = new UserInfo();
        user.setId(Optional.ofNullable(principal).map(UserPrincipal::getId).get());
        user.setAccount(Optional.ofNullable(principal).map(UserPrincipal::getUsername).get());
        UserInfo userInfo = userService.queryUserInfo(user);
        UserInfoDto userInfoDto = new UserInfoDto(userInfo);
        // 查询用户菜单树，用于页面展示
        List<SysResource> menuTree = resourceService.queryMenuTreeByUserId(userInfo.getId());
        userInfoDto.setMenuTree(menuTree);
        return R.restResult(userInfoDto, 200, "");
    }

//    @Inner
    @RequestMapping(value = "/captcha", method = RequestMethod.POST)
    public R captcha(@RequestBody CaptchaVO captchaVO) {
        try{
            ResponseModel responseModel = captchaService.get(captchaVO);
            return R.restResult(responseModel, 200, "");
        } catch(Exception e) {
            e.printStackTrace();
        }
        return R.failed("获取验证码失败");
    }


//    @Inner
    @ApiOperation("滑动验证码验证")
    @PostMapping("/captcha/check")
    public R captchaCheck(@RequestBody CaptchaVO captchaVO) {
        ResponseModel responseModel = captchaService.check(captchaVO);
        return R.restResult(responseModel, 200, "");
    }

}
