package com.tenco.spring_blog.payment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.net.PortUnreachableException;


// 포트원 서버와 HTTP 통신만 담당하는 클래스 (포트원 설정값도 이곳에서만 관리)
@Getter
@Component
public class PortOneApiClient {

    @Value("${portone.store-id}")
    private String storeId;
    @Value("${portone.channel-key}")
    private String channelKey;
    @Value("${portone.api-secret}")
    private String apiSecret;

    private static final String PAYMENT_URL = "https://api.portone.io/payments/";
    private final RestTemplate restTemplate = new RestTemplate();

    // 포트원 서버에 결제 단건 조회
    // GET, 헤더 설정
    public void getPayment(String paymentId) {

        HttpHeaders headers = new HttpHeaders();
        //headers.set("Authorization", "PortOne " + apiSecret);
        headers.set(HttpHeaders.AUTHORIZATION, "PortOne " + apiSecret);

        // GET 요청이라 바디 없이 헤더만
        HttpEntity<Void> request = new HttpEntity<>(headers);
    }

}