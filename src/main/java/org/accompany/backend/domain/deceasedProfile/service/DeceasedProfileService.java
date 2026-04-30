package org.accompany.backend.domain.deceasedProfile.service;

import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileCreateReq;
import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileUpdateReq;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileListRes;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileRes;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedSurveyStatusRes;

import java.util.List;

public interface DeceasedProfileService {
    DeceasedProfileRes createDeceasedProfile(Long userId, DeceasedProfileCreateReq request);
    List<DeceasedProfileListRes> getDeceasedProfiles(Long userId);
    void modifyDeceasedProfile(Long userId, Long deceasedProfileId, DeceasedProfileUpdateReq request);
    void changeActiveDeceasedProfile(Long userId, Long deceasedProfileId);
    DeceasedProfileRes getActiveDeceasedProfile(Long userId);
    DeceasedSurveyStatusRes getDeceasedSurveyStatus(Long userId);
}
