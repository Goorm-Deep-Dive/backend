package org.accompany.backend.domain.procedure.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DueDateUnit {

    YEAR("년"),
    MONTH("개월"),
    DAY("일");

    private final String label;
}
