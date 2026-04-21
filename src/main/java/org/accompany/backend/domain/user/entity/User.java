package org.accompany.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider;

    @Column(length = 100)
    private String providerUserId;

    @Column(length = 50)
    private String name;

    @Column(length = 100)
    private String email;

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
            UserStatus status,
            Boolean isNotificationEnabled,
            String providerAccessToken,
            String providerRefreshToken,
            String googleProviderUserId,
            LocalDateTime deletedAt
    ) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.name = name;
        this.email = email;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.isNotificationEnabled = isNotificationEnabled != null ? isNotificationEnabled : true;
        this.providerAccessToken = providerAccessToken;
        this.providerRefreshToken = providerRefreshToken;
        this.googleProviderUserId = googleProviderUserId;
        this.deletedAt = deletedAt;
    }

    public void updateSocialToken(String providerAccessToken, String providerRefreshToken) {
        this.providerAccessToken = providerAccessToken;
        this.providerRefreshToken = providerRefreshToken;
    }

    public void connectGoogleAccount(String googleProviderUserId) {
        this.googleProviderUserId = googleProviderUserId;
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
        this.providerUserId = null;
        this.providerAccessToken = null;
        this.providerRefreshToken = null;
        this.googleProviderUserId = null;
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