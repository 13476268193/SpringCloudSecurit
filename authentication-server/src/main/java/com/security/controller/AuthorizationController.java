package com.security.controller;

import com.alibaba.fastjson.JSON;
import com.base.core.annotation.Inner;
import com.base.core.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feign/authorization")
public class AuthorizationController {

    public static final String AUTHORIZATION_CAHCE_PREFIX = "authentication-server:security:authorization:";

    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    private final RedisTemplate<String, Object> redisTemplate;

    @Inner
    @GetMapping(value = "findAccessToken")
    R findAccessToken(@RequestParam("token") String token) {
        try {
//            return R.ok(Optional.ofNullable(getFromCache(token))
//                    .orElse(getAuthorization(token)));
            return R.ok(getAuthorization(token));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidBearerTokenException("get token failed");
        }
    }

    /**
     * 根据获取OAuth2Authorization
     * @param token
     * @return
     */
    private OAuth2Authorization getAuthorization(final String token) {
        OAuth2Authorization authorization = oAuth2AuthorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);
        final String cacheKey = AUTHORIZATION_CAHCE_PREFIX.concat(token);
        redisTemplate.opsForValue().set(cacheKey, JSON.toJSON(authorization));
        return authorization;
    }

    /**
     * 从redis缓存中获取OAuth2Authorization
     * @param token
     * @return
     */
    private OAuth2Authorization getFromCache(final String token) {
        final String cacheKey = AUTHORIZATION_CAHCE_PREFIX.concat(token);
        return Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey))
                .map(o -> JSON.parseObject(o.toString(), OAuth2Authorization.class))
                .orElse(null);
    }

}
