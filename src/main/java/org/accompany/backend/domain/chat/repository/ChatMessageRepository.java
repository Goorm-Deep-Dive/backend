package org.accompany.backend.domain.chat.repository;

import org.accompany.backend.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT c FROM ChatMessage c WHERE c.user.userId = :userId ORDER BY c.createdAt DESC")
    List<ChatMessage> findTopNByUser_UserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
    List<ChatMessage> findByUser_UserIdAndCreatedAtBetweenOrderByCreatedAtAsc(Long userId, LocalDateTime start, LocalDateTime end);
}
