package com.tenco.spring_blog.board;

/*
 * 영속성 컨텍스트 활용한 Repository 클래스 만들기
 * Repository - 소프트웨어에서 데이터를 저장하고 관리하는 곳을 추상화한 개념
 */

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoardPersistRepository {

    private final EntityManager em;

    // 게시글 저장
    @Transactional
    public Board save(Board board) {
        // 1. 매개변수로 받은 board는 이 시점에서 비영속 상태
        //    - 아직 영속성 컨택스트에 관리되지 않은 상태를 의미
        //    - 데이터베이스와 연관 없는 순수 Java 객체인 상태

        em.persist(board);
        // 2. em.persist(board); 이수에 엔티티를 영속성 컨텍스트에 저장
        //    - board 객체가 영속 상태로 변경됨
        //    - 영속성 컨텍스트가 엔티티를 관리하기 시작
        //    - 아직 실제 INSERT 쿼리는 실행되지 않은 상태 (쓰기 지연)

        // 3. 트랜잭션 커밋 시점에 실제 INSERT 쿼리가 실행
        //    - 이때 영속성 컨텍스트의 변경 사항이 DB에 반영
        //    - board 객체의 id 필드에 자동 생성된 값이 할당 (AUTO_INCREMENT)
        return board;
        // 4. 영속 상태의 객체를 반환
        //    - 자동으로 생성된 id 값을 포함한 객체가 반환
    }

    // 엔티티의 영속 상태 4가지
    // 1. 비영속 상태 : 새로 생성된 객체, 영속성 컨텍스트와 무관
    // 2. 영속 상태 : 영속성 컨텍스트에서 관리되는 상태
    // 3. 준영속 상태 : 영속성 컨텍스트에서 분리된 상태
    // 4. 삭제 상태 : 삭제 예정 상태 (트랜잭션 커밋 시 DELETE 쿼리 실행)
    public void entitiyLifecycleEx() {
        // 1. 비영속
        Board board = new Board("제목", "내용", "작성자");

        // 2. 영속
        em.persist(board);

        // 3. 준영속 - 영속성 컨텍스트에서 분리
        em.detach(board);

        // 4. 삭제/삭제 예정
        em.remove(board);
    }
}
