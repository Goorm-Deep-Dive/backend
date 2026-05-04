package org.accompany.backend.domain.chat.service;

import org.accompany.backend.domain.chat.dto.request.ChatReq;
import org.accompany.backend.domain.chat.dto.response.ChatRes;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface ChatService {

    ChatRes sendMessage(Long userId, ChatReq request);
    SseEmitter streamMessage(Long userId, ChatReq request);
}
