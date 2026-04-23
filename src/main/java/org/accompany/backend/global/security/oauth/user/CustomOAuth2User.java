package org.accompany.backend.global.security.oauth.user;

import lombok.Getter;
import org.accompany.backend.domain.user.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final Long userId;
    private final String email;
    private final Role role;
    private final String providerUserId;
    private final boolean newUser;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(Long userId,
                            String email,
                            Role role,
                            String providerUserId,
                            boolean newUser,
                            Map<String, Object> attributes) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.providerUserId = providerUserId;
        this.newUser = newUser;
        this.attributes = attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
}
