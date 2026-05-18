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
    LOGOUT_SUCCESS(HttpStatus.OK, "AUTH_003", "로그아웃되었습니다."),

    //체크리스트
    USER_PROCEDURE_CHECKLIST_UPDATED(HttpStatus.OK, "CHECKLIST_SUCCESS_001", "사용자의 절차 체크리스트 상태가 변경되었습니다."),
    USER_DOCUMENT_CHECKLIST_UPDATED(HttpStatus.OK, "CHECKLIST_SUCCESS_002", "사용자의 문서 체크리스트 상태가 변경되었습니다."),
    USER_PROCEDURE_CHECKLIST_DELETED(HttpStatus.OK, "CHECKLIST_SUCCESS_003", "사용자의 가변 체크리스트 항목이 삭제되었습니다."),
    USER_PROCEDURE_CHECKLIST_CREATED(HttpStatus.CREATED, "CHECKLIST_SUCCESS_004", "사용자의 가변 체크리스트 항목이 추가되었습니다."),

    // 캘린더
    PENDING_TASK_CALENDAR_CREATED(HttpStatus.CREATED, "CALENDAR_SUCCESS_001", "처리 필요 과업 일정이 추가되었습니다."),
    PENDING_TASK_CALENDAR_UPDATED(HttpStatus.OK, "CALENDAR_SUCCESS_002", "처리 필요 과업 일정이 수정되었습니다."),
    PENDING_TASK_CALENDAR_DELETED(HttpStatus.OK, "CALENDAR_SUCCESS_003", "처리 필요 과업 일정이 삭제되었습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}