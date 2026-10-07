package com.tenco.spring_blog.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor // 필수 (JPA 엔티티 생성 시 필수)
@AllArgsConstructor
@Table (name="user_tb")
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    @Setter
    private String password;
    // 같은 사람이 두 번 가입할 수 없도록 유니크 제약
    @Column(unique = true)
    private String email;

    @CreationTimestamp // now()
    private Timestamp createdAt;

    @Builder // id 와 createdAt은 자동으로 채워지므로 빌더에서 제외
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // 회원 정보 수정용 메서드 (변경 감지용)
    public void update(String password) {
        this.password = password;
    }
}
