package org.accompany.backend.global.security.principal;

import lombok.Getter;
import org.accompany.backend.domain.user.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * JWT 인증 완료 후 SecurityContext에 저장할
 * 사용자 식별 정보와 권한만 담는 Principal 클래스.
 */
@Getter
public class CustomUserPrincipal {

    private final Long userId;
    private final Role role;

    public CustomUserPrincipal(Long userId, Role role) {
        this.userId = userId;
        this.role = role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
