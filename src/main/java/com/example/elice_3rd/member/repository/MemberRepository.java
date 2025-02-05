package com.example.elice_3rd.member.repository;

import com.example.elice_3rd.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Long, Member> {
}
