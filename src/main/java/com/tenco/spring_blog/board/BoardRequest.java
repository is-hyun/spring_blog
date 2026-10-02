package com.tenco.spring_blog.board;

import lombok.Data;

public class BoardRequest {

    @Data
    public static class SaveDto {
        private String title;
        private String content;
        private String username;

    }

}
