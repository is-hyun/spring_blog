package com.tenco.spring_blog.board;


import com.tenco.spring_blog._core.util.MyDateUtil;
import com.tenco.spring_blog.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Table(name = "board_tb")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    // 유료 게시글 가격 (추가) - 구매 처리와 화면 안내에서 같은 값을 사용
    public static final int PREMIUM_PRICE = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // board_tb 에 만들어질 외래키 컬럼 이름 설정
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    // 유료 게시글 여부 (추가)
    // - boolean 기본값 false, data.sql 처럼 SQL 로 INSERT 할 때를 위해 DB 기본값도 false
    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean premium;

    // id 와 createdAt은 JPA 자동으로 설정하므로 매개변수에서 제외
    @Builder
    public Board(String title, String content, User user, boolean premium) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.premium = premium; // 추가 (빌더에서 생략하면 false)
    }

    // 자신에 상태값을 변경하는 메서드 (영속성 엔티티를 수정하는 메서드)
    public void update(BoardRequest.UpdateDto updateDto) {
        updateDto.validate();
        this.title = updateDto.getTitle();
        this.content = updateDto.getContent();
        this.premium = updateDto.isPremium(); // 추가 (유료 여부도 함께 수정)
    }

    // 게시글 수정/삭제 권한 체크용 편의 메서드
    public boolean isOwner(Long userId) {
        return this.user.getId().equals(userId);
    }

    // 시간을 포맷을 메서드를 추가
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

}