package com.cjq.springbootblog.service;

import com.cjq.springbootblog.entity.Blog;
import com.cjq.springbootblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BlogService {

    /**
     * 保存博客
     *
     * @param blog
     * @return
     */
    Blog saveBlog(Blog blog);

    /**
     * 删除博客
     *
     * @param id
     */
    void removeBlog(Long id);

    /**
     * 查询博客
     *
     * @param id
     * @return
     */
    Optional<Blog> getBlogById(Long id);

    /**
     * 根据用户和博客标题进行分页查询（最新）
     *
     * @param user
     * @param title
     * @param pageable
     * @return
     */
    Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable);

    /**
     * 根据用户和博客标题进行分页查询（最热）
     *
     * @param user
     * @param title
     * @param pageable
     * @return
     */
    Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable);

    /**
     * 阅读量递增
     *
     * @param id
     */
    void readingIncrease(Long id);

    /**
     * 发表评论
     *
     * @param blogId
     * @param commentContent
     * @return
     */
    Blog createComment(Long blogId, String commentContent);

    /**
     * 删除评论
     *
     * @param blogId
     * @param commentId
     */
    void removeComment(Long blogId, Long commentId);

    /**
     * 点赞
     *
     * @param blogId
     * @return
     */
    Blog createVote(Long blogId);

    /**
     * 取消点赞
     *
     * @param blogId
     * @param voteId
     */
    void removeVote(Long blogId, Long voteId);
}
