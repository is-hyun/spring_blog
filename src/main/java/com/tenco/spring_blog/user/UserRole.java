package com.tenco.spring_blog.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user_role_tb",
        uniqueConstraints = {@UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role"})})
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // enum을 숫자 대신 문자열(ADMIN, USER)로 사용
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder
    public UserRole(Role role) {
        this.role = role;
    }
}
