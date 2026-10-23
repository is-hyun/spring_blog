package com.tenco.spring_blog.sms;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception500;
import com.tenco.spring_blog._core.util.Define;
import com.tenco.spring_blog._core.util.SmsUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmsService {

    private final SmsApiClient smsApiClient;

    // 내부 레코드
    private record SmsCode(String code, LocalDateTime createdAt, LocalDateTime expiresAt) implements Serializable {
        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }

        // 60초 이내 재전송 방지용 남은 시간 계산
        long getRemainingSecondsToRetry() {
            long secondsPassed = Duration.between(createdAt, LocalDateTime.now()).toSeconds();
            return 60 - secondsPassed;
        }
    }

    // 1. 인증번호 생성 및 문자 발송
    public void senVerificationCode(String phone, HttpSession session) {
        // 솔라피 조건에 맞게 구분문자 제거
        String targetPhone = phone.replaceAll("-", "");
        String key = "smsCode_" + targetPhone;

        // 60초 이내 재전송 방지
        SmsCode savedSmsCode = (SmsCode) session.getAttribute(key);
        if (savedSmsCode != null) {
            long remainSec = savedSmsCode.getRemainingSecondsToRetry();
            if (remainSec > 0) {
                throw new Exception400(remainSec + "초 후에 다시 시도해 주세요");
            }
        }

        // 인증번호
        String code = SmsUtil.generateCode();
        LocalDateTime now = LocalDateTime.now();

        try {
            boolean isSent = smsApiClient.sendVerifiedSms(targetPhone, code);
            if (!isSent) {
                throw new Exception500("SMS 발송 서버 오류");
            }
        } catch (Exception e) {
            throw new Exception500("문자 내용 작성 및 전송 중에 오류가 발생했습니다");
        }

        session.setAttribute(key, new SmsCode(code, now, now.plusMinutes(Define.CODE_EXPIRE_MINUTES)));
        log.info("인증번호 문자 발송 완료 - {}", targetPhone);
    }

    // 2. 인증번호 확인 및 검증
    public boolean verifyCode(String phone, String code, HttpSession session) {
        String targetPhone = phone.replaceAll("-", "");
        String key = "smsCode_" + targetPhone;
        SmsCode savedSmsCode = (SmsCode) session.getAttribute(key);

        if (savedSmsCode == null) {
            throw new Exception400("인증번호를 먼저 요청해 주세요");
        }
        if (savedSmsCode.isExpired()) {
            session.removeAttribute(key);
            throw new Exception400("인증번호가 만료되었습니다. 다시 요청해주세요");
        }
        if (!savedSmsCode.code().equals(code)) {
            throw new Exception400("인증번호가 일치하지 않습니다");
        }
        session.removeAttribute(key);
        session.setAttribute("verifiedPhone", targetPhone);
        log.info("휴대폰 인증 완료 - {}",targetPhone);
        return true;
    }
}
