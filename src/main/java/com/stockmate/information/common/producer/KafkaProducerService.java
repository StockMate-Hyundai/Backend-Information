package com.stockmate.information.common.producer;

import com.stockmate.information.api.order.dto.ReceivingOrderHistorySuccessEvent;
import com.stockmate.information.api.order.dto.ReceivingOrderHistoryFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.receiving-history-success}")
    private String receivingHistorySuccessTopic;

    @Value("${kafka.topics.receiving-history-failed}")
    private String receivingHistoryFailedTopic;

    // 입고 히스토리 등록 성공 이벤트 발송
    public void sendReceivingHistorySuccess(ReceivingOrderHistorySuccessEvent event) {
        log.info("입고 히스토리 등록 성공 이벤트 발송 시작 - Order ID: {}, Order Number: {}", 
                event.getOrderId(), event.getOrderNumber());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                receivingHistorySuccessTopic,
                event.getOrderId().toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("입고 히스토리 등록 성공 이벤트 발송 성공 - 토픽: {}, 파티션: {}, 오프셋: {}, Order ID: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.getOrderId());
            } else {
                log.error("입고 히스토리 등록 성공 이벤트 발송 실패 - Order ID: {}, 에러: {}",
                        event.getOrderId(), ex.getMessage(), ex);
            }
        });
    }

    // 입고 히스토리 등록 실패 이벤트 발송
    public void sendReceivingHistoryFailed(ReceivingOrderHistoryFailedEvent event) {
        log.info("입고 히스토리 등록 실패 이벤트 발송 시작 - Order ID: {}, Order Number: {}, 에러: {}", 
                event.getOrderId(), event.getOrderNumber(), event.getErrorMessage());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                receivingHistoryFailedTopic,
                event.getOrderId().toString(),
                event
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("입고 히스토리 등록 실패 이벤트 발송 성공 - 토픽: {}, 파티션: {}, 오프셋: {}, Order ID: {}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset(),
                        event.getOrderId());
            } else {
                log.error("입고 히스토리 등록 실패 이벤트 발송 실패 - Order ID: {}, 에러: {}",
                        event.getOrderId(), ex.getMessage(), ex);
            }
        });
    }
}
