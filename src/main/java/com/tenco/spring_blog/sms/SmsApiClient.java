package com.tenco.spring_blog.sms;

import com.tenco.spring_blog._core.util.Define;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class SmsApiClient {

    @Value("${solapi.api-key}")
    private String apiKey;
    @Value("${solapi.api-secret}")
    private String apiSecret;
    @Value("${solapi.sender}")
    private String senderNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public boolean sendVerifiedSms(String to, String verifiedCode) {
        try {
            Message message = new Message();
            message.setFrom(senderNumber);
            message.setTo(to);
            message.setText("[스프링블로그] 인증번호 " + verifiedCode + "\n(" + Define.CODE_EXPIRE_MINUTES + "분 안에 입력)");

            this.messageService.sendOne(new SingleMessageSendingRequest(message));

            log.info("SOLAPI 외부 통신 성공");
            return true;

        } catch (Exception e) {
            log.error("SOLAPI 외부 통신 실패 - {}", e.getMessage());
            return false;
        }
    }
}
