package org.accompany.backend.domain.survey.repository;

import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.survey.entity.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    @Modifying
    @Query("delete from SurveyResponse sr where sr.deceasedProfile = :deceasedProfile")
    void deleteAllByDeceasedProfile(@Param("deceasedProfile") DeceasedProfile deceasedProfile);
    List<SurveyResponse> findAllByDeceasedProfile(DeceasedProfile deceasedProfile);
}
