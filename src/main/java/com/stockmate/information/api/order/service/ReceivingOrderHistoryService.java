package com.stockmate.information.api.order.service;

import com.stockmate.information.api.order.dto.ReceivingHistoryListResponseDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryRequestDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryResponseDTO;
import com.stockmate.information.api.order.entity.ReceivingOrderHistory;
import com.stockmate.information.api.order.repository.ReceivingOrderHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 가맹점별 입출고 히스토리 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public ReceivingHistoryListResponseDTO getReceivingHistoryByMemberId(Long memberId, int page, int size) {
        log.info("가맹점별 입출고 히스토리 조회 - 가맹점 ID: {}, Page: {}, Size: {}", memberId, page, size);
        
        // 페이지 번호와 크기 검증
        int validPage = page < 0 ? 0 : page;
        int validSize = (size <= 0 || size > 100) ? 20 : size;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(validPage, validSize);
        org.springframework.data.domain.Page<ReceivingOrderHistory> historyPage = receivingOrderHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        
        log.info("가맹점별 입출고 히스토리 조회 완료 - 가맹점 ID: {}, 총 데이터 수: {}, 현재 페이지 데이터 수: {}", 
                memberId, historyPage.getTotalElements(), historyPage.getContent().size());
        
        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(historyPage.getContent())
                .build();
    }

    // 관리자용 - 모든 입출고 히스토리 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public ReceivingHistoryListResponseDTO getAllReceivingHistory(int page, int size) {
        log.info("관리자용 전체 입출고 히스토리 조회 - Page: {}, Size: {}", page, size);
        
        // 페이지 번호와 크기 검증
        int validPage = page < 0 ? 0 : page;
        int validSize = (size <= 0 || size > 100) ? 20 : size;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(validPage, validSize);
        org.springframework.data.domain.Page<ReceivingOrderHistory> historyPage = receivingOrderHistoryRepository.findAllOrderByCreatedAtDesc(pageable);
        
        log.info("관리자용 전체 입출고 히스토리 조회 완료 - 총 데이터 수: {}, 현재 페이지 데이터 수: {}", 
                historyPage.getTotalElements(), historyPage.getContent().size());
        
        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(historyPage.getContent())
                .build();
    }

    // 관리자용 - 특정 가맹점 입출고 히스토리 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public ReceivingHistoryListResponseDTO getReceivingHistoryByMemberIdForAdmin(Long memberId, int page, int size) {
        log.info("관리자용 특정 가맹점 입출고 히스토리 조회 - 가맹점 ID: {}, Page: {}, Size: {}", memberId, page, size);
        
        // 페이지 번호와 크기 검증
        int validPage = page < 0 ? 0 : page;
        int validSize = (size <= 0 || size > 100) ? 20 : size;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(validPage, validSize);
        org.springframework.data.domain.Page<ReceivingOrderHistory> historyPage = receivingOrderHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        
        log.info("관리자용 특정 가맹점 입출고 히스토리 조회 완료 - 가맹점 ID: {}, 총 데이터 수: {}, 현재 페이지 데이터 수: {}", 
                memberId, historyPage.getTotalElements(), historyPage.getContent().size());
        
        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(historyPage.getContent())
                .build();
    }

    // 주문별 입출고 히스토리 조회 (페이지네이션)
    @Transactional(readOnly = true)
    public ReceivingHistoryListResponseDTO getReceivingHistoryByOrderNumber(String orderNumber, int page, int size) {
        log.info("주문별 입출고 히스토리 조회 - Order Number: {}, Page: {}, Size: {}", orderNumber, page, size);
        
        // 페이지 번호와 크기 검증
        int validPage = page < 0 ? 0 : page;
        int validSize = (size <= 0 || size > 100) ? 20 : size;
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(validPage, validSize);
        org.springframework.data.domain.Page<ReceivingOrderHistory> historyPage = receivingOrderHistoryRepository.findByOrderNumberOrderByCreatedAtDesc(orderNumber, pageable);
        
        log.info("주문별 입출고 히스토리 조회 완료 - Order Number: {}, 총 데이터 수: {}, 현재 페이지 데이터 수: {}", 
                orderNumber, historyPage.getTotalElements(), historyPage.getContent().size());
        
        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(historyPage.getContent())
                .build();
    }
}
