package org.accompany.backend.domain.procedure.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DueDateType {

    IMMEDIATE("즉시"),
    RELATIVE("사망일 기준"),
    DEATH_END_DAY("사망 말일 기준"),
    DEATH_MONTH("사망 월 내"),
    NONE("기한 없음");

    private final String label;
}