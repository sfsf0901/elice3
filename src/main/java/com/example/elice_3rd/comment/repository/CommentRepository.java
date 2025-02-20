package com.example.elice_3rd.comment.repository;

import com.example.elice_3rd.comment.entity.Comment;
import com.example.elice_3rd.counsel.entity.Counsel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCounsel(Counsel counsel);
}
