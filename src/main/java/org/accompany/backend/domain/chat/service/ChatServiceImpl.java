package org.accompany.backend.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.chat.client.AiChatClient;
import org.accompany.backend.domain.chat.dto.external.*;
import org.accompany.backend.domain.chat.dto.request.ChatReq;
import org.accompany.backend.domain.chat.dto.response.ChatRes;
import org.accompany.backend.domain.chat.entity.ChatMessage;
import org.accompany.backend.domain.chat.entity.ChatRole;
import org.accompany.backend.domain.chat.repository.ChatMessageRepository;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiChatClient aiChatClient;
    private final UserProcedureChecklistRepository userProcedureChecklistRepository;

    @Override
    @Transactional
    public ChatRes sendMessage(Long userId, ChatReq request) {

        log.info("[Chat] 메시지 요청 시작 - userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 채팅 history 조회
        List<AiChatMessage> history = getRecentHistory(user.getUserId());

        AiChatReq aiRequest = new AiChatReq(
                user.getUserId(),
                request.message(),
                createContext(user),
                history
        );

        AiChatRes response = aiChatClient.sendMessage(aiRequest);

        if (response == null || response.message() == null) {
            throw new BusinessException(ErrorCode.AI_CHAT_SERVICE_UNAVAILABLE);
        }

        saveMessage(user, request.message(), ChatRole.USER);
        saveMessage(user, response.message(), ChatRole.AI);

        return new ChatRes(response.message());
    }

    private AiChatContext createContext(User user) {

        DeceasedProfile profile = user.getActiveDeceasedProfile();

        if (profile == null) {
            return new AiChatContext(user.getName(), null);
        }

        List<UserProcedureChecklist> checklists =
                userProcedureChecklistRepository
                        .findAllWithProcedureByDeceasedProfileId(profile.getDeceasedProfileId());

        AiChecklistSummary summary = createChecklistSummary(checklists);

        log.info("[Chat] 체크리스트 요약 생성 완료 - deceasedProfileId={}",
                profile.getDeceasedProfileId());

        return new AiChatContext(
                user.getName(),
                summary
        );
    }

    private AiChecklistSummary createChecklistSummary(List<UserProcedureChecklist> checklists) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime urgentLimit = now.plusDays(7);

        List<String> notCompleted = checklists.stream()
                .filter(checklist -> !checklist.isChecked())
                .sorted(Comparator
                        .comparing((UserProcedureChecklist c) -> c.getProcedure().getPriority()).reversed()
                        .thenComparing(c -> c.getDueDate() == null ? LocalDateTime.MAX : c.getDueDate()))
                .limit(3)
                .map(checklist -> checklist.getProcedure().getProcedureName())
                .toList();

        List<String> urgent = checklists.stream()
                .filter(checklist -> !checklist.isChecked())
                .filter(checklist -> checklist.getDueDate() != null)
                .filter(checklist -> !checklist.getDueDate().isBefore(now))
                .filter(checklist -> !checklist.getDueDate().isAfter(urgentLimit))
                .sorted(Comparator.comparing(UserProcedureChecklist::getDueDate))
                .limit(3)
                .map(checklist -> checklist.getProcedure().getProcedureName()
                        + "(" + checklist.getDueDate().toLocalDate() + ")")
                .toList();

        List<String> completed = checklists.stream()
                .filter(UserProcedureChecklist::isChecked)
                .sorted(Comparator.comparing(UserProcedureChecklist::getUpdatedAt).reversed())
                .limit(3)
                .map(checklist -> checklist.getProcedure().getProcedureName())
                .toList();

        return new AiChecklistSummary(notCompleted, urgent, completed);
    }

    private List<AiChatMessage> getRecentHistory(Long userId) {
        return chatMessageRepository
                .findTop6ByUser_UserIdOrderByCreatedAtDesc(userId)
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(message -> new AiChatMessage(
                        message.getRole().name(),
                        message.getContent()
                ))
                .toList();
    }

    private void saveMessage(User user, String content, ChatRole role) {

        chatMessageRepository.save(
                ChatMessage.builder()
                        .user(user)
                        .content(content)
                        .role(role)
                        .build()
        );

        log.info("[Chat] 메시지 저장 완료 - userId={}, role={}",
                user.getUserId(),
                role);
    }
}
