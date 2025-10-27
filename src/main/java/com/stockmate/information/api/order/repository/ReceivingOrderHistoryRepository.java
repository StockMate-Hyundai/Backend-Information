package com.stockmate.information.api.order.repository;

import com.stockmate.information.api.order.entity.ReceivingOrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReceivingOrderHistoryRepository extends JpaRepository<ReceivingOrderHistory, Long> {
    
    // 가맹점별 입고 히스토리 조회
    @Query("SELECT rh FROM ReceivingOrderHistory rh WHERE rh.memberId = :memberId ORDER BY rh.createdAt DESC")
    List<ReceivingOrderHistory> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId);
    
    // 주문별 입고 히스토리 조회
    @Query("SELECT rh FROM ReceivingOrderHistory rh WHERE rh.orderNumber = :orderNumber ORDER BY rh.createdAt DESC")
    List<ReceivingOrderHistory> findByOrderNumberOrderByCreatedAtDesc(@Param("orderNumber") String orderNumber);
}
