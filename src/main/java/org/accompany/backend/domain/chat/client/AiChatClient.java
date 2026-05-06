package org.accompany.backend.domain.chat.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.chat.dto.external.AiChatReq;
import org.accompany.backend.domain.chat.dto.external.AiChatRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
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

    private static final String FALLBACK_MESSAGE =
            "현재 AI 서비스를 일시적으로 이용할 수 없습니다. 잠시 후 다시 시도해주세요.";

    public AiChatRes sendMessage(AiChatReq request) {

        log.info("[AI Chat] AI 서버 요청 시작 - userId={}", request.userId());

        try {
            AiChatRes response = restClient.post()
                    .uri(aiChatBaseUrl + "/api/v1/chats/messages")
                    .header("X-Internal-API-Key", aiChatApiKey)
                    .body(request)
                    .retrieve()
                    .body(AiChatRes.class);

            if (response == null || response.message() == null || response.message().isBlank()) {
                return new AiChatRes(FALLBACK_MESSAGE);
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

            return new AiChatRes(FALLBACK_MESSAGE);
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
                });
    }
}