package com.stockmate.information.common.consumer;

import com.stockmate.information.api.order.dto.ReceivingOrderHistoryRequestEvent;
import com.stockmate.information.api.order.service.ReceivingOrderHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReceivingHistoryConsumer {

    private final ReceivingOrderHistoryService receivingOrderHistoryService;

    @KafkaListener(topics = "receiving-history-request", groupId = "information-service")
    public void handleReceivingHistoryRequest(ReceivingOrderHistoryRequestEvent event) {
        log.info("=== 입고 히스토리 등록 요청 이벤트 수신 === Order ID: {}, 가맹점 ID: {}", 
                event.getOrderId(), event.getMemberId());
        
        try {
            receivingOrderHistoryService.registerReceivingHistory(event);
            log.info("=== 입고 히스토리 등록 완료 === Order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("=== 입고 히스토리 등록 실패 === Order ID: {}, 에러: {}", 
                    event.getOrderId(), e.getMessage(), e);
        }
    }
}
