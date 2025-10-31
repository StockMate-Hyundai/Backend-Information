package com.stockmate.information.api.order.service;

import com.stockmate.information.api.order.dto.ReceivingHistoryListResponseDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryRequestDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryResponseDTO;
import com.stockmate.information.api.order.dto.ReceivingHistoryDetailDTO;
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
    private final PartsApiService partsApiService;
    private final UserApiService userApiService;

    @Transactional
    public ReceivingHistoryResponseDTO registerReceivingHistory(ReceivingHistoryRequestDTO requestDTO) {
        log.info("입출고 히스토리 등록 시작 - 가맹점 ID: {}, 주문 번호: {}, 타입: {}, 메시지: {}",
                requestDTO.getMemberId(), requestDTO.getOrderNumber(), requestDTO.getType(), requestDTO.getMessage());

        // 히스토리 엔티티 생성
        ReceivingOrderHistory receivingOrderHistory = ReceivingOrderHistory.builder()
                .memberId(requestDTO.getMemberId()) // 가맹점 ID
                .orderId(requestDTO.getOrderId()) // 주문 ID
                .orderNumber(requestDTO.getOrderNumber()) // 주문 번호
                .message(requestDTO.getMessage()) // 메시지
                .status(requestDTO.getStatus()) // 상태
                .type(requestDTO.getType()) // 타입
                .items(new java.util.ArrayList<>())
                .build();

        // 부품 아이템 추가
        if (requestDTO.getItems() != null && !requestDTO.getItems().isEmpty()) {
            for (ReceivingHistoryRequestDTO.HistoryItemDTO itemDTO : requestDTO.getItems()) {
                com.stockmate.information.api.order.entity.ReceivingHistoryItem item = 
                        com.stockmate.information.api.order.entity.ReceivingHistoryItem.builder()
                        .history(receivingOrderHistory)
                        .partId(itemDTO.getPartId())
                        .quantity(itemDTO.getQuantity())
                        .build();
                receivingOrderHistory.getItems().add(item);
            }
        }

        ReceivingOrderHistory saved = receivingOrderHistoryRepository.save(receivingOrderHistory);
        
        log.info("입출고 히스토리 등록 완료 - 가맹점 ID: {}, 주문 번호: {}, 타입: {}, 상태: {}, 아이템 수: {}",
                requestDTO.getMemberId(), requestDTO.getOrderNumber(), requestDTO.getType(), 
                requestDTO.getStatus(), saved.getItems().size());

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
        
        // Entity를 DetailDTO로 변환
        java.util.List<ReceivingHistoryDetailDTO> content = historyPage.getContent().stream()
                .map(this::convertToDetailDTO)
                .collect(java.util.stream.Collectors.toList());

        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(content)
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
        
        // 모든 히스토리의 회원 ID 추출
        java.util.Set<Long> memberIds = historyPage.getContent().stream()
                .map(ReceivingOrderHistory::getMemberId)
                .collect(java.util.stream.Collectors.toSet());
        
        log.info("사용자 정보 조회 시작 - 회원 수: {}", memberIds.size());
        
        // 사용자 정보 일괄 조회
        java.util.Map<Long, com.stockmate.information.api.order.dto.UserBatchResponseDTO> userMap = 
                userApiService.getUsersByMemberIds(new java.util.ArrayList<>(memberIds));
        
        log.info("사용자 정보 조회 완료 - 조회된 회원 수: {}", userMap.size());
        
        // Entity를 DetailDTO로 변환 (사용자 정보 포함)
        java.util.List<ReceivingHistoryDetailDTO> content = historyPage.getContent().stream()
                .map(history -> convertToDetailDTOWithUser(history, userMap))
                .collect(java.util.stream.Collectors.toList());

        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(content)
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
        
        // 사용자 정보 조회
        log.info("사용자 정보 조회 시작 - 회원 ID: {}", memberId);
        
        java.util.Map<Long, com.stockmate.information.api.order.dto.UserBatchResponseDTO> userMap = 
                userApiService.getUsersByMemberIds(java.util.List.of(memberId));
        
        log.info("사용자 정보 조회 완료 - 조회된 회원 수: {}", userMap.size());
        
        // Entity를 DetailDTO로 변환 (사용자 정보 포함)
        java.util.List<ReceivingHistoryDetailDTO> content = historyPage.getContent().stream()
                .map(history -> convertToDetailDTOWithUser(history, userMap))
                .collect(java.util.stream.Collectors.toList());

        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(content)
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
        
        // Entity를 DetailDTO로 변환
        java.util.List<ReceivingHistoryDetailDTO> content = historyPage.getContent().stream()
                .map(this::convertToDetailDTO)
                .collect(java.util.stream.Collectors.toList());

        return ReceivingHistoryListResponseDTO.builder()
                .totalElements(historyPage.getTotalElements())
                .totalPages(historyPage.getTotalPages())
                .currentPage(historyPage.getNumber())
                .pageSize(historyPage.getSize())
                .isLast(historyPage.isLast())
                .content(content)
                .build();
    }

    // Entity를 DetailDTO로 변환하는 헬퍼 메서드
    private ReceivingHistoryDetailDTO convertToDetailDTO(ReceivingOrderHistory history) {
        java.util.List<ReceivingHistoryDetailDTO.HistoryItemDTO> items = null;
        
        // Entity의 items에서 partId와 quantity 가져오기
        if (history.getItems() != null && !history.getItems().isEmpty()) {
            // Part ID 목록 추출
            java.util.List<Long> partIds = history.getItems().stream()
                    .map(com.stockmate.information.api.order.entity.ReceivingHistoryItem::getPartId)
                    .collect(java.util.stream.Collectors.toList());
            
            // Parts 서버에서 부품 상세 정보 조회
            java.util.Map<Long, java.util.Map<String, Object>> partDetailsMap = partsApiService.getPartDetails(partIds);
            
            // 부품 상세 정보와 수량을 결합
            items = new java.util.ArrayList<>();
            for (com.stockmate.information.api.order.entity.ReceivingHistoryItem item : history.getItems()) {
                Long partId = item.getPartId();
                int quantity = item.getQuantity();
                
                java.util.Map<String, Object> partDetail = partDetailsMap.get(partId);
                
                if (partDetail != null) {
                    // 부품 서버에서 받은 모든 정보를 그대로 매핑
                    items.add(ReceivingHistoryDetailDTO.HistoryItemDTO.builder()
                            .id(partId)
                            .name((String) partDetail.get("name"))
                            .price(((Number) partDetail.getOrDefault("price", 0)).intValue())
                            .image((String) partDetail.get("image"))
                            .trim((String) partDetail.get("trim"))
                            .model((String) partDetail.get("model"))
                            .category(((Number) partDetail.getOrDefault("category", 0)).intValue())
                            .korName((String) partDetail.get("korName"))
                            .engName((String) partDetail.get("engName"))
                            .categoryName((String) partDetail.get("categoryName"))
                            .amount(((Number) partDetail.getOrDefault("amount", 0)).intValue())
                            .code((String) partDetail.get("code"))
                            .location((String) partDetail.get("location"))
                            .cost(((Number) partDetail.getOrDefault("cost", 0)).intValue())
                            .historyQuantity(quantity) // 입고/출고 수량
                            .build());
                } else {
                    // 부품 정보를 못 가져온 경우
                    items.add(ReceivingHistoryDetailDTO.HistoryItemDTO.builder()
                            .id(partId)
                            .name("알 수 없는 부품")
                            .code(String.valueOf(partId))
                            .historyQuantity(quantity)
                            .build());
                }
            }
        }

        return ReceivingHistoryDetailDTO.builder()
                .id(history.getId())
                .memberId(history.getMemberId())
                .orderId(history.getOrderId())
                .orderNumber(history.getOrderNumber())
                .message(history.getMessage())
                .status(history.getStatus())
                .type(history.getType())
                .createdAt(history.getCreatedAt())
                .updatedAt(history.getUpdatedAt())
                .items(items)
                .build();
    }

    // Entity를 DetailDTO로 변환하는 헬퍼 메서드 (사용자 정보 포함)
    private ReceivingHistoryDetailDTO convertToDetailDTOWithUser(
            ReceivingOrderHistory history, 
            java.util.Map<Long, com.stockmate.information.api.order.dto.UserBatchResponseDTO> userMap) {
        
        java.util.List<ReceivingHistoryDetailDTO.HistoryItemDTO> items = null;
        
        // Entity의 items에서 partId와 quantity 가져오기
        if (history.getItems() != null && !history.getItems().isEmpty()) {
            // Part ID 목록 추출
            java.util.List<Long> partIds = history.getItems().stream()
                    .map(com.stockmate.information.api.order.entity.ReceivingHistoryItem::getPartId)
                    .collect(java.util.stream.Collectors.toList());
            
            // Parts 서버에서 부품 상세 정보 조회
            java.util.Map<Long, java.util.Map<String, Object>> partDetailsMap = partsApiService.getPartDetails(partIds);
            
            // 부품 상세 정보와 수량을 결합
            items = new java.util.ArrayList<>();
            for (com.stockmate.information.api.order.entity.ReceivingHistoryItem item : history.getItems()) {
                Long partId = item.getPartId();
                int quantity = item.getQuantity();
                
                java.util.Map<String, Object> partDetail = partDetailsMap.get(partId);
                
                if (partDetail != null) {
                    // 부품 서버에서 받은 모든 정보를 그대로 매핑
                    items.add(ReceivingHistoryDetailDTO.HistoryItemDTO.builder()
                            .id(partId)
                            .name((String) partDetail.get("name"))
                            .price(((Number) partDetail.getOrDefault("price", 0)).intValue())
                            .image((String) partDetail.get("image"))
                            .trim((String) partDetail.get("trim"))
                            .model((String) partDetail.get("model"))
                            .category(((Number) partDetail.getOrDefault("category", 0)).intValue())
                            .korName((String) partDetail.get("korName"))
                            .engName((String) partDetail.get("engName"))
                            .categoryName((String) partDetail.get("categoryName"))
                            .amount(((Number) partDetail.getOrDefault("amount", 0)).intValue())
                            .code((String) partDetail.get("code"))
                            .location((String) partDetail.get("location"))
                            .cost(((Number) partDetail.getOrDefault("cost", 0)).intValue())
                            .historyQuantity(quantity) // 입고/출고 수량
                            .build());
                } else {
                    // 부품 정보를 못 가져온 경우
                    items.add(ReceivingHistoryDetailDTO.HistoryItemDTO.builder()
                            .id(partId)
                            .name("알 수 없는 부품")
                            .code(String.valueOf(partId))
                            .historyQuantity(quantity)
                            .build());
                }
            }
        }

        // 사용자 정보 가져오기
        com.stockmate.information.api.order.dto.UserBatchResponseDTO userInfo = userMap.get(history.getMemberId());

        return ReceivingHistoryDetailDTO.builder()
                .id(history.getId())
                .memberId(history.getMemberId())
                .orderId(history.getOrderId())
                .orderNumber(history.getOrderNumber())
                .message(history.getMessage())
                .status(history.getStatus())
                .type(history.getType())
                .createdAt(history.getCreatedAt())
                .updatedAt(history.getUpdatedAt())
                .userInfo(userInfo) // 사용자 정보 추가
                .items(items)
                .build();
    }
}
