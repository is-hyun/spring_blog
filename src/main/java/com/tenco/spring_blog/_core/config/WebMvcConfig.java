package com.tenco.spring_blog._core.config;

import com.tenco.spring_blog._core.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// IoC
@RequiredArgsConstructor
@Configuration // 스프링 설정 클래스임을 표시 (bin으로도 등록)
public class WebMvcConfig implements WebMvcConfigurer {

    private LoginInterceptor loginInterceptor;

    // 내가 정의한 인터셉터를 설정 클래스에 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LoginInterceptor를 시스템에 등록
        registry.addInterceptor(loginInterceptor)
                // 인터셉터가 동작할 URL 패턴 지정
                .addPathPatterns("user/**", "board/**")
                // 인터셉터에서 제외할 URL 패턴 지정
                .excludePathPatterns("/board/list", "/board/{id:\\d+}");
                // \\d+ - 1개 이상의 숫자를 의미하는 정규표현식
                // 예. /board/1, /board/123 (상세보기)은 로그인 없이도 접근 가능
                // /board/1/update 처럼 뒤에 경로가 더 붙으면 제외 대상이 아니게 된다.
    }

}
