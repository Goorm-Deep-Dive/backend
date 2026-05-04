package org.accompany.backend.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.accompany.backend.domain.chat.dto.request.ChatReq;
import org.accompany.backend.domain.chat.dto.response.ChatRes;
import org.accompany.backend.domain.chat.service.ChatService;
import org.accompany.backend.global.code.SuccessCode;
import org.accompany.backend.global.response.ApiResponse;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Chat API", description = "AI 챗봇 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/messages")
    @Operation(summary = "AI 챗봇 메시지 전송", description = "로그인한 사용자의 메시지를 AI 챗봇 서비스로 전달합니다.")
    public ResponseEntity<ApiResponse<ChatRes>> sendMessage(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ChatReq request
    ) {
        return ApiResponse.success(
                SuccessCode.OK,
                chatService.sendMessage(principal.getUserId(), request)
        );
    }

    @PostMapping(value = "/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "AI 챗봇 메시지 전송(SSE)", description = "로그인한 사용자의 메시지를 AI 챗봇 서비스로 전달합니다.")
    public SseEmitter streamMessage(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @Valid @RequestBody ChatReq request
    ) {
        return chatService.streamMessage(principal.getUserId(), request);
    }
}
