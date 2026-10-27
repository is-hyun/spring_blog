package com.tenco.spring_blog.purchase;

import com.tenco.spring_blog.board.Board;
import com.tenco.spring_blog.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

// 구매 내역 엔티티 (User N : M Board 를 중간 엔티티로 구현)
// - Purchase -> User  (N:1) : 누가 구매했는가
// - Purchase -> Board (N:1) : 어떤 글을 구매했는가
// [복합 유니크 제약] 같은 사용자가 같은 게시글을 두 번 구매할 수 없다 (DB 최종 방어)
@Getter
@NoArgsConstructor
@Entity
@Table(name = "purchase_tb",
        uniqueConstraints = {@UniqueConstraint(name = "uk_user_board", columnNames = {"user_id", "board_id"})})
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    // 구매 당시 지불한 포인트 (나중에 가격이 바뀌어도 구매 기록은 그대로 남음)
    private int price;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Purchase(User user, Board board, int price) {
        this.user = user;
        this.board = board;
        this.price = price;
    }
}