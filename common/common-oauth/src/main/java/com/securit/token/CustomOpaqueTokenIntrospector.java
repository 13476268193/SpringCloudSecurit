package com.securit.token;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.base.core.constant.SecurityConstants;
import com.base.core.model.AuthUserDto;
import com.base.core.result.R;
import com.feign.client.AuthorizationClient;
import com.feign.client.UserDetailsClient;
import com.securit.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.base.core.constant.SecurityConstants.FROM_IN;

/**
 * @author lengleng
 * @date 2022/5/28
 * @description 携带token去授权服务端获取对应的有效用户信息
 */
@Slf4j
@RequiredArgsConstructor
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final AuthorizationClient authorizationService;

    private final UserDetailsClient userDetailsService;

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        // 调用feign接口获取授权server端OAuth2Authorization
        R r = Optional.of(authorizationService.findAccessToken(token, FROM_IN))
                .orElseThrow(() -> new InvalidBearerTokenException(token));
        OAuth2Authorization authorization = JSON.parseObject(JSON.toJSONString(r.getData()), OAuth2Authorization.class);
        // 客户端模式默认返回
        AuthorizationGrantType authorizationGrantType = JSON.parseObject(JSON.toJSONString(authorization.getAuthorizationGrantType()),
                AuthorizationGrantType.class);
        if (AuthorizationGrantType.CLIENT_CREDENTIALS.equals(authorizationGrantType)) {
            return new ClientCredentialsOAuth2AuthenticatedPrincipal(authorization.getAttributes(),
                    AuthorityUtils.NO_AUTHORITIES, authorization.getPrincipalName());
        }

        try {
            Object principalObj = Objects.requireNonNull(authorization).getAttributes().get(Principal.class.getName());
            UsernamePasswordAuthenticationToken passwordAuthenticationToken = JSON.parseObject(JSON.toJSONString(principalObj), UsernamePasswordAuthenticationToken.class);
            User principal = JSON.parseObject(JSON.toJSONString(passwordAuthenticationToken.getPrincipal()), User.class);
            // 调用feign接口获取用户信息
            AuthUserDto authUserDto = userDetailsService.loadUserByUsername(principal.getUsername(),  FROM_IN);

            return new UserPrincipal(authUserDto.getId(), null,
                    authUserDto.getUsername(), SecurityConstants.BCRYPT + authUserDto.getPassword(),
                    null,
                    true,
                    true,
                    true,
                    true,
                    AuthorityUtils.createAuthorityList(collectAuthorities(authUserDto, authorization))
            );
        } catch (UsernameNotFoundException notFoundException) {
            log.warn("用户不不存在 {}", notFoundException.getLocalizedMessage());
            throw notFoundException;
        } catch (Exception ex) {
            log.error("资源服务器 introspect Token error {}", ex.getLocalizedMessage());
            throw new OAuth2AuthenticationException("token验证失败");
        }
    }

    /**
     * 获取权限列表
     * @param authUserDto
     * @param authorization
     */
    private String[] collectAuthorities(AuthUserDto authUserDto, OAuth2Authorization authorization) {
        // 查询用户拥有的资源
        if (!CollectionUtils.isEmpty(authUserDto.getAuthorities())) {
            // 添加client 授权 scope
            Object scopes = Optional.ofNullable(authorization.getAttributes().get(OAuth2Authorization.AUTHORIZED_SCOPE_ATTRIBUTE_NAME))
                    .orElse(new JSONArray());
            List<String> scopeList = JSON.parseObject(JSON.toJSONString(scopes), List.class);
            if (!CollectionUtils.isEmpty(scopeList)) {
                authUserDto.getAuthorities().addAll(scopeList);
            }
            return authUserDto.getAuthorities().stream().distinct().toArray(String[]::new);
        }
        return new String[]{};
    }

}
