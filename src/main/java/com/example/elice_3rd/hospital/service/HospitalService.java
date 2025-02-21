package com.example.elice_3rd.hospital.service;

import com.example.elice_3rd.hospital.entity.Hospital;
import com.example.elice_3rd.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public Optional<Hospital> findByYkiho(String ykiho) {
        return hospitalRepository.findByYkiho(ykiho);
    }

    public List<Hospital> findAll() {
        return hospitalRepository.findAll();
    }
}
