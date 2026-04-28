package org.accompany.backend.domain.chat.service;

import org.accompany.backend.domain.chat.dto.request.ChatReq;
import org.accompany.backend.domain.chat.dto.response.ChatRes;

public interface ChatService {

    ChatRes sendMessage(Long userId, ChatReq request);
}
