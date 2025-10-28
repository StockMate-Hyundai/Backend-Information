package com.stockmate.information.api.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceivingHistoryListResponseDTO {
    private long totalElements;      // 전체 데이터 수
    private int totalPages;          // 전체 페이지 수
    private int currentPage;         // 현재 페이지 번호
    private int pageSize;            // 페이지 크기
    private boolean isLast;          // 마지막 페이지 여부
    private List<ReceivingHistoryDetailDTO> content; // 히스토리 목록 (부품 상세 포함)
}

