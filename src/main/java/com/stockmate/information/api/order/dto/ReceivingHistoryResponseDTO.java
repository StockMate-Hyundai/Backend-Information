package com.stockmate.information.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivingHistoryResponseDTO {
    private Long id;
    private Long memberId;
    private String orderNumber;
    private String message;
    private String status;
    private boolean success;
}

