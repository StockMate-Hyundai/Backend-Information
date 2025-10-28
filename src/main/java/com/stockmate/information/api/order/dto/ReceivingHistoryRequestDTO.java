package com.stockmate.information.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivingHistoryRequestDTO {
    private Long memberId; // 가맹점 ID
    private String orderNumber; // 주문 번호
    private String message; // 메시지
    private String status; // 상태 (예: "RECEIVED")
}

