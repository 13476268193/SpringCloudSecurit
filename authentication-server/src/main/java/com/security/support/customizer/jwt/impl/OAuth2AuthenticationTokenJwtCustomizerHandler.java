package com.security.support.customizer.jwt.impl;

import com.security.support.customizer.jwt.JwtCustomizerHandler;
import com.security.support.token.OAuth2AuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

import java.util.*;

public class OAuth2AuthenticationTokenJwtCustomizerHandler extends AbstractJwtCustomizerHandler {

    public OAuth2AuthenticationTokenJwtCustomizerHandler(JwtCustomizerHandler jwtCustomizerHandler) {
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
        });

    }

    @Override
    protected boolean supportCustomizeContext(Authentication authentication) {
        return authentication != null && authentication instanceof OAuth2AuthenticationToken;
    }

}
