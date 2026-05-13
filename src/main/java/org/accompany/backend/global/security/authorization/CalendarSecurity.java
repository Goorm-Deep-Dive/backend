package org.accompany.backend.global.security.authorization;

import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.deceasedProfile.repository.DeceasedProfileRepository;
import org.springframework.stereotype.Component;

@Component("calendarSecurity")
@RequiredArgsConstructor
public class CalendarSecurity {

	private final DeceasedProfileRepository deceasedProfileRepository;

	public boolean isOwner(
			Long userId,
			Long deceasedProfileId
	) {

		return deceasedProfileRepository
				.existsByDeceasedProfileIdAndUserUserId(
						deceasedProfileId,
						userId
				);
	}
}