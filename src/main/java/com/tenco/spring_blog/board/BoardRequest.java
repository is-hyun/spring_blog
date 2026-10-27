package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog.user.User;
import lombok.Data;

public class BoardRequest {

    @Data
    public static class SaveDto {
        private String title;
        private String content;
        private boolean premium; // 유료 게시글 여부 (추가, 체크 안 하면 false)

        public void validate() {
            if(title == null || title.trim().isEmpty()) {
                throw new Exception400("제목은 필수입니다");
            }
            if(content == null || content.trim().isEmpty()) {
                throw new Exception400("내용은 필수입니다");
            }
        }

        // DTO 에서 Entity로 변환하는 편의 메서드
        public Board toEntity(User user) {
            return Board.builder()
                    .title(this.title)
                    .content(this.content)
                    .user(user)
                    .premium(this.premium) // 추가
                    .build();
        }
    }

    @Data
    public static class UpdateDto {
        private String title;
        private String content;
        private boolean premium; // 유료 게시글 여부 (추가)

        public void validate() {
            if(title == null || title.trim().isEmpty()) {
                throw new Exception400("제목은 필수입니다");
            }
            if(content == null || content.trim().isEmpty()) {
                throw new Exception400("내용은 필수입니다");
            }
        }
    }
}