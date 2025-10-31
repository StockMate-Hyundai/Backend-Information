package com.stockmate.information.api.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartsApiService {

    private final WebClient webClient;

    @Value("${parts.server.url}")
    private String partsServerUrl;

    /**
     * Parts 서버에서 부품 상세 정보를 조회합니다.
     * 
     * @param partIds 조회할 부품 ID 목록
     * @return 부품 ID를 키로 하는 부품 상세 정보 Map
     */
    public Map<Long, Map<String, Object>> getPartDetails(List<Long> partIds) {
        if (partIds == null || partIds.isEmpty()) {
            log.warn("부품 ID 목록이 비어있습니다.");
            return new HashMap<>();
        }

        try {
            log.info("Parts 서버 부품 상세 정보 조회 시작 - Part IDs: {}", partIds);

            // POST 방식으로 부품 ID 배열 전송
            String response = webClient.post()
                    .uri(partsServerUrl + "/api/v1/parts/detail")
                    .bodyValue(partIds)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // JSON 파싱
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(response);
            com.fasterxml.jackson.databind.JsonNode dataNode = rootNode.get("data");

            Map<Long, Map<String, Object>> partDetailsMap = new HashMap<>();

            // data는 배열 형태
            if (dataNode != null && dataNode.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode partNode : dataNode) {
                    try {
                        Long partId = partNode.get("id").asLong();
                        Map<String, Object> partDetail = objectMapper.convertValue(
                                partNode,
                                new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
                        partDetailsMap.put(partId, partDetail);
                    } catch (Exception e) {
                        log.error("부품 상세 정보 파싱 실패", e);
                    }
                }
            }

            log.info("Parts 서버 부품 상세 정보 조회 완료 - 조회된 부품 수: {}", partDetailsMap.size());
            return partDetailsMap;

        } catch (Exception e) {
            log.error("Parts 서버 부품 상세 정보 조회 실패 - Error: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}

