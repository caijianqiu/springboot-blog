package com.cjq.springbootblog.repository;

import com.cjq.springbootblog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReposity extends JpaRepository<Comment, Long> {
}
