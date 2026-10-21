package com.tenco.spring_blog.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

// 카카오 서버와 HTTP 통신만 담당하는 클래스
@Slf4j
@Component // IoC 싱글톤
public class KakaoApiClient {

    @Value("${oauth.kakao.client-id}")
    private String kakaoClientId;
    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;

    private static String AUTHORIZE_URL =
    private static String TOKEN_URL =
    private static String PROFILE_URL = "";
    private final RestTemplate restTemplate = new RestTemplate();

    // 1. 카카오 로그인 화면 주소 만들기
}
