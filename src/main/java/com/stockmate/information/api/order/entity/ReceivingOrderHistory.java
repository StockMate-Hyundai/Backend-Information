package com.stockmate.information.api.order.entity;

import com.stockmate.information.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "receiving_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReceivingOrderHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId; // 가맹점 ID

    @Column(name = "order_number", nullable = false)
    private String orderNumber; // 주문 번호

    @Column(name = "message", nullable = false)
    private String message; // 메시지

    @Column(name = "status", nullable = false)
    private String status; // 상태
}
