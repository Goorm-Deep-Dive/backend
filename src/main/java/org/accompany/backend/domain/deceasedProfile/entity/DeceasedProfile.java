package org.accompany.backend.domain.deceasedProfile.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.user.entity.SurveyStatus;
import org.accompany.backend.domain.user.entity.User;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Entity
@Table(name = "deceased_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeceasedProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deceasedProfileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private LocalDate dateOfDeath;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurveyStatus surveyStatus;

    @Builder
    private DeceasedProfile(
            User user,
            String name,
            LocalDate dateOfDeath,
            SurveyStatus surveyStatus
    ) {
        this.user = user;
        this.name = name;
        this.dateOfDeath = dateOfDeath;
        this.surveyStatus = surveyStatus != null ? surveyStatus : SurveyStatus.NOT_STARTED;
    }

    public void updateDeceasedProfile(String name, LocalDate dateOfDeath) {
        this.name = name;
        this.dateOfDeath = dateOfDeath;
    }

    public void updateStatus(SurveyStatus surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
