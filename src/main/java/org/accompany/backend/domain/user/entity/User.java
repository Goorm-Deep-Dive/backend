package org.accompany.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_users_provider_provider_user_id",
                        columnNames = {"provider", "provider_user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(length = 100)
    private String providerUserId;

    @Column(length = 50)
    private String name;

    @Column(length = 100)
    private String email;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(nullable = false)
    private boolean isNotificationEnabled;

    @Column(columnDefinition = "TEXT")
    private String providerAccessToken;

    @Column(columnDefinition = "TEXT")
    private String providerRefreshToken;

    @Column(length = 100)
    private String googleProviderUserId;

    @Column(columnDefinition = "TEXT")
    private String googleAccessToken;

    @Column(columnDefinition = "TEXT")
    private String googleRefreshToken;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeceasedProfile> deceasedProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @Builder
    private User(
            Provider provider,
            String providerUserId,
            String name,
            String email,
            Role role,
            UserStatus status,
            Boolean isNotificationEnabled,
            String providerAccessToken,
            String providerRefreshToken,
            String googleProviderUserId,
            String googleAccessToken,
            String googleRefreshToken,
            LocalDateTime deletedAt
    ) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.name = name;
        this.email = email;
        this.role = role != null ? role : Role.USER;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.isNotificationEnabled = isNotificationEnabled != null ? isNotificationEnabled : true;
        this.providerAccessToken = providerAccessToken;
        this.providerRefreshToken = providerRefreshToken;
        this.googleProviderUserId = googleProviderUserId;
        this.googleAccessToken = googleAccessToken;
        this.googleRefreshToken = googleRefreshToken;
        this.deletedAt = deletedAt;
    }

    public void updateProfile(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public void updateSocialToken(String providerAccessToken, String providerRefreshToken) {
        this.providerAccessToken = providerAccessToken;
        if (providerRefreshToken != null) {
            this.providerRefreshToken = providerRefreshToken;
        }
    }

    public void connectGoogleAccount(String googleProviderUserId) {
        this.googleProviderUserId = googleProviderUserId;
    }

    public void updateGoogleToken(String googleAccessToken, String googleRefreshToken) {
        this.googleAccessToken = googleAccessToken;
        if (googleRefreshToken != null) {
            this.googleRefreshToken = googleRefreshToken;
        }
    }

    public void updateNotification(boolean isNotificationEnabled) {
        this.isNotificationEnabled = isNotificationEnabled;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
        this.deletedAt = status == UserStatus.DELETED ? LocalDateTime.now() : null;
    }

    public void withdraw() {
        this.status = UserStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
        this.name = null;
        this.email = null;
        this.providerUserId = null;
        this.providerAccessToken = null;
        this.providerRefreshToken = null;
        this.googleProviderUserId = null;
        this.googleAccessToken = null;
        this.googleRefreshToken = null;
    }

    public void addDeceasedProfile(DeceasedProfile deceasedProfile) {
        this.deceasedProfiles.add(deceasedProfile);
        deceasedProfile.assignUser(this);
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        this.refreshTokens.add(refreshToken);
        refreshToken.assignUser(this);
    }

    public void removeRefreshToken(RefreshToken refreshToken) {
        this.refreshTokens.remove(refreshToken);
        refreshToken.removeUser();
    }
}