package com.example.elice_3rd.license.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.elice_3rd.license.entity.License;

public interface LicenseRepository extends JpaRepository <License, Long> {
	
	Optional<License> findByBusinessRegistration(String businessRegistration);
}
