package com.example.elice_3rd.hospital.service;

import com.example.elice_3rd.diagnosisSubject.entity.DiagnosisSubject;
import com.example.elice_3rd.hospital.dto.HospitalInfo;
import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    private final WebClient webClient = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB로 증가
                    .build())
            .build();

    @Value("${api.service.key}")
    private String serviceKey;

    public List<HospitalInfo> getHospitalsFromAPI(DiagnosisSubject diagnosisSubject, int currentPage, int pageSize) {
        String requestUrl = UriComponentsBuilder
                .fromUriString("https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList")
                .queryParam("ServiceKey", serviceKey)
                .queryParam("pageNo", currentPage)
                .queryParam("numOfRows", pageSize)
                .queryParam("dgsbjtCd", diagnosisSubject.getDiagnosisSubjectCode())
                .build(true)
                .toUriString();

        URI url = null;
        try {
            url = new URI(requestUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        System.out.println("########url = " + url);

        String responseBody = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return parseXmlResponse(responseBody, diagnosisSubject);
    }



    private List<HospitalInfo> parseXmlResponse(String xml, DiagnosisSubject diagnosisSubject) {
        List<HospitalInfo> hospitals = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

            NodeList nodeList = doc.getElementsByTagName("item");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element element = (Element) nodeList.item(i);
                String ykiho = getTagValue("ykiho", element);
                String yadmNm = getTagValue("yadmNm", element);
                String postNo = getTagValue("postNo", element);
                String addr = getTagValue("addr", element);
                String telno = getTagValue("telno", element);
                String hospUrl = getTagValue("hospUrl", element);
                String yPos = getTagValue("YPos", element);
                String xPos = getTagValue("XPos", element);

                hospitals.add(new HospitalInfo(ykiho, yadmNm, postNo, addr, telno, hospUrl, yPos, xPos, diagnosisSubject, diagnosisSubject.getCategory()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hospitals;
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }

    public List<Hospital> findByYkiho(String ykiho) {
        return hospitalRepository.findByYkiho(ykiho);
    }

    public List<Hospital> findAllByYkihoIn(List<String> ykihoList) {
        return hospitalRepository.findAllByYkihoIn(ykihoList);
    }

}
