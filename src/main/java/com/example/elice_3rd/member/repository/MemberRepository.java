package com.example.elice_3rd.member.repository;

import com.example.elice_3rd.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Boolean existsByEmail(String email);
    @Modifying
    @Query(value = "DELETE FROM Member m WHERE m.verification = false AND m.createdAt < CURRENT_TIMESTAMP - INTERVAL 5 MINUTE", nativeQuery = true)
    void verificationTimeout();
}
