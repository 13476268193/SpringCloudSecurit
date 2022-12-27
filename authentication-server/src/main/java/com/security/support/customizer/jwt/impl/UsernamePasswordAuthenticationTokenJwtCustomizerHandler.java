package com.security.support.customizer.jwt.impl;

import cn.hutool.json.JSONUtil;
import com.security.support.customizer.jwt.JwtCustomizerHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自定义密码模式token处理器【token字段】
 */
@Slf4j
public class UsernamePasswordAuthenticationTokenJwtCustomizerHandler extends AbstractJwtCustomizerHandler {


    public UsernamePasswordAuthenticationTokenJwtCustomizerHandler(JwtCustomizerHandler jwtCustomizerHandler) {
        super(jwtCustomizerHandler);
    }

    @Override
    protected void customizeJwt(JwtEncodingContext jwtEncodingContext) {
        Authentication authentication = jwtEncodingContext.getPrincipal();
        Map<String, Object> thirdPartyClaims = extractClaims(authentication);
        JwtClaimsSet.Builder jwtClaimSetBuilder = jwtEncodingContext.getClaims();

        jwtClaimSetBuilder.claims(existingClaims -> {
            // Remove conflicting claims set by this authorization server
            existingClaims.keySet().forEach(thirdPartyClaims::remove);
            // Remove standard id_token claims that could cause problems with clients
            THIRD_PARTY_TOKEN_EXCLUDE_CLAIMS.forEach(thirdPartyClaims::remove);
            // Add all other claims directly to id_token
            existingClaims.putAll(thirdPartyClaims);
            // Remove from claims【排除token字段】
            ACCESS_TOKEN_EXCLUDE_CLAIMS.forEach(existingClaims::remove);
        });

        log.info("claims:{}", JSONUtil.toJsonStr(jwtClaimSetBuilder));
    }

    @Override
    protected boolean supportCustomizeContext(Authentication authentication) {
        return authentication != null && authentication instanceof UsernamePasswordAuthenticationToken;
    }

    /**
     * 重置token字段
     * @param jwtEncodingContext
     */
    @Deprecated
    private void resetClaimForToken(JwtEncodingContext jwtEncodingContext) {
        Authentication authentication = jwtEncodingContext.getPrincipal();
        JwtClaimsSet.Builder jwtClaimSetBuilder = jwtEncodingContext.getClaims();
        //      token加入用户名字段
        User userPrincipal = (User) authentication.getPrincipal();
        Map<String, Object> userAttributes = new HashMap<>();
        userAttributes.put("userName", userPrincipal.getUsername());
        // token加入scope字段
        Set<String> authorities = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        Set<String> contextAuthorizedScopes = jwtEncodingContext.getAuthorizedScopes();
        if (CollectionUtils.isEmpty(contextAuthorizedScopes)) {
            jwtClaimSetBuilder.claim(OAuth2ParameterNames.SCOPE, authorities);
        }

        // 过滤并生成token字段
        jwtClaimSetBuilder.claims(claims -> claims.putAll(userAttributes));
    }


}
