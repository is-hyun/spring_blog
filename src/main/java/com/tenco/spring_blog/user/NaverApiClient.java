package com.tenco.spring_blog.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

// 네이버 서버와 HTTP 통신만 담당하는 클래스
@Slf4j
@Component // IoC (싱글톤)
public class NaverApiClient {

    // application.yml 의 변수를 가지고 올 때 사용
    @Value("${oauth.naver.client-id}")
    private String naverClientId;
    @Value("${oauth.naver.redirect-uri}")
    private String naverRedirectUri;
    @Value("${oauth.naver.client-secret}")
    private String naverClientSecret;

    private static final String AUTHORIZE_URL = "https://nid.naver.com/oauth2.0/authorize";
    private static final String TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
    private static final String PROFILE_URL = "https://openapi.naver.com/v1/nid/me";

    // 💡 카카오(매개변수 없음)와 구조를 맞추기 위해 세션 대신 고정 검증용 state 상수를 사용합니다.
    private static final String STATE_STRING = "naver_blog_login_secret_state";
    // 한 번 만들면 여러 요청에서 함께 써도 안전하게 설계 되어 하나만 만들어 재사용 한다.
    private final RestTemplate restTemplate = new RestTemplate();

    // 1. 네이버 로그인 화면 주소 만들기 (카카오와 매개변수/구조 완벽 일치)
    public String getAuthorizeUrl() {
        return AUTHORIZE_URL
                + "?client_id=" + naverClientId
                + "&redirect_uri=" + naverRedirectUri
                + "&response_type=code"
                + "&state=" + STATE_STRING;
    }

    // 2. 인가 코드로 액세스 토큰 요청 (카카오처럼 code 하나만 받도록 매개변수 구조 통일)
    public UserResponse.OauthToken requestAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("redirect_uri", naverRedirectUri); // 카카오와 대칭 배치
        params.add("code", code);
        // 네이버 로그인 오픈 API 필수 파라미터 포함
        params.add("state", STATE_STRING);
        // 클라이언트 시크릿 활성화시 반드시 포함
        params.add("client_secret", naverClientSecret);

        // 바디 + 헤더
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity(params, headers);

        // 주소, method, HTTP 메세지, 응답 타입
        ResponseEntity<UserResponse.OauthToken> response = restTemplate.exchange(
                TOKEN_URL,
                HttpMethod.POST,
                tokenRequest,
                UserResponse.OauthToken.class
        );
        log.info("네이버 액세스 토큰 발급 완료"); // 토큰 값은 로그에 남기지 않는다.
        return  response.getBody();
    }

    // 3. 액세스 토큰으로 사용자 정보 요청 (GET, 인증 헤더 - 카카오와 완벽 일치)
    public UserResponse.NaverUserInfo requestProfile(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<UserResponse.NaverUserInfo> response = restTemplate.exchange(
                PROFILE_URL,
                HttpMethod.GET,
                request,
                UserResponse.NaverUserInfo.class
        );
        return response.getBody();
    }
}
