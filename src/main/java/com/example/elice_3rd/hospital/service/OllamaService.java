package com.example.elice_3rd.hospital.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OllamaService {

    private final WebClient webClient = WebClient.builder()
//            .baseUrl("http://localhost:11434/api/generate") // Ollama API 엔드포인트
//            .baseUrl("http://34.64.254.152:11434/api/generate")
            .baseUrl("http://43.200.132.134:11434/api/generate")
            .build();

    public String analyzeKeyword(String keyword) {
        // Ollama API 요청 JSON 데이터
        Map<String, Object> requestBody = Map.of(
                "model", "llama3",  // 사용 중인 모델 (llama2도 가능)
                "prompt", "사용자가 입력한 단어를 증상, 병원이름, 주소 중 하나로 분류해."  +
                "증상일 때는 1을 사용하고, 병원이름일 때는 2를 사용하고, 주소일 때는 3을 사용해서 구분할거야.\n" +
                "카테고리는 다음과 같이 19개가 있어. 가정의학과,내과,마취통증의학과,비뇨기과,산부인과,성형외과,신경과,신경외과,안과,영상의학과,외과,응급의학과,이비인후과,재활의학과,정신건강의학과,정형외과,치과,피부과,한의원.\n" +
                "넌 존스홉킨스의 유능한 의사야.\n" +
                "증상으로 분류되면 19개 카테고리 중에 가장 관련이 높은 것을 골라.\n" +
                "그리고 이게 제일 중요한데, '1:내과' 이 형식을 지켜서 이것만 반환해주고, 다른 설명은 필요없으니까 절대로 추가하지 마. 띄어쓰기도 필요없어.\n" +
                "(예) 증상일때 -> 1:내과, 병원이름일 때 -> 2:병원이름, 주소일 때 -> 3:주소.\n" +
                "사용자가 입력한 단어: " + keyword,
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
