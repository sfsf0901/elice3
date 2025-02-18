package com.example.elice_3rd.counsel.repository;

import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselRepository extends JpaRepository<Counsel, Long> {
    Optional<Counsel> findByMember(Member member);
    Page<Counsel> findAll(Pageable pageable);
    Page<Counsel> findAllByMember(Member member, Pageable pageable);
}
