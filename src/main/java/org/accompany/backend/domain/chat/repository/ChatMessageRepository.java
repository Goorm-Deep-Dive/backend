package org.accompany.backend.domain.chat.repository;

import org.accompany.backend.domain.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop6ByUser_UserIdOrderByCreatedAtDesc(Long userId);
}
