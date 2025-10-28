package com.stockmate.information.api.order.entity;

import com.stockmate.information.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "receiving_history_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReceivingHistoryItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private ReceivingOrderHistory history;

    @Column(name = "part_id", nullable = false)
    private Long partId; // 부품 ID

    @Column(name = "quantity", nullable = false)
    private int quantity; // 입고/출고 수량
}

