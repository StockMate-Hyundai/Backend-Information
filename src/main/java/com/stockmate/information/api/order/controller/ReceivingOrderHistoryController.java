package com.stockmate.information.api.order.controller;

import com.stockmate.information.api.order.dto.ReceivingHistoryListResponseDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryRequestDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryResponseDTO;
import com.stockmate.information.common.config.security.Role;
import com.stockmate.information.common.config.security.SecurityUser;
import com.stockmate.information.common.exception.UnauthorizedException;
import com.stockmate.information.common.response.ApiResponse;
import com.stockmate.information.common.response.SuccessStatus;
import com.stockmate.information.api.order.service.ReceivingOrderHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/information/order-history")
@RequiredArgsConstructor
@Slf4j
public class ReceivingOrderHistoryController {

    private final ReceivingOrderHistoryService receivingOrderHistoryService;

    @Operation(summary = "입출고 히스토리 등록 API", description = "입출고 히스토리를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReceivingHistoryResponseDTO>> registerReceivingHistory(@RequestBody ReceivingHistoryRequestDTO requestDTO) {
        log.info("입출고 히스토리 등록 요청 - 가맹점 ID: {}, 주문 번호: {}", requestDTO.getMemberId(), requestDTO.getOrderNumber());

        ReceivingHistoryResponseDTO response = receivingOrderHistoryService.registerReceivingHistory(requestDTO);
        log.info("입출고 히스토리 등록 완료 - 가맹점 ID: {}, 주문 번호: {}", requestDTO.getMemberId(), requestDTO.getOrderNumber());

        return ApiResponse.success(SuccessStatus.REGISTER_RECEIVING_HISTORY_SUCCESS, response);
    }

    @Operation(summary = "가맹점별 입출고 히스토리 조회 API", description = "토큰 인증된 가맹점의 입출고 히스토리를 조회합니다. (가맹점 전용)")
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<ReceivingHistoryListResponseDTO>> getMyReceivingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SecurityUser securityUser) {
        
        log.info("가맹점별 입출고 히스토리 조회 요청 - 가맹점 ID: {}, Page: {}, Size: {}", 
                securityUser.getMemberId(), page, size);

        ReceivingHistoryListResponseDTO response = receivingOrderHistoryService.getReceivingHistoryByMemberId(
                securityUser.getMemberId(), page, size);
        
        log.info("가맹점별 입출고 히스토리 조회 완료 - 가맹점 ID: {}, 총 데이터 수: {}", 
                securityUser.getMemberId(), response.getTotalElements());

        return ApiResponse.success(SuccessStatus.GET_RECEIVING_HISTORY_SUCCESS, response);
    }

    @Operation(summary = "관리자용 전체 입출고 히스토리 조회 API", description = "모든 가맹점의 입출고 히스토리를 조회합니다. (관리자 전용)")
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<ReceivingHistoryListResponseDTO>> getAllReceivingHistoryForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SecurityUser securityUser) {
        
        log.info("관리자용 전체 입출고 히스토리 조회 요청 - 요청자 ID: {}, Role: {}, Page: {}, Size: {}", 
                securityUser.getMemberId(), securityUser.getRole(), page, size);

        // 권한 체크 (ADMIN 또는 SUPER_ADMIN만 가능)
        if (securityUser.getRole() != Role.ADMIN && securityUser.getRole() != Role.SUPER_ADMIN && securityUser.getRole() != Role.WAREHOUSE) {
            log.error("권한 부족 - 요청자 ID: {}, Role: {}", securityUser.getMemberId(), securityUser.getRole());
            throw new UnauthorizedException("관리자 권한이 필요합니다.");
        }

        ReceivingHistoryListResponseDTO response = receivingOrderHistoryService.getAllReceivingHistory(page, size);
        
        log.info("관리자용 전체 입출고 히스토리 조회 완료 - 총 데이터 수: {}", response.getTotalElements());

        return ApiResponse.success(SuccessStatus.GET_RECEIVING_HISTORY_SUCCESS, response);
    }

    @Operation(summary = "관리자용 특정 가맹점 입출고 히스토리 조회 API", description = "특정 가맹점의 입출고 히스토리를 조회합니다. (관리자 전용)")
    @GetMapping("/admin/member/{memberId}")
    public ResponseEntity<ApiResponse<ReceivingHistoryListResponseDTO>> getReceivingHistoryByMemberIdForAdmin(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal SecurityUser securityUser) {
        
        log.info("관리자용 특정 가맹점 입출고 히스토리 조회 요청 - 가맹점 ID: {}, 요청자 ID: {}, Role: {}, Page: {}, Size: {}", 
                memberId, securityUser.getMemberId(), securityUser.getRole(), page, size);

        // 권한 체크 (ADMIN 또는 SUPER_ADMIN만 가능)
        if (securityUser.getRole() != Role.ADMIN && securityUser.getRole() != Role.SUPER_ADMIN && securityUser.getRole() != Role.WAREHOUSE) {
            log.error("권한 부족 - 요청자 ID: {}, Role: {}", securityUser.getMemberId(), securityUser.getRole());
            throw new UnauthorizedException("관리자 권한이 필요합니다.");
        }

        ReceivingHistoryListResponseDTO response = receivingOrderHistoryService.getReceivingHistoryByMemberIdForAdmin(
                memberId, page, size);
        
        log.info("관리자용 특정 가맹점 입출고 히스토리 조회 완료 - 가맹점 ID: {}, 총 데이터 수: {}", 
                memberId, response.getTotalElements());

        return ApiResponse.success(SuccessStatus.GET_RECEIVING_HISTORY_SUCCESS, response);
    }

    @Operation(summary = "주문별 입출고 히스토리 조회 API", description = "특정 주문의 입출고 히스토리를 조회합니다.")
    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<ApiResponse<ReceivingHistoryListResponseDTO>> getReceivingHistoryByOrderNumber(
            @PathVariable String orderNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("주문별 입출고 히스토리 조회 요청 - Order Number: {}, Page: {}, Size: {}", orderNumber, page, size);

        ReceivingHistoryListResponseDTO response = receivingOrderHistoryService.getReceivingHistoryByOrderNumber(
                orderNumber, page, size);
        
        log.info("주문별 입출고 히스토리 조회 완료 - Order Number: {}, 총 데이터 수: {}", 
                orderNumber, response.getTotalElements());

        return ApiResponse.success(SuccessStatus.GET_RECEIVING_HISTORY_SUCCESS, response);
    }
}
