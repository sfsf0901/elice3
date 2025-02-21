package com.example.elice_3rd.hospital.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

//@Service
@RequiredArgsConstructor
public class OllamaService {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:11434/api/generate") // Ollama API 엔드포인트
            .build();

    public String analyzeKeyword(String keyword) {
        // Ollama API 요청 JSON 데이터
        Map<String, Object> requestBody = Map.of(
                "model", "llama3",  // 사용 중인 모델 (llama2도 가능)
                "prompt", "사용자가 입력한 문장에서 병원명, 진료과목, 증상을 분석하여 핵심 키워드로 변환해 주세요.\n"
                        + "예: '목이 아파요' -> '이비인후과', '김산내과' -> '병원이름: 김산내과'\n\n"
                        + "사용자 입력: " + keyword,
                "stream", false
        );

        try {
            // Ollama API 호출
            Map<String, Object> response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(); // 동기 호출 (비동기로 하려면 Mono 사용)

            if (response != null && response.containsKey("response")) {
                return response.get("response").toString().trim();
            }
        } catch (Exception e) {
            System.err.println("Ollama API 호출 오류: " + e.getMessage());
        }

        return keyword; // 실패 시 원래 키워드 반환
    }
}
