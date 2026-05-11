package org.accompany.backend.domain.calendar.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum EventType {

	/**
	 * 사용자 절차 체크리스트 기반 일정
	 */
	CHECKLIST("체크리스트"),

	/**
	 * Google Calendar 에서 동기화된 일정
	 */
	GOOGLE("구글 캘린더"),

	/**
	 * 사용자가 직접 생성한 일정
	 */
	USER_CUSTOM("사용자 일정");

	private final String label;
}