package com.tenco.spring_blog.purchase;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;

public class PurchaseResponse {

    // 단건 구매 내역에 딱 필요한 정보만 담은 DTO
    @Data
    public static class PurchaseListDto {
        private String boardTitle;   // 게시글 제목
        private String boardWriter;   // 작성자
        private int price;           // 구매 가격
        private String createdAt; // 구매일

        @Builder
        public PurchaseListDto(Purchase purchase) {
            this.boardTitle = purchase.getBoard().getTitle();
            this.boardWriter = purchase.getBoard().getUser().getUsername();
            this.price = purchase.getPrice();
            this.createdAt = purchase.getTime();
        }
    }
}
