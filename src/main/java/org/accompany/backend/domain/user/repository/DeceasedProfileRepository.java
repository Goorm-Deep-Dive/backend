package org.accompany.backend.domain.user.repository;

import org.accompany.backend.domain.user.entity.DeceasedProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeceasedProfileRepository extends JpaRepository<DeceasedProfile, Long> {
    Optional<DeceasedProfile> findByUserUserId(Long userId);
    boolean existsByUserUserId(Long userId);
    DeceasedProfile findByUser_UserId(Long userId);
}
