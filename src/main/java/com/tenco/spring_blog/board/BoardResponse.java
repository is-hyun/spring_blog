package com.tenco.spring_blog.board;

import com.tenco.spring_blog._core.util.MyDateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 응답 DTO
 *
 * - 서비스(트랜잭션 안)에서 엔티티를 DTO로 변환해서 반환한다.
 * - 뷰는 엔티티가 아닌 DTO 만 사용하므로 OSIV 를 꺼도 지연 로딩 오류가 나지 않는다.
 * - 화면에 필요한 값만 담으므로 계층간에 결합도도 낮아진다.
 */
public class BoardResponse {

    // 게시글 목록 + 페이지 정보 응답 DTO
    @Data
    public static class PageDto {
        private List<ListDto> content;
        private int size;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private boolean hasPrevious;
        private boolean hasNext;
        private List<PageLink> pagesLinks;

        private int previousPage;
        private int nextPage;

        public PageDto(Page<Board> page) {
            this.content = page.getContent().stream()
                    .map(ListDto::new)
                    .toList();
            this.size = page.getSize();
            this.currentPage = page.getNumber() + 1; // +1 제거해서 주소랑 통일하기
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.hasPrevious = page.hasPrevious();
            this.hasNext = page.hasNext();
            this.previousPage = currentPage -1;
            this.nextPage = currentPage + 1;
            this.pagesLinks = makePageLinks(currentPage, totalPages);
        }

        // 편의 메서드
        private List<PageLink> makePageLinks(int currentPage, int totalPages) {
            List<PageLink> links = new ArrayList<>();

            int startPage = Math.max(1, currentPage - 2); // 1 미만으로 떨어지지 않도록

            int endPage = Math.min(totalPages, currentPage + 2); // 전체 페이지 초과 방지

            for (int i = startPage; i <= endPage; i++) {
                links.add(new PageLink(i, i == currentPage));
            }
            return links;
        }
    }

    // 페이지 번호 버튼 정보
    @Data
    @AllArgsConstructor
    public static class PageLink {
        private int number;
        private boolean active;
    }

    @Data
    public static class ListDto {
        private Long id;
        private String title;
        private String username;
        private String createdAt;

        public ListDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            // JOIN FETCH로 이미 가져온 user 사용할 예정(추가 쿼리 발생 없음)
            if (board.getUser() != null) {
                this.username = board.getUser().getUsername();
            }
            this.createdAt = board.getTime();
        }
    }

    // 게시글 상세 응답 DTO
    // 게시글 상세 응답 DTO
    // 게시글 상세 응답 DTO
    @Data
    public static class DetailDto {
        private Long id;
        private String title;
        private String content;
        private Long userId;
        private String username;
        private String createdAt;
        private boolean premium; // 유료 게시글 여부
        private int price; //  가격 (안내 문구용)
        private boolean owner; // 로그인한 사용자가 작성자인가
        private boolean purchased; // 로그인한 사용자가 구매했는가
        private boolean canRead; // 본문 열람 가능 여부 = 무료글 OR 작성자 OR 구매자 --> OK

        public DetailDto(Board board, boolean owner, boolean purchased) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            // 지연 로딩 오류 방지를 위한 방어적 코드 작성
            if (board.getUser() != null) {
                this.userId = board.getUser().getId();
                this.username = board.getUser().getUsername();
            }
            if (board.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.timestampFormat(board.getCreatedAt());
            }

            this.premium = board.isPremium();
            this.price = 500; // 추후 수정
            this.owner = owner;
            this.purchased = purchased;
            this.canRead = !premium || owner || purchased;
            if (!canRead) {
                this.content = null;
            }
        }
    }

    // 게시글 수정 화면 응답 DTO
    public static class UpdateFormDto {
        private Long id;
        private String title;
        private String content;
        private boolean premium;

        public UpdateFormDto(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.premium = board.isPremium();
        }
    }
}