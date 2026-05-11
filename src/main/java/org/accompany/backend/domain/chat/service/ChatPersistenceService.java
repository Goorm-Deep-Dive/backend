package org.accompany.backend.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.domain.chat.dto.external.AiChatContext;
import org.accompany.backend.domain.chat.dto.external.AiChatMessage;
import org.accompany.backend.domain.chat.dto.external.AiChatReq;
import org.accompany.backend.domain.chat.dto.external.AiChecklistSummary;
import org.accompany.backend.domain.chat.dto.response.ChatMessageRes;
import org.accompany.backend.domain.chat.entity.ChatMessage;
import org.accompany.backend.domain.chat.entity.ChatRole;
import org.accompany.backend.domain.chat.repository.ChatMessageRepository;
import org.accompany.backend.domain.checklist.entity.UserProcedureChecklist;
import org.accompany.backend.domain.checklist.repository.UserProcedureChecklistRepository;
import org.accompany.backend.domain.deceasedProfile.entity.DeceasedProfile;
import org.accompany.backend.domain.procedure.entity.DueDateType;
import org.accompany.backend.domain.user.entity.User;
import org.accompany.backend.domain.user.repository.UserRepository;
import org.accompany.backend.global.code.ErrorCode;
import org.accompany.backend.global.exception.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatPersistenceService {

    private static final int HISTORY_LIMIT = 6;

    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserProcedureChecklistRepository userProcedureChecklistRepository;

    public List<ChatMessageRes> getMessagesByDate(Long userId, LocalDateTime start, LocalDateTime end) {
        return chatMessageRepository
                .findByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtAsc(userId, start, end)
                .stream()
                .map(chatMessage ->
                        new ChatMessageRes(chatMessage.getRole(), chatMessage.getContent(), chatMessage.getCreatedAt()))
                .toList();
    }

    public AiChatReq createAiRequest(Long userId, String message) {

        User user = userRepository.findByIdWithActiveDeceasedProfile(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<AiChatMessage> history = getRecentHistory(user.getUserId());
        AiChatContext context = createContext(user);

        return new AiChatReq(
                user.getUserId(),
                message,
                context,
                history
        );
    }

    @Transactional(readOnly = false)
    public void saveMessages(Long userId, String userMsg, String aiMsg) {

        User user = userRepository.getReferenceById(userId);

        saveMessage(user, userMsg, ChatRole.USER);
        saveMessage(user, aiMsg, ChatRole.AI);
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

    // =========================
    // Context + History
    // =========================

    private AiChatContext createContext(User user) {

        DeceasedProfile profile = user.getActiveDeceasedProfile();

        if (profile == null) {
            return new AiChatContext(null, null);
        }

        List<UserProcedureChecklist> checklists =
                userProcedureChecklistRepository
                        .findAllWithProcedureByDeceasedProfileId(profile.getDeceasedProfileId());

        AiChecklistSummary summary = createChecklistSummary(checklists);

        log.info("[Chat] 체크리스트 요약 생성 완료 - deceasedProfileId={}",
                profile.getDeceasedProfileId());

        return new AiChatContext(profile.getDateOfDeath(), summary);
    }

    private List<AiChatMessage> getRecentHistory(Long userId) {
        return chatMessageRepository
                .findTopNByUser_UserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, HISTORY_LIMIT))
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(message -> new AiChatMessage(
                        message.getRole().name(),
                        message.getContent()
                ))
                .toList();
    }

    private AiChecklistSummary createChecklistSummary(List<UserProcedureChecklist> checklists) {

        List<AiChecklistSummary.DueItem> notCompletedWithDeadline = checklists.stream()
                .filter(c -> !c.isChecked())
                .filter(c -> !c.getProcedure().getDueDateType().equals(DueDateType.NONE) && !c.getProcedure().getDueDateType().equals(DueDateType.IMMEDIATE))
                .sorted(Comparator.comparing(UserProcedureChecklist::getDueDate))
                .map(c -> new AiChecklistSummary.DueItem(
                        c.getProcedure().getProcedureName(),
                        c.getDueDate().toLocalDate()
                ))
                .toList();

        List<String> notCompletedUrgent = checklists.stream()
                .filter(c -> !c.isChecked())
                .filter(c -> c.getProcedure().getDueDateType().equals(DueDateType.IMMEDIATE))
                .sorted(Comparator.comparing(c -> c.getProcedure().getPriority()))
                .map(c -> c.getProcedure().getProcedureName())
                .toList();

        List<String> notCompletedNoDueDate = checklists.stream()
                .filter(c -> !c.isChecked())
                .filter(c -> c.getProcedure().getDueDateType().equals(DueDateType.NONE))
                .sorted(Comparator.comparing(c -> c.getProcedure().getPriority()))
                .map(c -> c.getProcedure().getProcedureName())
                .toList();

        List<String> completed = checklists.stream()
                .filter(UserProcedureChecklist::isChecked)
                .sorted(Comparator.comparing(UserProcedureChecklist::getUpdatedAt).reversed())
                .limit(3)
                .map(c -> c.getProcedure().getProcedureName())
                .toList();

        return new AiChecklistSummary(notCompletedWithDeadline, notCompletedUrgent, notCompletedNoDueDate,completed);
    }
}