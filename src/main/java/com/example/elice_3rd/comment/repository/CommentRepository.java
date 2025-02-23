package com.example.elice_3rd.comment.repository;

import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.counsel.entity.Counsel;
import com.example.elice_3rd.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCounsel(Counsel counsel);
    Page<Comment> findAllByCounsel(Counsel counsel, Pageable pageable);
    @Query("SELECT c FROM Comment c WHERE c.member = :member AND (c.content LIKE %:keyword% OR c.counsel.category.name LIKE %:keyword% OR c.counsel.title LIKE %:keyword%)")
    Page<Comment> searchByKeyword(Member member, String keyword, Pageable pageable);
    Boolean existsByCounsel(Counsel counsel);
}
