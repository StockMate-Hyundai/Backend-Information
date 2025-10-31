package com.stockmate.information.api.order.service;

import com.stockmate.information.api.order.dto.UserBatchResponseDTO;
import com.stockmate.information.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserApiService {

    private final WebClient webClient;

    @Value("${user.server.url}")
    private String userServerUrl;

    /**
     * User 서버에서 사용자 정보를 일괄 조회합니다.
     * 
     * @param memberIds 조회할 회원 ID 목록
     * @return 회원 ID를 키로 하는 사용자 정보 Map
     */
    public Map<Long, UserBatchResponseDTO> getUsersByMemberIds(List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            log.warn("회원 ID 목록이 비어있습니다.");
            return new HashMap<>();
        }

        log.info("사용자 정보 일괄 조회 요청 - Member IDs 수: {}", memberIds.size());

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("memberIds", memberIds);

            String response = webClient.post()
                    .uri(userServerUrl + "/api/v1/user/batch")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // JSON 파싱
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(response);
            com.fasterxml.jackson.databind.JsonNode dataNode = rootNode.get("data");

            Map<Long, UserBatchResponseDTO> userMap = new HashMap<>();

            if (dataNode != null && dataNode.isArray()) {
                for (com.fasterxml.jackson.databind.JsonNode userNode : dataNode) {
                    try {
                        UserBatchResponseDTO user = objectMapper.treeToValue(userNode, UserBatchResponseDTO.class);
                        userMap.put(user.getMemberId(), user);
                    } catch (Exception e) {
                        log.error("사용자 정보 파싱 실패", e);
                    }
                }
            }

            log.info("사용자 정보 일괄 조회 완료 - 조회된 사용자 수: {}", userMap.size());
            return userMap;

        } catch (WebClientResponseException e) {
            log.error("User 서버 사용자 정보 조회 실패 - Status: {}, Response: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return new HashMap<>();
        } catch (Exception e) {
            log.error("User 서버 사용자 정보 조회 중 예외 발생 - Error: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}

