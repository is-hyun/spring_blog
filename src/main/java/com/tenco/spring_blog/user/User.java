package com.tenco.spring_blog.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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

    // 프로필 이미지 파일 이름
    private String profileImage;

    // 사용자 역할 목록
    @OneToMany
    @JoinColumn(name = "user_tb", nullable = false)
    private List<UserRole> roles = new ArrayList<>();

    @Builder // id 와 createdAt은 자동으로 채워지므로 빌더에서 제외
    public User(String username, String password, String email, String profileImage) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImage = profileImage;
    }

    // 회원 정보 수정용 메서드 (변경 감지용)
    public void update(String password) {
        this.password = password;
    }

    // 프로필 이미지 변경 메서드
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
    // 역할 추가 (회원 가입 시 기본 USER 부여, 관리자는 직접 지정 )
    public void addRole(Role role) {
        // 이미 가진 역할이면 추가하지 않음 (코드에서 1차 방어)
        if (hasRole(role)) {
            return;
        }
        this.roles.add(UserRole.builder().role(role).build());
    }

    // 해당 역할을 가지고 있는지 확인
    public boolean hasRole(Role role) {
        for (UserRole u : this.roles) {
            if (u.getRole() == role) {
                return true; // 찾으면 바로 true
            }
        }
        return false; // 끝까지 없으면 false
    }

    // 로인한 사용자의 관리자 여부 확인 메서드
    public boolean isAdmin() {
        return hasRole(Role.ADMIN); // 고정 값 : 관리자 여부인지만 확인 true , false 반환
    }
}
