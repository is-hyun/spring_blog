package com.tenco.spring_blog.board;

import com.tenco.spring_blog.util.MyDateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
// 엔티티 클래스 만들기 : 데이터베이스 테이블 한 개를 자바 클래스로 그린 설계도
@Table(name = "board_tb")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id // 이 필드가 기본키임을 나타냄
    // 기본키 값을 자동으로 생성 (IDENTITY 전략 - DB 기본 설정을 따름) AUTO_INCREMENT 기능 사용
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 별도 어노테이션이 없으면 필드명 = 컬럼명
    private String title;
    private String content;
    private String username;

    @CreationTimestamp  // now() 없이 자동으로 PC 시간을 DB로 전송
    private Timestamp createdAt;    // 스프링이 기본값이 스네이크 케이스로 자동 변환

    // 비즈니스 로직을 위한 생성자 설계
    // id 와 createdAt은 JPA 자동으로 설정하므로 매개변수에서 제외
    @Builder
    public Board(String title, String content, String username) {
        this.title = title;
        this.content = content;
        this.username = username;
    }

    // 시간 포맷 메서드를 추가
    public String getTime() {
        return MyDateUtil.timestampFormat(createdAt);
    }

}
