package com.tenco.spring_blog.user;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserPersistRepository {

    @Autowired
    private final EntityManager em;

    // 회원 가입
    @Transactional
    public User save(User user) {
        // 비영속 상태의 User 객체를 영속성 컨텍스트에 저장
        em.persist(user);
        // 영속성 컨텍스트가 user 객체 관리 시작\
        // persist() 후 객체는 영속 상태가 되고, 트랜잭션 커밋 시점에 INSERT 쿼리 실행
        // 자동 생성된 ID와 생성시간이 user 객체에 설정
        return user;
    }

    // 사용자명 중복 체크용 조회 메서드
    public User findByUsername(String username) {
        // JPQL 사용 (em.find()는 PK 기반으로 조회) 우리가 필요한 건 username 기반으로 조회
        String jpql = """
                SELECT u FROM User u WHERE u.username = :username
                """;
        try {
            return em.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
