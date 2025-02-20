package com.example.elice_3rd.hospital.repository;

import com.example.elice_3rd.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {

    Optional<Hospital> findByYkiho(String ykiho);
}
