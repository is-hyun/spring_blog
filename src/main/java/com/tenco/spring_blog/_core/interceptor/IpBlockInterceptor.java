package com.tenco.spring_blog._core.interceptor;

import com.tenco.spring_blog._core.error.Exception403;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

// 특정 IP 차단 인터셉터 구현. 단, 여러 개 가능
@Slf4j
@Component
public class IpBlockInterceptor implements HandlerInterceptor {

    // 차단할 IP
    private final Set<String> blockedIp = Set.of(
            "192.168.5.20",
            "192.168.7.237",
            // 로컬 호스트
            "192.168.5.16",
            "localhost",
            "0:0:0:0:0:0:0:1",
            "127.0.0.1"
    );

    // IP 추출 메서드
    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isBlank() || ip.trim() == null || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userIp = getIp(request);
        log.info("현재 접속한 사용자의 IP 주소: {}", userIp);

        if (blockedIp.contains(userIp)) {
            log.warn("차단 목록에 포함된 IP 접근 확인 : {}", userIp);
            throw new Exception403("접근이 거부된 IP입니다");
        }

        return true;
    }
}
