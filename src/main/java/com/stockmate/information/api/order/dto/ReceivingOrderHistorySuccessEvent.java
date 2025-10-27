package com.stockmate.information.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivingOrderHistorySuccessEvent {
    private Long orderId;
    private String orderNumber;
    private String approvalAttemptId;
    private String message;
}
