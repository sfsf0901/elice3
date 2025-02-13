package com.example.elice_3rd.license.repository;

import com.example.elice_3rd.license.entity.License;
import com.example.elice_3rd.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByMember(Member member);
}
