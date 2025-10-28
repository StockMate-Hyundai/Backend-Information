package com.stockmate.information.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivingHistoryRequestDTO {
    private Long memberId; // 가맹점 ID
    private String orderNumber; // 주문 번호 (출고의 경우 null 가능)
    private String message; // 메시지
    private String status; // 상태 (예: "RECEIVED", "RELEASED")
    private String type; // 타입 (예: "RECEIVING" - 입고, "RELEASE" - 출고)
    private List<HistoryItemDTO> items; // 부품 간단 정보 (ID, 수량만)

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryItemDTO {
        private Long partId;    // 부품 ID
        private int quantity;   // 수량 (입고/출고)
    }
}

