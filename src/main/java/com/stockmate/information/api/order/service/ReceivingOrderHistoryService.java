package com.stockmate.information.api.order.service;

import com.stockmate.information.api.order.dto.ReceivingHistoryRequestDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryResponseDTO;
import com.stockmate.information.api.order.entity.ReceivingOrderHistory;
import com.stockmate.information.api.order.repository.ReceivingOrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceivingOrderHistoryService {

    private final ReceivingOrderHistoryRepository receivingOrderHistoryRepository;

    @Transactional
    public ReceivingHistoryResponseDTO registerReceivingHistory(ReceivingHistoryRequestDTO requestDTO) {
        log.info("입출고 히스토리 등록 시작 - 가맹점 ID: {}, 주문 번호: {}, 메시지: {}",
                requestDTO.getMemberId(), requestDTO.getOrderNumber(), requestDTO.getMessage());

        ReceivingOrderHistory receivingOrderHistory = ReceivingOrderHistory.builder()
                .memberId(requestDTO.getMemberId()) // 가맹점 ID
                .orderNumber(requestDTO.getOrderNumber()) // 주문 번호
                .message(requestDTO.getMessage()) // 메시지
                .status(requestDTO.getStatus()) // 상태
                .build();

        ReceivingOrderHistory saved = receivingOrderHistoryRepository.save(receivingOrderHistory);
        
        log.info("입출고 히스토리 등록 완료 - 가맹점 ID: {}, 주문 번호: {}, 상태: {}",
                requestDTO.getMemberId(), requestDTO.getOrderNumber(), requestDTO.getStatus());

        return ReceivingHistoryResponseDTO.builder()
                .id(saved.getId())
                .memberId(saved.getMemberId())
                .orderNumber(saved.getOrderNumber())
                .message(saved.getMessage())
                .status(saved.getStatus())
                .success(true)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReceivingOrderHistory> getReceivingHistoryByMemberId(Long memberId) {
        log.info("가맹점별 입출고 히스토리 조회 - 가맹점 ID: {}", memberId);
        return receivingOrderHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional(readOnly = true)
    public List<ReceivingOrderHistory> getReceivingHistoryByOrderNumber(String orderNumber) {
        log.info("주문별 입출고 히스토리 조회 - Order Number: {}", orderNumber);
        return receivingOrderHistoryRepository.findByOrderNumberOrderByCreatedAtDesc(orderNumber);
    }
}
