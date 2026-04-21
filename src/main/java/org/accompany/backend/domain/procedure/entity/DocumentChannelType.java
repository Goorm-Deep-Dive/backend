package org.accompany.backend.domain.procedure.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DocumentChannelType {

    ONLINE("온라인"),
    OFFLINE("오프라인"),
    BOTH("온라인/오프라인");

    private final String label;
}
