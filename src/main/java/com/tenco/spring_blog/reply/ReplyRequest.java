package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.user.User;
import lombok.Data;

// DTO
public class ReplyRequest {

    @Data
    public static class SaveDto {
        private Long boardId;
        private String comment;

        public void validate() {
            if (boardId == null) {
                throw new Exception400("게시글 정보가 없습니다");
            }
            if (comment == null || comment.trim().isEmpty()) {
                throw new Exception400("댓글 내용을 입력해 주세요");
            }
            if (comment.length() > 500) {
                throw new Exception400("댓글 최대 입력 글자 수는 500자 입니다");
            }
        }

        public Reply toEntity(Board board, User user) {
            return Reply.builder()
                    .comment(this.comment)
                    .board(board)
                    .user(user)
                    .build();
        }

    }

}
