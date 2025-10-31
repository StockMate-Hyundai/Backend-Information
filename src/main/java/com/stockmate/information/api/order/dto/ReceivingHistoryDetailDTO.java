package com.stockmate.information.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivingHistoryDetailDTO {
    private Long id;
    private Long memberId;
    private Long orderId;
    private String orderNumber;
    private String message;
    private String status;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserBatchResponseDTO userInfo; // 가맹점 정보
    private List<HistoryItemDTO> items; // 부품 상세 정보

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryItemDTO {
        // 부품 서버에서 받은 모든 정보
        private Long id;
        private String name;
        private int price;
        private String image;
        private String trim;
        private String model;
        private int category;
        private String korName;
        private String engName;
        private String categoryName;
        private int amount;
        private String code;
        private String location;
        private int cost;
        
        // 히스토리에서 추가되는 정보
        private int historyQuantity; // 입고/출고 수량
    }
}

