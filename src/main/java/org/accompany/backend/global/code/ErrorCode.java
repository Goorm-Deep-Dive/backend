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
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "서버 오류")
	;
	private final HttpStatus status;
	private final String code;
	private final String message;
}