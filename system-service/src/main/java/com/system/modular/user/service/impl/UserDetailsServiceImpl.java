///**
// * @project：
// * @title：UserDetailsServiceImpl.java
// * @description：
// * @package com.springcloud.auth.modular.user.service.impl
// * @author：weiyang
// * @date：2021/8/31
// * @copyright: 武汉中天云通数据科技有限责任公司  All rights reserved.
// * @version V1.0
// */
//package com.system.modular.user.service.impl;
//
//import com.base.core.constant.AuthConstant;
//import com.base.core.constant.enums.AuthEnum;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import com.security.oauth.core.constant.AuthConstant;
//import com.security.oauth.core.constant.SecurityConstants;
//import com.security.oauth.core.constant.enums.AuthEnum;
//import com.security.oauth.core.constant.enums.ResultCodeEnum;
//import com.security.oauth.core.exception.BusinessException;
//import com.security.oauth.modular.system.model.Resource;
//import com.security.oauth.modular.system.service.IRoleResourceService;
//import com.security.oauth.modular.user.model.SysUser;
//import com.security.oauth.modular.user.model.UserInfo;
//import com.security.oauth.modular.user.model.UserPrincipal;
//import com.security.oauth.modular.user.service.UserService;
//import com.system.modular.user.model.SysUser;
//import com.system.modular.user.model.UserInfo;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.authentication.DisabledException;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.Collection;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor(onConstructor = @_(@Autowired))
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    private final RedisTemplate redisTemplate;
//
////    private final IUserFeign userFeign;
//
//    private final HttpServletRequest request;
//
////    private final UserMapper userMapper;
//    private final UserService userService;
//
//    private final IRoleResourceService roleResourceService;
//
////    @Value("${system.maxTryTimes}")
////    private int maxTryTimes;
//
//    // 不需要验证码登录的最大尝试次数
////    @Value("${system.maxNonCaptchaTimes}")
////    private int maxNonCaptchaTimes;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) {
//
//        // 获取登录类型，密码，二维码，验证码
//        String authGrantType = request.getParameter(AuthConstant.GRANT_TYPE);
//
//        // 获取客户端id
//        String clientId = request.getParameter(AuthConstant.AUTH_CLIENT_ID);
//
//        // 远程调用返回数据
////        Result<Object> result;
//        UserInfo userInfo = null;
//        SysUser sysUser = SysUser.builder().build();
//        // 通过手机号码登录
//        if (!StringUtils.isEmpty(authGrantType) && AuthEnum.SMS_CAPTCHA.code.equals(authGrantType)) {
//            String phone = request.getParameter(AuthConstant.PHONE_NUMBER);
////            userInfo = userMapper.queryUserInfo(user.setMobile(phone));
//            userInfo = userService.queryUserInfo(sysUser.setMobile(phone));
////            result = userFeign.queryUserByPhone(phone);
//        }
//        // 通过二维码登录登录
//        else if (!StringUtils.isEmpty(authGrantType) && AuthEnum.QR.code.equals(authGrantType)) {
////            result = userFeign.queryUserByAccount(username);
////            userInfo = userMapper.queryUserInfo(user.setMobile(username));
//            userInfo = userService.queryUserInfo(sysUser.setMobile(username));
//        } else {
////            result = userFeign.queryUserByAccount(username);
////            userInfo = userMapper.queryUserInfo(user.setAccount(username));
//            userInfo = userService.queryUserInfo(sysUser.setAccount(username));
//        }
//
//        // 判断返回信息
//        if (null != userInfo) {
////            BeanUtil.copyProperties(result.getData(), gitEggUser, false);
//            // 用户名或密码错误
//            if (userInfo == null || userInfo.getId() == null) {
//                throw new UsernameNotFoundException(ResultCodeEnum.INVALID_USERNAME.msg);
//            }
//
//            // 没有角色
//            if (CollectionUtils.isEmpty(userInfo.getRoleIdList())) {
//                throw new BusinessException(ResultCodeEnum.INVALID_ROLE.msg);
//            }
//
//            // 从Redis获取账号密码错误次数
//            /*Object lockTimes = redisTemplate.boundValueOps(AuthConstant.LOCK_ACCOUNT_PREFIX + userInfo.getId()).get();
//
//            // 判断账号密码输入错误几次，如果输入错误多次，则锁定账号
//            // 输入错误大于配置的次数，必须选择captcha或sms_captcha
//            if (null != lockTimes && (int) lockTimes > maxNonCaptchaTimes && (org.springframework.util.StringUtils.isEmpty(authGrantType) || (!org.springframework.util.StringUtils.isEmpty(authGrantType)
//                    && !AuthEnum.SMS_CAPTCHA.code.equals(authGrantType) && !AuthEnum.CAPTCHA.code.equals(authGrantType)))) {
//                throw new BusinessException(ResultCodeEnum.INVALID_PASSWORD_CAPTCHA.msg);
//            }
//
//            // 判断账号是否被锁定（账户过期，凭证过期等可在此处扩展）
//            if (null != lockTimes && (int) lockTimes >= maxTryTimes) {
//                throw new LockedException(ResultCodeEnum.PASSWORD_TRY_MAX_ERROR.msg);
//            }*/
//
//            // 判断账号是否被禁用
//            String userStatus = userInfo.getStatus();
//            if (!String.valueOf(1).equals(userStatus)) {
//                throw new DisabledException(ResultCodeEnum.DISABLED_ACCOUNT.msg);
//            }
//            // 查询用户拥有的资源
//            String[] resources = {};
//            if(!CollectionUtils.isEmpty(userInfo.getRoleIdList())) {
//                Set<String> resourceSet = userInfo.getRoleIdList().stream().map(Long::parseLong)
//                        .map(roleResourceService::queryResourceByRoleId)
//                        .flatMap(Collection::stream).map(Resource::getResourceKey).collect(Collectors.toSet());
//                resources = resourceSet.toArray(new String[resourceSet.size()]);
//            }
//
//            // 构造security用户【默认使用User封装认证用户，使用其它类型会导致授权码认证报错】
//            return new User(
////                    userInfo.getId(),
////                    userInfo.getOrganizationId(),
//                    userInfo.getAccount(),
//                    SecurityConstants.BCRYPT + userInfo.getPassword(),
////                    userInfo.getMobile(),
//                    true,
//                    true,
//                    true,
//                    true,
//                    AuthorityUtils.createAuthorityList(resources)
//            );
//        } else {
//            throw new UsernameNotFoundException("用户不存在");
//        }
//    }
//
//
//}
//
