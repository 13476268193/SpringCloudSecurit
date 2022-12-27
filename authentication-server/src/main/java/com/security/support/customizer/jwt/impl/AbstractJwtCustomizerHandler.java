package com.security.support.customizer.jwt.impl;

import com.security.support.customizer.jwt.JwtCustomizerHandler;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

import java.util.*;

public abstract class AbstractJwtCustomizerHandler implements JwtCustomizerHandler {

	protected JwtCustomizerHandler jwtCustomizerHandler;


	/**
	 * token排除字段【第三方】
	 */
	protected final Set<String> THIRD_PARTY_TOKEN_EXCLUDE_CLAIMS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			IdTokenClaimNames.ISS, //
			IdTokenClaimNames.SUB, // 用户名
			IdTokenClaimNames.AUD,
			IdTokenClaimNames.EXP,
			IdTokenClaimNames.IAT,
			IdTokenClaimNames.AUTH_TIME,
			IdTokenClaimNames.NONCE,
			IdTokenClaimNames.ACR,
			IdTokenClaimNames.AMR,
			IdTokenClaimNames.AZP,
			IdTokenClaimNames.AT_HASH,
			IdTokenClaimNames.C_HASH
	)));

	/**
	 * token排除字段属性【access_token】
	 */
	protected final Set<String> ACCESS_TOKEN_EXCLUDE_CLAIMS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
			IdTokenClaimNames.ISS, //
			IdTokenClaimNames.SUB, // 用户名
			IdTokenClaimNames.AUD ,
			"nbf"//,
//			IdTokenClaimNames.EXP, //必要字段
//			IdTokenClaimNames.IAT,//必要字段
//			IdTokenClaimNames.AUTH_TIME,
//			IdTokenClaimNames.NONCE,
//			IdTokenClaimNames.ACR,
//			IdTokenClaimNames.AMR,
//			IdTokenClaimNames.AZP,
//			IdTokenClaimNames.AT_HASH,
//			IdTokenClaimNames.C_HASH
	)));


	/**
	 * 提取token字段
	 * @param authentication
	 * @return
	 */
	protected Map<String, Object> extractClaims(Authentication authentication) {
		Map<String, Object> claims;
		Object principalObj = authentication.getPrincipal();
		if (principalObj instanceof OidcUser) {
			OidcUser oidcUser = (OidcUser) principalObj;
			OidcIdToken idToken = oidcUser.getIdToken();
			claims = idToken.getClaims();
		} else if (principalObj instanceof OAuth2User) {
			OAuth2User oauth2User = (OAuth2User) principalObj;
			claims = oauth2User.getAttributes();
		} else {
			claims = Collections.emptyMap();
		}

		return new HashMap<>(claims);
	}

	public AbstractJwtCustomizerHandler(JwtCustomizerHandler jwtCustomizerHandler) {
		this.jwtCustomizerHandler = jwtCustomizerHandler;
	}

	protected abstract boolean supportCustomizeContext(Authentication authentication);
	protected abstract void customizeJwt(JwtEncodingContext jwtEncodingContext);

	@Override
	public void customize(JwtEncodingContext jwtEncodingContext) {

		boolean supportCustomizeContext = false;
		AbstractAuthenticationToken token = null;

    	Authentication authenticataion = SecurityContextHolder.getContext().getAuthentication();

    	if (authenticataion instanceof OAuth2ClientAuthenticationToken ) {
    		token = (OAuth2ClientAuthenticationToken) authenticataion;
    	}

    	if (token != null) {
    		if (token.isAuthenticated() && OAuth2TokenType.ACCESS_TOKEN.equals(jwtEncodingContext.getTokenType())) {
    			Authentication authentication = jwtEncodingContext.getPrincipal();
    			supportCustomizeContext = supportCustomizeContext(authentication);
    		}
    	}

    	if (supportCustomizeContext) {
    		customizeJwt(jwtEncodingContext);
    	} else {
    		jwtCustomizerHandler.customize(jwtEncodingContext);
    	}

	}

}
