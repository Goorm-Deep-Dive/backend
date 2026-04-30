package org.accompany.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_deceased_profile_id")
    private DeceasedProfile activeDeceasedProfile;

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
            Boolean isNotificationEnabled
    ) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.name = name;
        this.email = email;
        this.role = role != null ? role : Role.USER;
        this.isNotificationEnabled = isNotificationEnabled != null ? isNotificationEnabled : true;
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

    public void updateActiveDeceasedProfile(DeceasedProfile activeDeceasedProfile) {
        this.activeDeceasedProfile = activeDeceasedProfile;
    }

}