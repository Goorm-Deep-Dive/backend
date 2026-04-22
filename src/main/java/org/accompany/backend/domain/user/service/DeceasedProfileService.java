package org.accompany.backend.domain.user.service;

import java.time.LocalDate;

public interface DeceasedProfileService {
    void createDeceasedProfile(Long userId, LocalDate dateOfDeath);
}
