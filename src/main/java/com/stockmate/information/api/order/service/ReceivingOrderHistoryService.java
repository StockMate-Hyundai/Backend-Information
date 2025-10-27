package com.stockmate.information.api.order.service;

import com.stockmate.information.api.order.dto.ReceivingOrderHistoryFailedEvent;
import com.stockmate.information.api.order.dto.ReceivingOrderHistoryRequestEvent;
import com.stockmate.information.api.order.dto.ReceivingOrderHistorySuccessEvent;
import com.stockmate.information.api.order.entity.ReceivingOrderHistory;
import com.stockmate.information.api.order.repository.ReceivingOrderHistoryRepository;
import com.stockmate.information.common.producer.KafkaProducerService;
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
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public void registerReceivingHistory(ReceivingOrderHistoryRequestEvent event) {
        log.info("입고 히스토리 등록 시작 - 가맹점 ID: {}, 주문 번호: {}, 메시지: {}", 
                event.getMemberId(), event.getOrderNumber(), event.getMessage());

        try {
            ReceivingOrderHistory receivingOrderHistory = ReceivingOrderHistory.builder()
                    .memberId(event.getMemberId()) // 가맹점 ID
                    .orderNumber(event.getOrderNumber()) // 주문 번호
                    .message(event.getMessage()) // 메시지
                    .status(event.getStatus()) // 상태
                    .build();

            receivingOrderHistoryRepository.save(receivingOrderHistory);
            
            log.info("입고 히스토리 등록 완료 - 가맹점 ID: {}, 주문 번호: {}, 상태: {}", 
                    event.getMemberId(), event.getOrderNumber(), event.getStatus());

            // 성공 이벤트 발송 (Order 서버로)
            ReceivingOrderHistorySuccessEvent successEvent = ReceivingOrderHistorySuccessEvent.builder()
                    .orderId(event.getOrderId()) // Order 서버에서 전달받은 orderId 사용
                    .orderNumber(event.getOrderNumber())
                    .approvalAttemptId(event.getApprovalAttemptId()) // Order 서버에서 전달받은 attemptId 사용
                    .message("입고 히스토리 등록 성공")
                    .build();

            kafkaProducerService.sendReceivingHistorySuccess(successEvent);
            log.info("입고 히스토리 등록 성공 이벤트 발송 완료 - 주문 번호: {}", event.getOrderNumber());

        } catch (Exception e) {
            log.error("입고 히스토리 등록 실패 - 가맹점 ID: {}, 주문 번호: {}, 에러: {}", 
                    event.getMemberId(), event.getOrderNumber(), e.getMessage(), e);

            // 실패 이벤트 발송 (Order 서버로)
            ReceivingOrderHistoryFailedEvent failedEvent = ReceivingOrderHistoryFailedEvent.builder()
                    .orderId(event.getOrderId()) // Order 서버에서 전달받은 orderId 사용
                    .orderNumber(event.getOrderNumber())
                    .approvalAttemptId(event.getApprovalAttemptId()) // Order 서버에서 전달받은 attemptId 사용
                    .errorMessage("입고 히스토리 등록 실패: " + e.getMessage())
                    .data(e)
                    .build();

            kafkaProducerService.sendReceivingHistoryFailed(failedEvent);
            log.info("입고 히스토리 등록 실패 이벤트 발송 완료 - 주문 번호: {}", event.getOrderNumber());

            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<ReceivingOrderHistory> getReceivingHistoryByMemberId(Long memberId) {
        log.info("가맹점별 입고 히스토리 조회 - 가맹점 ID: {}", memberId);
        return receivingOrderHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional(readOnly = true)
    public List<ReceivingOrderHistory> getReceivingHistoryByOrderNumber(String orderNumber) {
        log.info("주문별 입고 히스토리 조회 - Order Number: {}", orderNumber);
        return receivingOrderHistoryRepository.findByOrderNumberOrderByCreatedAtDesc(orderNumber);
    }
}
