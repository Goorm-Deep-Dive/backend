package org.accompany.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserEventType {
    LOGIN_ATTEMPT("로그인 시도"),
    LOGIN_SUCCESS("로그인 성공"),
    LOGIN_FAILURE("로그인 실패"),
    LOGOUT("로그아웃"),
    SIGN_UP("회원가입"),
    WITHDRAWAL("회원탈퇴");

    private final String label;
}

