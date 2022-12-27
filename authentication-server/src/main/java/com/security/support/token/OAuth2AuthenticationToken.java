package com.security.support.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.Assert;

import java.util.Collection;

public class OAuth2AuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 570L;
    private final OAuth2User principal;
    private final String authorizedClientRegistrationId;

    public OAuth2AuthenticationToken(OAuth2User principal, Collection<? extends GrantedAuthority> authorities, String authorizedClientRegistrationId) {
        super(authorities);
        Assert.notNull(principal, "principal cannot be null");
        Assert.hasText(authorizedClientRegistrationId, "authorizedClientRegistrationId cannot be empty");
        this.principal = principal;
        this.authorizedClientRegistrationId = authorizedClientRegistrationId;
        this.setAuthenticated(true);
    }

    public OAuth2User getPrincipal() {
        return this.principal;
    }

    public Object getCredentials() {
        return "";
    }

    public String getAuthorizedClientRegistrationId() {
        return this.authorizedClientRegistrationId;
    }

}
