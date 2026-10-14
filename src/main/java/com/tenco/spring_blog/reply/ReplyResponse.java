package com.tenco.spring_blog.reply;

import com.tenco.spring_blog._core.util.MyDateUtil;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Objects;

public class ReplyResponse {

    @Data
    public static class ListDto {

        private Long id;
        private String comment;
        private String username;
        private String createdAt;
        private boolean isOwner;

        // 생성자
        public ListDto(Reply reply, Long sessionUserId) {
            this.id = reply.getId();
            this.comment = reply.getComment();
            this.username = reply.getUser().getUsername();
            if (reply.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(reply.getCreatedAt());
            }
            this.isOwner = reply.isOwner(sessionUserId);
        }
    }

}
