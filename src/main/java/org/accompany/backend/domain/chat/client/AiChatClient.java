package org.accompany.backend.domain.chat.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.chat.dto.external.AiChatReq;
import org.accompany.backend.domain.chat.dto.external.AiChatRes;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiChatClient {

    private final RestClient restClient;

    @Value("${chatbot.base-url}")
    private String aiChatBaseUrl;

    @Value("${chatbot.internal-api-key}")
    private String aiChatApiKey;

    public AiChatRes sendMessage(AiChatReq request) {

        log.info("[AI Chat] AI 서버 요청 시작 - userId={}", request.userId());

        try {
            AiChatRes response = restClient.post()
                    .uri(aiChatBaseUrl + "/api/v1/chat/messages")
                    .header("X-Internal-API-Key", aiChatApiKey)
                    .body(request)
                    .retrieve()
                    .body(AiChatRes.class);

            if (response == null || response.message() == null || response.message().isBlank()) {

                log.warn("[AI Chat] AI 응답 비어 있음 - userId={}",
                        request.userId()
                );

                throw new BusinessException(ErrorCode.AI_CHAT_SERVICE_UNAVAILABLE);
            }

            log.debug("[AI Chat] AI 응답 수신 완료 - userId={}, responseLength={}",
                    request.userId(),
                    response.message().length()
            );

            return response;

        } catch (RestClientException e) {

            log.error("[AI Chat] AI 서버 호출 실패 - userId={}, baseUrl={}",
                    request.userId(),
                    aiChatBaseUrl,
                    e
            );

            throw new BusinessException(ErrorCode.AI_CHAT_SERVICE_UNAVAILABLE);
        }
    }
}