package com.cjq.springbootblog.service;

import com.cjq.springbootblog.entity.Blog;
import com.cjq.springbootblog.entity.Comment;
import com.cjq.springbootblog.entity.User;
import com.cjq.springbootblog.entity.Vote;
import com.cjq.springbootblog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogRepository blogRepository;

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Optional<Blog> getBlogById(Long id) {
        Optional<Blog> blogOptional = blogRepository.findById(id);
        return blogOptional;
    }

    @Override
    public Page<Blog> listBlogsByTitleVote(User user, String title, Pageable pageable) {
        title = "%" + title + "%";
        String tags = title;
        Page<Blog> blogs = blogRepository.findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(title, user, tags, user, pageable);

        return blogs;
    }

    @Override
    public Page<Blog> listBlogsByTitleVoteAndSort(User user, String title, Pageable pageable) {
        title = "%" + title + "%";
        Page<Blog> blogs = blogRepository.findByUserAndTitleLike(user, title, pageable);
        return blogs;
    }

    @Override
    public void readingIncrease(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isPresent()) {
            Blog newBlog = blog.get();
            newBlog.setReading(newBlog.getReading() + 1);

            this.saveBlog(newBlog);
        }
    }

    @Override
    public Blog createComment(Long blogId, String commentContent) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);

        Blog originalBlog = null;
        if (blogOptional.isPresent()) {
            originalBlog = blogOptional.get();
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Comment comment = new Comment(user, commentContent);
            originalBlog.addComment(comment);
        }

        return this.saveBlog(originalBlog);
    }

    /**
     * 从博客的评论列表中删除评论
     *
     * @param blogId
     * @param commentId
     */
    @Override
    public void removeComment(Long blogId, Long commentId) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        if (blogOptional.isPresent()) {
            Blog blog = blogOptional.get();
            blog.removeComment(commentId);
            this.saveBlog(blog);
        }
    }

    /**
     * 点赞管理
     *
     * @param blogId
     * @return
     */
    @Override
    public Blog createVote(Long blogId) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        Blog originalBlog = null;

        if (blogOptional.isPresent()) {
            originalBlog = blogOptional.get();

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Vote vote = new Vote(user);
            boolean isExist = originalBlog.addVote(vote);
            if (isExist) {
                throw new IllegalArgumentException("该用户已经点过赞了");
            }
        }

        return this.saveBlog(originalBlog);
    }

    @Override
    public void removeVote(Long blogId, Long voteId) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        Blog originalBlog = null;

        if (blogOptional.isPresent()) {
            originalBlog = blogOptional.get();

            originalBlog.removeVote(voteId);
            this.saveBlog(originalBlog);
        }
    }
}
