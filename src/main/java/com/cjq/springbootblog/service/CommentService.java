package com.cjq.springbootblog.service;

import com.cjq.springbootblog.entity.Comment;

import java.util.Optional;

public interface CommentService {
    /**
     * 根据id获得评论
     *
     * @param id
     * @return
     */
    Optional<Comment> getCommentById(Long id);

    /**
     * 根据id删除评论
     *
     * @param id
     */
    void removeComment(Long id);
}
