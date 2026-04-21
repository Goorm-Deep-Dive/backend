package org.accompany.backend.domain.procedure.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChannelType {

    ONLINE("온라인"),
    VISIT("방문"),
    POSTAL("우편"),
    PHONE("전화"),
    FAX("팩스");

    private final String label;
}
