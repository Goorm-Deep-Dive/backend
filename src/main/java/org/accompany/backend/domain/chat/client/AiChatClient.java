package org.accompany.backend.domain.chat.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.chat.dto.external.AiChatReq;
import org.accompany.backend.domain.chat.dto.external.AiChatRes;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiChatClient {

    private final RestClient restClient;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${chatbot.base-url}")
    private String aiChatBaseUrl;

    @Value("${chatbot.internal-api-key}")
    private String aiChatApiKey;


    public AiChatRes sendMessage(AiChatReq request) {

        log.info("[AI Chat] AI 서버 요청 시작 - userId={}", request.userId());

        try {
            AiChatRes response = restClient.post()
                    .uri(aiChatBaseUrl + "/api/v1/chats/messages")
                    .header("X-Internal-API-Key", aiChatApiKey)
                    .body(request)
                    .retrieve()
                    .body(AiChatRes.class);

            log.debug("[AI Chat] AI 응답 수신 완료 - userId={}, responseLength={}",
                    request.userId(),
                    response.message().length()
            );

            return response;

        } catch (RestClientResponseException e) {

            log.error("[AI Chat] 챗봇/AI 서버 에러 응답 - userId={}, status={}",
                    request.userId(), e.getStatusCode(), e);

            throw new BusinessException(ErrorCode.AI_CHAT_SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            log.error("[AI Chat] 챗봇 서버 연결 실패 - userId={}, baseUrl={}",
                    request.userId(), aiChatBaseUrl, e);
            throw new BusinessException(ErrorCode.AI_CHAT_SERVICE_UNAVAILABLE);
        }
    }

    public Flux<String> streamMessage(AiChatReq request) {
        return webClient.post()
                .uri(aiChatBaseUrl + "/api/v1/chats/messages/stream")
                .header("X-Internal-API-Key", aiChatApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .map(json -> {
                    try { return objectMapper.readValue(json, String.class); }
                    catch (Exception e) { return json; }
                }).onErrorMap(e -> new BusinessException(ErrorCode.AI_CHAT_SERVICE_UNAVAILABLE));
    }
}