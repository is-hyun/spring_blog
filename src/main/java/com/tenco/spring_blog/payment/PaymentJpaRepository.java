package com.tenco.spring_blog.payment;

import com.tenco.spring_blog.purchase.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    // id로 전체 조회
    @Query("""
                    SELECT p FROM Payment p
                    JOIN FETCH p.user
                    WHERE p.user.id = :userId
                    ORDER BY p.id DESC 
            """)
    List<Payment> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Payment p WHERE p.paymentId = :paymentId")
    Optional<Payment> findByPaymentId(@Param("paymentId") String paymentId);

    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
        FROM Payment p 
        WHERE p.paymentId = :paymentId 
    """)
    boolean existsByPaymentId(@Param("paymentId") String paymentId);

}