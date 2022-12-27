package com.security.support.config;

import com.base.core.constant.SecurityConstants;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.security.support.customizer.CustomeOAuth2TokenCustomizer;
import com.security.support.DaoAuthenticationProvider;
import com.security.support.customizer.jwt.JwtCustomizer;
import com.security.support.customizer.jwt.JwtCustomizerHandler;
import com.security.support.customizer.jwt.impl.JwtCustomizerImpl;
import com.security.support.customizer.token.claims.OAuth2TokenClaimsCustomizer;
import com.security.support.customizer.token.claims.impl.OAuth2TokenClaimsCustomizerImpl;
import com.security.support.handler.AuthenticationFailureEventHandler;
import com.security.support.handler.AuthenticationSuccessEventHandler;
import com.security.support.password.OAuth2ResourceOwnerPasswordAuthenticationConverter;
import com.security.support.password.OAuth2ResourceOwnerPasswordAuthenticationProvider;
import com.security.client.OAuth2TokenSettings;
import com.security.client.builder.RegisteredClientBuilder;
import com.security.support.token.CustomeOAuth2AccessTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.oauth2.server.authorization.web.authentication.*;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {
    private static final String CUSTOM_CONSENT_PAGE_URI = "/oauth2/consent";
    @Autowired
    private KeyPair keyPair;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OAuth2TokenSettings oauth2TokenSettings;

//    @Autowired
//    private OAuth2AuthorizationService oauth2AuthorizationService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer<>();
        http.apply(authorizationServerConfigurer.tokenEndpoint((tokenEndpoint) -> {// 个性化认证授权端点
            tokenEndpoint.accessTokenRequestConverter(accessTokenRequestConverter()) // 注入自定义的授权认证Converter
                    .accessTokenResponseHandler(new AuthenticationSuccessEventHandler()) // 登录成功处理器
                    .errorResponseHandler(new AuthenticationFailureEventHandler())
            ;// 登录失败处理器
        }));
        authorizationServerConfigurer
                .authorizationEndpoint(authorizationEndpoint ->
                        authorizationEndpoint.consentPage(CUSTOM_CONSENT_PAGE_URI));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        DefaultSecurityFilterChain securityFilterChain = http.requestMatcher(endpointsMatcher)
                .authorizeRequests(
//                        authorizeRequests -> authorizeRequests
//                        .anyRequest().authenticated()
                ).anyRequest().authenticated()
                .and()
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer
//                        .authorizationService(oauth2AuthorizationService)// redis存储token的实现
                        .providerSettings(ProviderSettings.builder()
                                .issuer(SecurityConstants.PROJECT_LICENSE).build()))

                .and()
                .formLogin(withDefaults())
//                .apply(new FormIdentityLoginConfigurer()).and() // 自定义认证登录配置
                .build();
        // 注入自定义授权模式实现
        addCustomOAuth2GrantAuthenticationProvider(http);
        return securityFilterChain;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        // Save registered client in db as if in-jdbc
        RegisteredClient registeredClient = new RegisteredClientBuilder(passwordEncoder, oauth2TokenSettings).buildDefault();
        // Save registered client in db as if in-memory
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
//        registeredClientRepository.save(registeredClient);
        return registeredClientRepository;
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
        return service;
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    public RSAKey generateRsa() {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * request -> xToken 注入请求转换器
     *
     * @return DelegatingAuthenticationConverter
     */
    private AuthenticationConverter accessTokenRequestConverter() {
        return new DelegatingAuthenticationConverter(Arrays.asList(
                new OAuth2ResourceOwnerPasswordAuthenticationConverter(),
                new OAuth2RefreshTokenAuthenticationConverter(),
                new OAuth2ClientCredentialsAuthenticationConverter(),
                new OAuth2AuthorizationCodeAuthenticationConverter(),
                new OAuth2AuthorizationCodeRequestAuthenticationConverter()));
    }

    /**
     * 注入授权模式实现提供方
     * <p>
     * 1. 密码模式 </br>
     * 2. 短信登录 </br>
     */
    @SuppressWarnings("unchecked")
    private void addCustomOAuth2GrantAuthenticationProvider(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = http.getSharedObject(OAuth2TokenGenerator.class);

        OAuth2ResourceOwnerPasswordAuthenticationProvider passwordAuthenticationProvider =
                new OAuth2ResourceOwnerPasswordAuthenticationProvider(
                        authenticationManager, authorizationService
                        , tokenGenerator
//                , oAuth2TokenGenerator() // 自定义token
                );
        // 处理 UsernamePasswordAuthenticationToken
        http.authenticationProvider(new DaoAuthenticationProvider());
        // 处理 OAuth2ResourceOwnerPasswordAuthenticationToken
        http.authenticationProvider(passwordAuthenticationProvider);
    }

    /**
     * 定制token相关信息
     *
     * @return
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> buildJwtCustomizer() {
        JwtCustomizerHandler jwtCustomizerHandler = JwtCustomizerHandler.getJwtCustomizerHandler();
        JwtCustomizer jwtCustomizer = new JwtCustomizerImpl(jwtCustomizerHandler);
        OAuth2TokenCustomizer<JwtEncodingContext> customizer = (context) -> jwtCustomizer.customizeToken(context);
        return customizer;
    }

    //@Bean
    @Deprecated
    public OAuth2TokenCustomizer<OAuth2TokenClaimsContext> buildOAuth2TokenClaimsCustomizer() {
        OAuth2TokenClaimsCustomizer oauth2TokenClaimsCustomizer = new OAuth2TokenClaimsCustomizerImpl();
        OAuth2TokenCustomizer<OAuth2TokenClaimsContext> customizer = (context) -> {
            oauth2TokenClaimsCustomizer.customizeTokenClaims(context);
        };
        return customizer;
    }


    // @Bean
    @Deprecated
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://www.security.com:9500").build();
    }


    /**
     * 令牌生成规则实现【自定义】 </br>
     * client:username:uuid
     *
     * @return OAuth2TokenGenerator
     */
    //@Bean
    @Deprecated
    public OAuth2TokenGenerator oAuth2TokenGenerator() {
        CustomeOAuth2AccessTokenGenerator accessTokenGenerator = new CustomeOAuth2AccessTokenGenerator();
        // 注入Token 增加关联用户信息
        accessTokenGenerator.setAccessTokenCustomizer(new CustomeOAuth2TokenCustomizer());
        return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, new OAuth2RefreshTokenGenerator());
    }

}



