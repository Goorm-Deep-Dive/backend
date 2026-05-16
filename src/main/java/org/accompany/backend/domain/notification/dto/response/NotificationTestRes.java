package org.accompany.backend.domain.notification.dto.response;

import java.util.List;

public record NotificationTestRes(
        int totalCount,
        int successCount,
        int failureCount,
        List<TestResult> results
) {
    public record TestResult(
            Long deceasedProfileId,
            String deceasedName,
            boolean sent,
            String messageId,
            String failureReason
    ) {
    }
}
