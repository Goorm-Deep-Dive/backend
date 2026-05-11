package org.accompany.backend.domain.calendar.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Google Calendar 동기화 상태
 */
@Getter
@RequiredArgsConstructor
public enum SyncStatus {

	SYNCED("동기화 완료"),
	PENDING("동기화 대기"),
	FAILED("동기화 실패");

	private final String label;
}