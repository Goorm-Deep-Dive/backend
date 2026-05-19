package org.accompany.backend.global.interceptor;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.accompany.backend.global.security.principal.CustomUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";
    private static final String USER_ID = "userId";

    // Controller 전에 실행되는 Interceptor
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler ) {

        if (request.getDispatcherType() == DispatcherType.ASYNC) {
            return true;  // async 완료 dispatch는 로깅 스킵
        }

        // 요청 시작 시간 저장 (요청 전체 처리 시간 측정을 위한 기준값)
        request.setAttribute(START_TIME, System.currentTimeMillis());

        // Controller 요청이 아닌 정적 리소스 요청은 로그 대상에서 제외
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Long userId = extractUserId(request); // 사용자 정보 조회
        String handlerName = getHandlerName(handlerMethod); // 실행될 Controller + 메서드명 추출
        request.setAttribute(USER_ID, userId);

        // 요청 시작 로그 기록
        log.info("[{}] 요청 시작 - method={}, uri={}, userId={}",
                handlerName,
                request.getMethod(),
                request.getRequestURI(),
                userId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        if (request.getDispatcherType() == DispatcherType.ASYNC) {
            return;
        }

        // Controller 요청이 아닌 정적 리소스 요청은 로그 대상에서 제외
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        // 전체 처리 시간 계산
        long duration = 0L;
        Object startObj = request.getAttribute(START_TIME);
        if (startObj instanceof Long startTime) {
            duration = System.currentTimeMillis() - startTime;
        }

        Long userId = (Long) request.getAttribute(USER_ID);// 사용자 정보 조회
        String handlerName = getHandlerName(handlerMethod); // 실행될 Controller + 메서드명 추출

        if (duration > 1000 && !request.getRequestURI().contains("/chats/messages/stream")) {
            log.warn("[SLOW API] durationMs={}, method={}, uri={}, userId={}",
                    duration, request.getMethod(), request.getRequestURI(), userId);
        }

        if (ex == null) {
            // 정상적으로 요청이 처리된 경우
            log.info("[{}] 요청 완료 - method={}, uri={}, status={}, userId={}, durationMs={}",
                    handlerName,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    userId,
                    duration);
        } else {
            // Controller 처리 중 예외가 발생한 경우
            log.error("[{}] 요청 예외 - method={}, uri={}, status={}, userId={}, durationMs={}, error={}",
                    handlerName,
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    userId,
                    duration,
                    ex.getMessage(),
                    ex);
        }
    }

    private String getHandlerName(HandlerMethod handlerMethod) {
        return handlerMethod.getBeanType().getSimpleName() + "." + handlerMethod.getMethod().getName();
    }

    private Long extractUserId(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal customUser) {
            return customUser.getUserId();
        }

        return null;
    }
}
