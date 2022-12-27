package com.security.support.customizer.jwt.impl;

import com.security.support.customizer.jwt.JwtCustomizer;
import com.security.support.customizer.jwt.JwtCustomizerHandler;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;

public class JwtCustomizerImpl implements JwtCustomizer {

	private final JwtCustomizerHandler jwtCustomizerHandler;

	public JwtCustomizerImpl(JwtCustomizerHandler jwtCustomizerHandler) {
		this.jwtCustomizerHandler = jwtCustomizerHandler;
	}

	@Override
	public void customizeToken(JwtEncodingContext jwtEncodingContext) {
		jwtCustomizerHandler.customize(jwtEncodingContext);
	}

}
