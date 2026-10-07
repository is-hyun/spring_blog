package com.tenco.spring_blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserPersistRepository {

    @Autowired
    private final EntityManager em;

    @Transactional
    public User updateById(Long id, UserRequest.UpdateDto updateDto) {
        User userEntity = em.find(User.class, id);
        if (userEntity == null) {
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다");
        }
        userEntity.update(updateDto.getPassword());
        return userEntity;
    }

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

    // 회원 정보 조회 - 로그인 (사용자 이름, 비밀번호 확인)
    public User findByUsernameAndPassword(String username, String password) {
        try {
            // JPQL
            String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.password = :password ";
            Query query = em.createQuery(jpql, User.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return (User) query.getSingleResult();
        } catch (Exception e) {
            // 일치하는 사용자가 없거나 에러 발생 시 null 반환
            // 로그인 실패를 의미함
            return null;
        }
    }

    public User findById(Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new IllegalArgumentException("회원 정보를 찾을 수 없습니다");
        }
        return user;
    }
}
