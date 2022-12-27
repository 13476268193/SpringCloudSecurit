/**
 * @project：
 * @title：UserDetailsServiceImpl.java
 * @description：
 * @package com.springcloud.auth.modular.user.service.impl
 * @author：weiyang
 * @date：2021/8/31
 * @copyright: 武汉中天云通数据科技有限责任公司  All rights reserved.
 * @version V1.0
 */
package com.system.modular.user.feign;

import cn.hutool.core.util.StrUtil;
import com.base.core.annotation.Inner;
import com.base.core.constant.SecurityConstants;
import com.base.core.model.AuthUserDto;
import com.base.core.model.UserInfo;
import com.system.modular.user.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

import static com.base.core.constant.AuthConstant.GRANT_TYPE;
import static com.base.core.constant.AuthConstant.PHONE_NUMBER;
import static com.base.core.constant.enums.AuthEnum.SMS_CAPTCHA;
import static com.base.core.constant.enums.ResultCodeEnum.DISABLED_ACCOUNT;
import static com.base.core.constant.enums.ResultCodeEnum.INVALID_USERNAME;

@RestController
@RequestMapping(value = "feign/userDetails")
@RequiredArgsConstructor
public class UserDetailsFeign {

    public static final String USERDETAILS_CAHCE_PREFIX = "system-service:userDetails:";

    private final IUserService userService;

    private final RedisTemplate redisTemplate;

    @Inner
    @GetMapping(value = "loadUserByUsername", produces = "application/json; charset=UTF-8")
    public AuthUserDto loadUserByUsername(@RequestParam("userName") String userName) {
        return getAuthUserDto(userName);
    }

    /**
     * 获取请求对象
     *
     * @return HttpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取AuthUserDto
     * @param userName
     * @return
     */
    private AuthUserDto getAuthUserDto(final String userName) {
        AuthUserDto authUserDto = getFromCache(userName);
        if(authUserDto != null) return authUserDto;
        authUserDto = buildWithUserInfo(getUserInfo(userName));
        redisTemplate.opsForValue().set(USERDETAILS_CAHCE_PREFIX.concat(userName), authUserDto);
        return authUserDto;
    }

    /**
     * 根据用户信息生成AuthUserDto
     * @param userInfo
     * @return
     */
    private AuthUserDto buildWithUserInfo(UserInfo userInfo) {
        // 用户名或密码错误
        if (userInfo.getId() == null) {
            throw new UsernameNotFoundException(INVALID_USERNAME.msg);
        }

        // 判断账号是否被禁用
        String userStatus = userInfo.getStatus();
        if (!String.valueOf(1).equals(userStatus)) {
            throw new DisabledException(DISABLED_ACCOUNT.msg);
        }

        // 构造security用户【默认使用User封装认证用户，使用其它类型会导致授权码认证报错】【UserPrincipal】
        return new AuthUserDto(
                userInfo.getId(),
                userInfo.getAccount(),
                SecurityConstants.BCRYPT + userInfo.getPassword(),
                true,
                true,
                true,
                true,
                userInfo.getResourceKeyList());
    }

    /**
     * 根据授权类型查询用户信息
     * @param userName
     * @return
     */
    private UserInfo getUserInfo(final String userName) {
        HttpServletRequest request = getHttpServletRequest();
        // 获取登录类型，密码，二维码，验证码
        String authGrantType = request.getParameter(GRANT_TYPE);
        // 获取客户端id
        // String clientId = request.getParameter(AuthConstant.AUTH_CLIENT_ID);

        UserInfo userInfo = null;
        // 通过手机号码登录
        if (!StrUtil.isEmpty(authGrantType) && SMS_CAPTCHA.code.equals(authGrantType)) {
            String phone = request.getParameter(PHONE_NUMBER);
            userInfo = userService.queryUserInfo(UserInfo.builder().mobile(phone).build());
        } else {
            userInfo = userService.queryUserInfo(UserInfo.builder().account(userName).build());
        }
        return userInfo;
    }

    /**
     * 从redis缓存中获取AuthUserDto
     * @param userName
     * @return
     */
    private AuthUserDto getFromCache(final String userName) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(USERDETAILS_CAHCE_PREFIX.concat(userName)))
                .map(o -> (AuthUserDto)o).orElse(null);
    }


}

