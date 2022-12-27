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
package com.security.support.service;

import com.base.core.model.AuthUserDto;
import com.feign.client.UserDetailsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.base.core.constant.SecurityConstants.FROM_IN;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsClient remoteUserDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        AuthUserDto authUserDto = remoteUserDetailsService.loadUserByUsername(username, FROM_IN);
        // 查询用户拥有的资源
        String[] resources = {};
        if (!CollectionUtils.isEmpty(authUserDto.getAuthorities())) {
            resources = authUserDto.getAuthorities().stream().distinct().toArray(String[]::new);
        }
        return User.builder()
                .username(authUserDto.getUsername())
                .password(authUserDto.getPassword())
                .authorities(resources)
                .accountExpired(!authUserDto.isAccountNonExpired())
                .accountLocked(!authUserDto.isAccountNonLocked())
                .credentialsExpired(!authUserDto.isCredentialsNonExpired())
                .disabled(!authUserDto.isEnabled())
                .build();
    }


}

