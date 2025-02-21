package com.example.elice_3rd.hospital.batch.repository;

import com.example.elice_3rd.hospital.batch.entity.HospitalTemp;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HospitalTempRepository extends JpaRepository<HospitalTemp, String> {

    @Query("SELECT h FROM HospitalTemp h WHERE h.ykiho IN :ykihoList")
    List<HospitalTemp> findAllByYkihoIn(@Param("ykihoList") List<String> ykihoList);

/*    @Query(value = """
    SELECT t.*
    FROM hospital_temp t
    INNER JOIN (
        SELECT MIN(hospital_temp_id) as min_id
        FROM hospital_temp
        GROUP BY ykiho
    ) sub ON t.hospital_temp_id = sub.min_id
    """, nativeQuery = true)*/

    @Query("SELECT t FROM HospitalTemp t WHERE t.id IN (SELECT MIN(t2.id) FROM HospitalTemp t2 GROUP BY t2.ykiho)")
    List<HospitalTemp> findDistinctByYkiho();
}
