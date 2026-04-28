package org.accompany.backend.domain.survey.repository;

import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    void deleteAllByDeceasedProfile(DeceasedProfile deceasedProfile);
}
