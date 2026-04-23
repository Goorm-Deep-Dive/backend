package org.accompany.backend.domain.deceasedProfile.repository;

import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeceasedProfileRepository extends JpaRepository<DeceasedProfile, Long> {
    Optional<DeceasedProfile> findByUserUserId(Long userId);
    boolean existsByUserUserId(Long userId);
}
