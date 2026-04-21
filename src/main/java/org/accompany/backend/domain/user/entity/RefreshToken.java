package org.accompany.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private RefreshToken(
            User user,
            String refreshToken,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public void updateToken(String refreshToken, LocalDateTime expiresAt) {
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public void assignUser(User user) {
        this.user = user;
    }

    public void removeUser() {
        this.user = null;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}