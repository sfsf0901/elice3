package com.example.elice_3rd.hospital.repository;

import com.example.elice_3rd.hospital.entity.Hospital;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    List<Hospital> findByYkiho(String ykiho);

    @Query("SELECT h FROM Hospital h WHERE h.ykiho IN :ykihoList")
    List<Hospital> findAllByYkihoIn(@Param("ykihoList") List<String> ykihoList);

}
