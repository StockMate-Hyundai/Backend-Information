package com.stockmate.information.common.consumer;

import com.stockmate.information.api.order.dto.ReceivingOrderHistoryRequestEvent;
import com.stockmate.information.api.order.service.ReceivingOrderHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReceivingHistoryConsumer {

    private final ReceivingOrderHistoryService receivingOrderHistoryService;

    @KafkaListener(
            topics = "${kafka.topics.receiving-history-request}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleReceivingHistoryRequest(
            @Payload ReceivingOrderHistoryRequestEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.info("입고 히스토리 요청 이벤트 수신 - 토픽: {}, 파티션: {}, 오프셋: {}, Order ID: {}, 가맹점 ID: {}", 
                topic, partition, offset, event.getOrderId(), event.getMemberId());
        
        try {
            receivingOrderHistoryService.registerReceivingHistory(event);
            acknowledgment.acknowledge();
            log.info("입고 히스토리 요청 처리 완료 - Order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("입고 히스토리 요청 처리 실패 - Order ID: {}, 에러: {}", 
                    event.getOrderId(), e.getMessage(), e);
        }
    }
}