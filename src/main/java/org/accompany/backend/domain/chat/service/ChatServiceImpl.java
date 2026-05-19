package org.accompany.backend.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.chat.client.AiChatClient;
import org.accompany.backend.domain.chat.dto.external.AiChatReq;
import org.accompany.backend.domain.chat.dto.external.AiChatRes;
import org.accompany.backend.domain.chat.dto.request.ChatReq;
import org.accompany.backend.domain.chat.dto.response.ChatMessageRes;
import org.accompany.backend.domain.chat.dto.response.ChatRes;
import org.accompany.backend.global.exception.BusinessException;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final String FALLBACK_MESSAGE =
            "현재 AI 서비스를 일시적으로 이용할 수 없습니다. 잠시 후 다시 시도해주세요.";

    private final ChatPersistenceService chatPersistenceService;
    private final AiChatClient aiChatClient;

    @Override
    public ChatRes sendMessage(Long userId, ChatReq request) {

        log.info("[Chat] 메시지 요청 시작 - userId={}", userId);

        // DB 조회
        AiChatReq aiRequest = chatPersistenceService.createAiRequest(userId, request.message());

        try {
            // AI 호출
            AiChatRes response = aiChatClient.sendMessage(aiRequest);

            // DB 저장
            chatPersistenceService.saveMessages(
                    userId,
                    request.message(),
                    response.message()
            );
            return new ChatRes(response.message());

        } catch (BusinessException e) {
            log.warn("[Chat] AI 서비스 폴백 적용 - userId={}", userId);
            return new ChatRes(FALLBACK_MESSAGE);
        }
    }

    @Override
    public SseEmitter streamMessage(Long userId, ChatReq request) {
        AiChatReq aiRequest = chatPersistenceService.createAiRequest(userId, request.message());
        SseEmitter emitter = new SseEmitter(150_000L);

        StringBuilder fullResponse = new StringBuilder();

        log.info("[Chat:SSE] 시작 - userId={}", userId);
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        Thread.ofVirtual().start(() -> {
            try {
                if (mdcContext != null) MDC.setContextMap(mdcContext);

                for (String token : aiChatClient.streamMessage(aiRequest).toIterable()) {
                    fullResponse.append(token);
                    emitter.send(SseEmitter.event().name("message").data(token));
                }

                if (fullResponse.isEmpty()) {
                    log.warn("[Chat:SSE] AI 응답 없음, 폴백 적용 - userId={}", userId);
                    try {
                        emitter.send(SseEmitter.event().name("error").data(FALLBACK_MESSAGE));
                    } catch (IOException ex) {
                        log.warn("[Chat:SSE] 폴백 전송 중 클라이언트 연결 종료 - userId={}", userId);
                    }
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

            }  catch (BusinessException e) {
                log.warn("[Chat:SSE] AI 서비스 폴백 적용 - userId={}", userId);
                try {
                    if (fullResponse.isEmpty()) {
                        emitter.send(SseEmitter.event().name("error").data(FALLBACK_MESSAGE));
                    }
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }

            } catch (IOException e) {
                log.warn("[Chat:SSE] 클라이언트 연결 종료 - userId={}, length={}",
                        userId, fullResponse.length());
                emitter.complete();

            } catch (Exception e) {
                log.error("[Chat:SSE] 실패 - userId={}, length={}",
                        userId, fullResponse.length(), e);
                emitter.completeWithError(e);
            } finally {
                MDC.clear();
            }
        });

        emitter.onTimeout(() -> {
            log.warn("[Chat:SSE] 타임아웃 - userId={}", userId);
            try {
                emitter.complete();
            } catch (Exception e) {
                log.debug("[Chat:SSE] 타임아웃 처리 중 이미 완료된 emitter");
            }
        });

        return emitter;
    }

    @Override
    public List<ChatMessageRes> getMessages(Long userId, LocalDate date) {
        log.info("[Chat] 메시지 히스토리 조회 시작 - userId={}, date={}", userId, date);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return chatPersistenceService.getMessagesByDate(userId, start, end);
    }

}
