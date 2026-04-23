package org.accompany.backend.domain.deceasedProfile.service;

import org.accompany.backend.domain.deceasedProfile.dto.request.DeceasedProfileCreateReq;
import org.accompany.backend.domain.deceasedProfile.dto.response.DeceasedProfileRes;

public interface DeceasedProfileService {
    DeceasedProfileRes createDeceasedProfile(Long userId, DeceasedProfileCreateReq request);
}
