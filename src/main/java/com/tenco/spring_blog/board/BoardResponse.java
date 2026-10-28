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
        private List<ListDto> content; // 현재 페이지의 게시글 목록
        private int size;              // 페이지 크기
        private int currentPage;       // 현재 페이지 (1부터 시작)
        private int totalPages;        // 전체 페이지 수
        private long totalElements;     // 전체 게시글 수
        private boolean hasPrevious;   // 이전 페이지 있음 여부
        private boolean hasNext;       // 다음 페이지 있음 여부
        private List<PageLink> pagesLinks;    // 화면에 표시할 페이지 번호 목록
        // 코드 추가
        private int previousPage;
        private int nextPage;

        public PageDto(Page<Board> page) {
            this.content = page.getContent().stream()
                    .map(ListDto::new)
                    .toList();

            this.size = page.getSize();
            this.currentPage = page.getNumber() + 1;   // 0 부터 시작 + 1  (사용자에게는 1로 표시)
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.hasPrevious = page.hasPrevious();
            this.hasNext = page.hasNext();
            this.previousPage = currentPage - 1;
            this.nextPage = currentPage + 1;
            this.pagesLinks = makePageLinks(currentPage, totalPages);
        }

        // 편의 메서드 : 현재 페이지를 가운데 두고 앞뒤 2개씩, 최대 5개의 페이지 링크 생성
        private List<PageLink> makePageLinks(int currentPage, int totalPages) {
            List<PageLink> links = new ArrayList<>();

            int pageBlock = 5; // 화면에 항상 유지하고 싶은 최대 번호 개수
            int startPage;
            int endPage;

            // 1. 전체 페이지 수가 5개 이하인 경우는 연산 없이 전부 다 보여줌
            if (totalPages <= pageBlock) {
                startPage = 1;
                endPage = totalPages;
            }
            else {
                // 2. [역방향 4번 흔들림 방지 극약 처방]
                // 현재 페이지가 마지막 페이지 바로 앞 그룹(totalPages - 2)인 4페이지 이상 영역에 머무는 동안은
                // 역방향으로 5, 4번을 누르며 돌아오더라도 1~5로 성급하게 리셋되지 않도록
                // 무조건 끝 페이지를 기준으로 역산한 '마지막 블록(2 3 4 5 6)' 구성을 강제로 묶어둡니다.
                if (currentPage >= totalPages - (pageBlock - 3)) { // 6페이지 기준 4, 5, 6일 때 무조건 여기를 탐
                    endPage = totalPages;
                    startPage = totalPages - pageBlock + 1;
                }
                // 3. 반대로 처음 진입하거나 1, 2, 3번 같은 완전 앞쪽 동네를 누르고 있을 때는 1~5로 단단히 고정합니다.
                else if (currentPage <= pageBlock - 3) { // 6페이지 기준 1, 2, 3일 때
                    startPage = 1;
                    endPage = pageBlock;
                }
                // 4. 그 외 대규모 페이징 중앙 통과 영역용 처리
                else {
                    startPage = currentPage - 2;
                    endPage = currentPage + 2;
                }
            }

            // 계산된 정밀 슬라이딩 범위로 페이지 번호판 바인딩
            for (int i = startPage; i <= endPage; i++) {
                links.add(new PageLink(i, i == currentPage));
            }
            return links;
        }

    } // end of PageDto

    // 페이지 번호 버튼 하나의 정보
    @Data
    @AllArgsConstructor
    public static class PageLink {
        private int number; // 표시할 페이지 번호 (1부터)
        private boolean active; // 현재 페이지 여부 (강조 표시용)
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