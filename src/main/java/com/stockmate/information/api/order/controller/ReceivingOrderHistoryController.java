package com.stockmate.information.api.order.controller;

import com.stockmate.information.api.order.entity.ReceivingOrderHistory;
import com.stockmate.information.api.order.service.ReceivingOrderHistoryService;
import com.stockmate.information.common.config.security.SecurityUser;
import com.stockmate.information.common.response.ApiResponse;
import com.stockmate.information.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/information/order-history")
@RequiredArgsConstructor
@Slf4j
public class ReceivingOrderHistoryController {

    private final ReceivingOrderHistoryService receivingOrderHistoryService;

    @Operation(summary = "가맹점별 입고 히스토리 조회 API", description = "가맹점의 입고 히스토리를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReceivingOrderHistory>>> getReceivingHistoryByMemberId(@AuthenticationPrincipal SecurityUser securityUser) {
        log.info("가맹점별 입고 히스토리 조회 요청 - 가맹점 ID: {}", securityUser.getMemberId());

        List<ReceivingOrderHistory> history = receivingOrderHistoryService.getReceivingHistoryByMemberId(securityUser.getMemberId());
        log.info("가맹점별 입고 히스토리 조회 완료 - 가맹점 ID: {}, 히스토리 수: {}", securityUser.getMemberId(), history.size());

        return ApiResponse.success(SuccessStatus.GET_RECEIVING_HISTORY_SUCCESS, history);
    }

    @Operation(summary = "주문별 입고 히스토리 조회 API", description = "특정 주문의 입고 히스토리를 조회합니다.")
    @GetMapping("/{orderNumber}")
    public ResponseEntity<ApiResponse<List<ReceivingOrderHistory>>> getReceivingHistoryByOrderNumber(@PathVariable String orderNumber) {
        log.info("주문별 입고 히스토리 조회 요청 - Order Number: {}", orderNumber);

        List<ReceivingOrderHistory> history = receivingOrderHistoryService.getReceivingHistoryByOrderNumber(orderNumber);
        log.info("주문별 입고 히스토리 조회 완료 - Order Number: {}, 히스토리 수: {}", orderNumber, history.size());

        return ApiResponse.success(SuccessStatus.GET_RECEIVING_HISTORY_SUCCESS, history);
    }
}
