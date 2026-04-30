package org.accompany.backend.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 공통
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_002", "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_003", "권한이 없습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_004", "리소스를 찾을 수 없습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "서버 오류"),

	// 사용자
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
	SOCIAL_UNLINK_FAILED(HttpStatus.BAD_REQUEST, "SOCIAL_400", "소셜 계정 연동 해제에 실패했습니다."),

	// 인증
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_001", "리프레시 토큰이 없습니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "유효하지 않은 리프레시 토큰입니다."),
	STORED_REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH_003", "저장된 리프레시 토큰이 없습니다."),
	EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_004", "만료된 리프레시 토큰입니다."),

	// 체크리스트
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CHECKLIST_001", "카테고리를 찾을 수 없습니다."),
	CHECKLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "CHECKLIST_002", "사용자의 체크리스트를 찾을 수 없습니다."),
	CHECKLIST_DELETE_FORBIDDEN(HttpStatus.BAD_REQUEST,"CHECKLIST_003", "선택 항목만 삭제할 수 있습니다."),
	CHECKLIST_ALREADY_EXISTS(HttpStatus.CONFLICT, "CHECKLIST_004","이미 추가된 체크리스트입니다."),
	CHECKLIST_CREATE_FORBIDDEN(HttpStatus.BAD_REQUEST,"CHECKLIST_005","선택 항목만 추가할 수 있습니다."),

	// 절차
	PROCEDURE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROCEDURE_001", "절차 정보를 찾을 수 없습니다."),

	// 고인 프로필
	DECEASED_PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND,"DECEASED_PROFILE_001","고인 정보를 찾을 수 없습니다."),
	PROFILE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "DECEASED_PROFILE_002", "해당 프로필에 접근할 수 없습니다."),

	// 설문조사
	SURVEY_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "SURVEY_001", "이미 완료된 설문조사입니다."),

	// AI 챗봇
	AI_CHAT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "CHAT_001", "AI 챗봇 서비스와 통신할 수 없습니다."),
	// 서류
	DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DOCUMENT_001", "해당 서류를 찾을 수 없습니다.")


	;
	private final HttpStatus status;
	private final String code;
	private final String message;
}
