package com.tenco.spring_blog.controller;

import com.tenco.spring_blog.user.User;
import com.tenco.spring_blog.user.UserPersistRepository;
import com.tenco.spring_blog.user.UserRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserPersistRepository userPersistRepository;

    @GetMapping("/user/update")
    public String updateForm(Model model, HttpSession session) {
        // 1. 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        User user = userPersistRepository.findById(sessionUser.getId());
        model.addAttribute("user", user);
        return "user/update-form";
    }

    @PostMapping("/user/update")
    public String update(UserRequest.UpdateDto updateDto, Model model, HttpSession session) {
        // 1. 인증 검사
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        try {
            // 2. 권한 검사
            User userEntity = userPersistRepository.findById(sessionUser.getId());
            if (userEntity == null) {
                throw new RuntimeException("존재하지 않는 회원 정보입니다");
            }

            // 3. 유효성 검사
            updateDto.validate();

            // 4. 세션 동기화
            User updateUser = userPersistRepository.updateById(sessionUser.getId(), updateDto);
            session.setAttribute("sessionUser", updateUser);

            // 5. 성공 후 메인 페이지 이동
            return "redirect:/";
        } catch (Exception e) {
            // 5. 예외 발생
            log.error("회원 정보 수정 실패 : {}", e.getMessage());
            model.addAttribute("user", userPersistRepository.findById(sessionUser.getId()));
            model.addAttribute("errorMessage", e.getMessage());
            return "user/update-form";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        // templates/   <-- 콘텐츠 루트 경로
        return "user/login-form";
    }

    // POST http://localhost:8080/login
    // 로그인 처리 (예외적으로 POTS 요청)
    @PostMapping("/login")
    public String login(UserRequest.LoginDto loginDto, HttpSession session, Model model, HttpServletResponse response) {
        log.info("=== 로그인 요청 ===");
        log.info("사용자 명 : {}", loginDto.getUsername());
        log.info("아이디 저장 체크 여부 : {}", loginDto.isRememberId());

        try {
            // 1. 입력 데이터 검증
            loginDto.validate();

            // 2. 사용자명과 비밀번호로 사용자 조회
            User sessionUser = userPersistRepository.findByUsernameAndPassword(loginDto.getUsername(),
                    loginDto.getPassword());

            // 3. 로그인 성공/실패 처리
            if (sessionUser == null) {
                // 로그인 실패 : 일치하는 사용자 없음
                throw new IllegalArgumentException("사용자명 또는 비밀번호가 올바르지 않습니다");
            }

            if (loginDto.isRememberId()) {
                // [체크박스 True] -> 쿠키 생성 및 저장
                // 요구 사항 1: 쿠키 이름은 'rememberUsername', 값은 로그인한 사용자명
                Cookie cookie = new Cookie("rememberUsername", sessionUser.getUsername());

                // 요구 사항 3: 쿠키 유효 시간은 7일 (7일 * 24시간 * 60분 * 60초)
                cookie.setMaxAge(7 * 24 * 60 * 60);

                // 요구 사항 4: 자바스크립트에서 읽을 수 없도록 보안 설정 (HttpOnly)
                cookie.setHttpOnly(true);

                // 애플리케이션 전체 경로에서 쿠키가 유효하도록 설정
                cookie.setPath("/");

                // 브라우저 응답 헤더에 쿠키 추가
                response.addCookie(cookie);
                log.info("아이디 저장 쿠키 생성 완료 (7일 유지)");
            } else {
                // [체크박스 False] -> 쿠키 삭제 (요구 사항 5 만족)
                // 기존에 저장된 쿠키가 있다면 해제 시 지워주어야 합니다.
                Cookie cookie = new Cookie("rememberUsername", null);
                cookie.setMaxAge(0); // 유효시간을 0으로 설정하여 즉시 만료 및 삭제 처리
                cookie.setPath("/");
                response.addCookie(cookie);
                log.info("아이디 저장 쿠키 삭제 처리 완료");
            }

            // 4. 로그인 성공 : 세션에 사용자 정보를 저장
            session.setAttribute("sessionUser", sessionUser);

            log.info("로그인한 사용자 : {} ", sessionUser.getUsername());

            // 5. 메인 페이지로 리다이렉트
            return "redirect:/";

        } catch (Exception e) {
            // 로그인 실패 시 에러 메세지와 함께 로그인 폼으로 돌려 보내기
            model.addAttribute("errorMessage", e.getMessage());
            return "user/login-form";
        }
    }

    // POST http://localhost:8080/join
    @PostMapping("/join")
    public String join(UserRequest.JoinDto joinDto) {
        log.info("=== 회원가입 요청 ====");
        log.info("사용자명 {}", joinDto.getUsername());
        log.info("패스워드 {}", joinDto.getPassword());
        log.info("이메일 {}", joinDto.getEmail());

        try {
            // 1. 유효성 검사
            joinDto.validate();

            // 2. 사용자명 중복 체크
            User existingUser = userPersistRepository.findByUsername(joinDto.getUsername());
            if (existingUser != null) {
                throw new IllegalArgumentException("이미 존재하는 사용자명입니다");
            }

            // 3. DTO 를 Entity 변환
            User user = joinDto.toEntity();

            // 4. DB 에 회원 정보 저장
            User userEntity = userPersistRepository.save(user);

            // 회원 가입 성공시 로그인 화면으로 이동
            return "redirect:/login";
        } catch (Exception e) {
            log.error("회원가입 실패 : {} ", e.getMessage());
            return "user/join-form";
        }

    }

    // GET - http://localhost:8080/join
    @GetMapping("/join")
    public String joinForm() {
        // templates/   <<-- 콘텐츠 루트 경로
        return "user/join-form";
    }

    // GET http://localhost:8080/logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("=== 로그아웃 요청 ===");
        // 세션 무효화 처리
        session.invalidate();
        log.info("로그아웃 완료");
        // templates/   <-- 콘텐츠 루트 경로
        return "redirect:/";
    }

