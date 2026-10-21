package com.tenco.spring_blog.user;

import com.tenco.spring_blog._core.error.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Slf4j
@RequiredArgsConstructor
@Service
public class NaverLoginService {

    private final NaverApiClient naverApiClient;
    private final UserService userService;

    // 카카오와 동일하게 매개변수 없는 주소 요청 스타일 유지
    // (※ 고정된 state 문자열 또는 ApiClient 내부에서 처리하도록 연동)
    public String getAuthorizeUrl() {
        return naverApiClient.getAuthorizeUrl();
    }

    // ✨ 카카오와 완벽히 동일하게 인수(code)를 하나만 받는 login 메서드 스타일
    public SessionUserDto login(String code) {
        if (code == null) {
            throw new UnauthorizedException("네이버 로그인이 취소되었습니다");
        }

        try {
            // 1. 인가 코드로 액세스 토큰 발급 (네이버 서버 통신)
            // (※ ApiClient 내부 고정 state 값을 활용하거나 처리를 단순화)
            UserResponse.OauthToken oAuthToken = naverApiClient.requestAccessToken(code);

            // 2. 액세스 토큰으로 사용자 정보 조회 (네이버 서버)
            UserResponse.NaverUserInfo naverUserInfo = naverApiClient.requestProfile(oAuthToken.getAccessToken());

            // 3. 자동 회원가입 또는 로그인 (우리 DB, 트랜잭션은 UserService 에서)
            return userService.naverLogin(naverUserInfo);

        } catch (HttpClientErrorException e) {
            // 네이버 4xx 응답을 한 경우 (인가 코드 재사용, 키 오류 등)
            log.warn("네이버 API 오류 : {} ", e.getResponseBodyAsString());
            throw new UnauthorizedException("네이버 로그인에 실패했습니다. 다시 시도해 주세요");
        } catch (ResourceAccessException e) {
            // 네이버 서버에 연결하지 못한 경우 (네트워크 오류, 시간 초과)
            log.warn("네이버 서버에 연결 실패 : {}", e.getMessage());
            throw new UnauthorizedException("네이버 서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요");
        }
    }
}
