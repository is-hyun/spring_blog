package com.tenco.spring_blog._core.util;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.error.Exception500;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class FileUtil {

    // 1. 업로드 폴더 생성 : 사용자 홈 폴더 하위에 blog_uploads (운영체제와 관계없이 동작)
    public static final String UPLOAD_DIR =
            Paths.get(System.getProperty("user.home"), "blog_uploads").toString();

    // 2. 허용하는 이미지 형식과 저장할 확장자 설정
    private static final Map<String, String> IMAGE_TYPES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp"
    );

    // 3. 이미지 저장 : 저장할 파일 이름을 반환
    public static String saveImage(MultipartFile file) {
        // (1) 파일 저장은 선택 사항으로 결정 (프로필)
        if (file == null || file.isEmpty()) {
            return null;
        }
        // (2) 이미지 형식 검사
        if (!isImageFile(file)) {
            throw new Exception400("잘못된 이미지 형식입니다 (허용 형식 | JPG, PNG, GIF WEBP)");
        }

        // (3) 겹치지 않는 새 파일 이름 만들기 (UUID + 확장자)
        String extension = IMAGE_TYPES.get(file.getContentType());
        String savedFileName = UUID.randomUUID() + extension;

        // (4) 폴더가 없다면 생성 후 파일 저장
        Path uploadPath = Paths.get(UPLOAD_DIR);
        try {
            Files.createDirectories(uploadPath); // 이미 존재하면 생성 안 함
            file.transferTo(uploadPath.resolve(savedFileName)); // 폴더 경로 + 파일 이름 (파일 생성)
        } catch (Exception e) {
            log.error("파일 저장 실패 : {}", e.getMessage());
            throw new Exception500("파일 저장 중 오류가 발생했습니다" );
        }
        return savedFileName;
    }

    // 4. 파일 삭제 (파일 이름이 없거나 파일이 존재하지 않으면 삭제 안 함)
    public static void deleteFile(String filename) {
        if (filename == null || filename.isBlank()) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR, filename));
        } catch (IOException e) {
            // 파일 삭제에 실패해도 로그만 기록하고 사용자 요청은 계속 진행
            log.warn("파일 삭제 실패 : {}", e.getMessage());
        }
    }

    // 3.(2) 편의 메서드
    public static boolean isImageFile(MultipartFile file) {
        if (file == null || file.getContentType() == null) {
            return false; // 형식 정보가 없는 경우
        }
        return IMAGE_TYPES.containsKey(file.getContentType()); // 파일 형식 비교
    }
}
