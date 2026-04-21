package org.accompany.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;

import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private LocalDateTime dateOfDeath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SurveyStatus surveyStatus;

    @Builder
    private DeceasedProfile(
            User user,
            LocalDateTime dateOfDeath,
            SurveyStatus surveyStatus
    ) {
        this.user = user;
        this.dateOfDeath = dateOfDeath;
        this.surveyStatus = surveyStatus != null ? surveyStatus : SurveyStatus.NOT_STARTED;
    }

    public void updateDateOfDeath(LocalDateTime dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public void updateStatus(SurveyStatus surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    public void assignUser(User user) {
        this.user = user;
    }
}
