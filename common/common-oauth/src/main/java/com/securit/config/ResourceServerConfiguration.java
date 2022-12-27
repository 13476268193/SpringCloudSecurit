package com.securit.config;

import cn.hutool.core.util.ArrayUtil;
import com.securit.jwt.CustomJwtGrantedAuthoritiesConverter;
import com.securit.token.BearerTokenExtractor;
import com.securit.token.ResourceAuthExceptionEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

//@EnableGlobalMethodSecurity(
//	prePostEnabled = true,
//	order = 0
//)
@RequiredArgsConstructor
@EnableWebSecurity
public class ResourceServerConfiguration {


	private final OpaqueTokenIntrospector customOpaqueTokenIntrospector;
	protected final ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint;

	private final BearerTokenExtractor bearerTokenExtractor;

	private final PermitAllUrlProperties permitAllUrl;


    JwtAuthenticationConverter jwtAuthenticationConverter() {
    	CustomJwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new CustomJwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(authorizeRequests -> authorizeRequests
						.antMatchers(ArrayUtil.toArray(permitAllUrl.getUrls(), String.class)).permitAll().anyRequest()
						.authenticated())
			.oauth2ResourceServer(
					oauth2 -> oauth2
							// JWT【不推荐】、opaque只能选择一种
//							.jwt(jwt -> jwt.decoder(jwtDecoder()).jwtAuthenticationConverter(jwtAuthenticationConverter()))
							.opaqueToken(token -> token.introspector(customOpaqueTokenIntrospector))
							.authenticationEntryPoint(resourceAuthExceptionEntryPoint)
							.bearerTokenResolver(bearerTokenExtractor))
				.headers().frameOptions().disable().and().csrf().disable();

		return http.build();
	}

}