//    // GET - http://localhost:8080/user/update
//    @GetMapping("/user/update")
//    public String updateForm(Model model) {
//
//        // 뼈대용 임시 데이터
//        model.addAttribute("user",
//                Map.of("username", "김민수", "email", "abc@naver.com"));
//        return "user/update-form";
//    }
//    // GET - http://localhost:8080/logout
//    @GetMapping("/logout")
//    public String logout() {
//        // templates/   <<-- 콘텐츠 루트 경로
//        return "redirect/";
//    }

//    // 캐시로 로그인 정보 저장
//    @GetMapping("/login")
//    public String loginForm(jakarta.servlet.http.HttpServletRequest request, Model model) {
//        log.info("=== 로그인 화면 요청 ===");
//
//        // 화면에 전달할 기본 아이디 값 (쿠키가 없으면 빈 문자열 유지)
//        String savedUsername = "";
//
//        // 1. 브라우저가 요청 헤더에 담아 보낸 쿠키들을 전부 가져옵니다.
//        Cookie[] cookies = request.getCookies();
//
//        // 2. 쿠키가 존재할 때만 내부 검색을 수행합니다 (요구 사항 6: 첫 방문 시 null 방어)
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                // 3. 미션 3에서 저장했던 쿠키 이름인 'rememberUsername'이 있는지 확인합니다.
//                if ("rememberUsername".equals(cookie.getName())) {
//                    savedUsername = cookie.getValue(); // 쿠키에 저장된 사용자명 꺼내기
//                    log.info("쿠키에서 복원된 사용자명 : {}", savedUsername);
//                    break; // 찾았으므로 반복문 종료
//                }
//            }
//        }
//        model.addAttribute("rememberUsername", savedUsername);
//        return "user/login-form";
//    }
}
