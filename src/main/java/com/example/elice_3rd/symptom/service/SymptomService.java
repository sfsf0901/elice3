package com.example.elice_3rd.symptom.service;

import com.example.elice_3rd.symptom.entity.Symptom;
import com.example.elice_3rd.symptom.repository.SymptomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SymptomService {

    private final SymptomRepository symptomRepository;


    public List<Symptom> findAll() {
        return symptomRepository.findAll();
    }

    public Symptom findByName(String symptomName) {
        return symptomRepository.findByName(symptomName).orElse(null);
    }
}
