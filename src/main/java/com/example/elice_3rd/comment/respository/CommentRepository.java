package com.example.elice_3rd.comment.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.elice_3rd.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long>{
	List<Comment> findByCounsel_CounselId(Long counselId);
	
}
