package org.accompany.backend.domain.survey.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.accompany.backend.domain.BaseEntity;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;

@Entity
@Table(name = "survey_responses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SurveyResponse extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyResponseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deceased_profile_id", nullable = false)
    private DeceasedProfile deceasedProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_answer_id", nullable = false)
    private SurveyAnswer surveyAnswer;

    @Builder
    private SurveyResponse(
            DeceasedProfile deceasedProfile,
            SurveyAnswer surveyAnswer
    ) {
        this.deceasedProfile = deceasedProfile;
        this.surveyAnswer = surveyAnswer;
    }

    public void update(SurveyAnswer surveyAnswer) {
        this.surveyAnswer = surveyAnswer;
    }
}
