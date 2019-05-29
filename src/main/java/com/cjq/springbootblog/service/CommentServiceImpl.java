package com.cjq.springbootblog.service;

import com.cjq.springbootblog.entity.Comment;
import com.cjq.springbootblog.repository.CommentReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentReposity commentReposity;

    @Override
    public Optional<Comment> getCommentById(Long id) {
        Optional<Comment> optionalComment = commentReposity.findById(id);
        return optionalComment;
    }

    @Transactional
    @Override
    public void removeComment(Long id) {
        commentReposity.deleteById(id);
    }
}
