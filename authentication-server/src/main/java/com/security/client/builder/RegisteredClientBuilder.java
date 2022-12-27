package com.security.client.builder;

import com.security.client.OAuth2TokenSettings;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.util.UUID;

@AllArgsConstructor
public class RegisteredClientBuilder {

    private PasswordEncoder passwordEncoder;

    private OAuth2TokenSettings oauth2TokenSettings;


    public RegisteredClient buildDefault() {
        // 注意这里启动第一次写入客户端信息后一定要注释掉，不然每次启动都会新生成一个重复的客户端，后续会报错。如果已经报错，检擦数据库是否有重复数据，删掉重复的
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret(passwordEncoder.encode("123456"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .redirectUri("http://192.168.10.152:8060/login/oauth2/code/messaging-client")
                .redirectUri("http://os.com:8080/login/oauth2/code/messaging-client")
                .redirectUri("http://os.com:8050/login/oauth2/code/messaging-client")
                .scope(OidcScopes.OPENID)
                .scope("message.read")
                .scope("message.write")
//                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())// 令牌有效期设置为 30 分钟
//                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
//                        .accessTokenTimeToLive(Duration.ofMinutes(120)).build())// 令牌格式指定为 自包含（JWT）, 有限期120分钟
                .tokenSettings(oauth2TokenSettings.getTokenSettings())
                .build();
        return registeredClient;
    }



}
