package org.accompany.backend.domain.procedure.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentType {

    REQUIRED("필수"),
    OPTIONAL("선택"),
    CONDITIONAL("조건부");

    private final String label;
}
