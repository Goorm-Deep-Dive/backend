package org.accompany.backend.domain.user.repository;

import org.accompany.backend.domain.user.entity.UserEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEventLogRepository extends JpaRepository<UserEventLog, Long> {
}
