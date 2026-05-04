package org.accompany.backend.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.chat.client.AiChatClient;
import org.accompany.backend.domain.chat.dto.external.AiChatReq;
import org.accompany.backend.domain.chat.dto.external.AiChatRes;
import org.accompany.backend.domain.chat.dto.request.ChatReq;
import org.accompany.backend.domain.chat.dto.response.ChatRes;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatPersistenceService chatPersistenceService;
    private final AiChatClient aiChatClient;

    @Override
    public ChatRes sendMessage(Long userId, ChatReq request) {

        log.info("[Chat] 메시지 요청 시작 - userId={}", userId);

        // DB 조회
        AiChatReq aiRequest =
                chatPersistenceService.createAiRequest(userId, request.message());

        // AI 호출
        AiChatRes response =
                aiChatClient.sendMessage(aiRequest);

        if (response == null || response.message() == null) {
            throw new BusinessException(ErrorCode.AI_CHAT_SERVICE_UNAVAILABLE);
        }

        // DB 저장
        chatPersistenceService.saveMessages(
                userId,
                request.message(),
                response.message()
        );

        return new ChatRes(response.message());
    }

    @Override
    public SseEmitter streamMessage(Long userId, ChatReq request) {
        AiChatReq aiRequest = chatPersistenceService.createAiRequest(userId, request.message());
        SseEmitter emitter = new SseEmitter(60_000L);

        StringBuilder fullResponse = new StringBuilder();
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        log.info("[Chat:SSE] 시작 - requestId={}, userId={}", requestId, userId);

        Thread.ofVirtual().start(() -> {
            try {
                for (String token : aiChatClient.streamMessage(aiRequest).toIterable()) {
                    fullResponse.append(token);
                    emitter.send(SseEmitter.event().data(token));
                }

                if (fullResponse.isEmpty()) {
                    log.warn("[Chat:SSE] 저장 생략 - AI 응답 없음, userId={}", userId);
                    emitter.complete();
                    return;
                }

                chatPersistenceService.saveMessages(
                        userId,
                        request.message(),
                        fullResponse.toString()
                );

                log.info("[Chat:SSE] 완료 - userId={}, length={}", userId, fullResponse.length());
                emitter.complete();

            } catch (IOException e) {
                log.warn("[Chat:SSE] 클라이언트 연결 종료 - userId={}, length={}",
                        userId, fullResponse.length());
                emitter.complete();

            } catch (Exception e) {
                log.error("[Chat:SSE] 실패 - userId={}, length={}",
                        userId, fullResponse.length(), e);
                emitter.completeWithError(e);
            }
        });

        emitter.onTimeout(() -> {
            log.warn("[Chat:SSE] 타임아웃 - requestId={}, userId={}", requestId, userId);
            emitter.complete();
        });

        return emitter;
    }
}
