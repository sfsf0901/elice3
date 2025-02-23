package com.example.elice_3rd.counsel.repository;

import com.example.elice_3rd.category.entity.Category;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CounselRepository extends JpaRepository<Counsel, Long> {
    Optional<Counsel> findByMember(Member member);
    Page<Counsel> findAll(Pageable pageable);
    @Query("SELECT c FROM Counsel c WHERE c.category.name LIKE %:keyword% OR c.title LIKE %:keyword% OR c.content LIKE %:keyword%")
    Page<Counsel> searchAllByKeyword(String keyword, Pageable pageable);
//    Page<Counsel> findAllByCategoryNameContainingOrTitleContaining(String keyword, Pageable pageable);
    Page<Counsel> findAllByMember(Member member, Pageable pageable);
}
