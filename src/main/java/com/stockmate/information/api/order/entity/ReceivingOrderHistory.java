package com.stockmate.information.api.order.entity;

import com.stockmate.information.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "order_id")
    private Long orderId; // 주문 ID (출고의 경우 null)

    @Column(name = "order_number")
    private String orderNumber; // 주문 번호 (출고의 경우 null)

    @Column(name = "message", nullable = false)
    private String message; // 메시지

    @Column(name = "status", nullable = false)
    private String status; // 상태 (RECEIVED, RELEASED)

    @Column(name = "type", nullable = false)
    private String type; // 타입 (RECEIVING, RELEASE)

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReceivingHistoryItem> items = new ArrayList<>(); // 부품 아이템 목록
}
