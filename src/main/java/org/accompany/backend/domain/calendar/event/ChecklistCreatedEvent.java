package org.accompany.backend.domain.calendar.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChecklistCreatedEvent {

	private final Long deceasedProfileId;
}