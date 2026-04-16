package org.accompany.backend.global.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

    // 공통
    OK(HttpStatus.OK, "COMMON_001", "요청에 성공했습니다."),
    CREATED(HttpStatus.CREATED, "COMMON_002", "생성되었습니다."),

    // 인증
    LOGIN_SUCCESS(HttpStatus.OK, "AUTH_001", "로그인에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.CREATED, "AUTH_002", "회원가입이 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH_003", "로그아웃되었습니다.")

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}