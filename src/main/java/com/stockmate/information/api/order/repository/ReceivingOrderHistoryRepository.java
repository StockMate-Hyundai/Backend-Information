package com.stockmate.information.api.order.repository;

import com.stockmate.information.api.order.entity.ReceivingOrderHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReceivingOrderHistoryRepository extends JpaRepository<ReceivingOrderHistory, Long> {
    
    // 가맹점별 입출고 히스토리 조회 (페이지네이션)
    @Query("SELECT rh FROM ReceivingOrderHistory rh WHERE rh.memberId = :memberId ORDER BY rh.createdAt DESC")
    Page<ReceivingOrderHistory> findByMemberIdOrderByCreatedAtDesc(@Param("memberId") Long memberId, Pageable pageable);
    
    // 모든 입출고 히스토리 조회 (관리자용 - 페이지네이션)
    @Query("SELECT rh FROM ReceivingOrderHistory rh ORDER BY rh.createdAt DESC")
    Page<ReceivingOrderHistory> findAllOrderByCreatedAtDesc(Pageable pageable);
    
    // 주문별 입출고 히스토리 조회
    @Query("SELECT rh FROM ReceivingOrderHistory rh WHERE rh.orderNumber = :orderNumber ORDER BY rh.createdAt DESC")
    Page<ReceivingOrderHistory> findByOrderNumberOrderByCreatedAtDesc(@Param("orderNumber") String orderNumber, Pageable pageable);
}
