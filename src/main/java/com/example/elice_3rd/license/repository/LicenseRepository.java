package com.example.elice_3rd.license.repository;

import com.example.elice_3rd.license.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<Long, License> {
}
