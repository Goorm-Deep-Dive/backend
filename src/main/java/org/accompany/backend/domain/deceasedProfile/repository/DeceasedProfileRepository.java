package org.accompany.backend.domain.deceasedProfile.repository;

import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeceasedProfileRepository extends JpaRepository<DeceasedProfile, Long> {
	List<DeceasedProfile> findAllByUserUserIdOrderByDateOfDeathDescCreatedAtDesc(Long userId);
	Optional<DeceasedProfile> findByDeceasedProfileIdAndUserUserId(Long deceasedProfileId, Long userId);
	boolean existsByUserUserId(Long userId);


	boolean existsByDeceasedProfileIdAndUserUserId(Long deceasedProfileId, Long userId);
}
