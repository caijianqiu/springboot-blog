package com.cjq.springbootblog.repository;

import com.cjq.springbootblog.entity.Blog;
import com.cjq.springbootblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    /**
     * 根据博客所属的用户以及博客标题进行分页查询
     *
     * @param user
     * @param title
     * @param pageable
     * @return
     */
    Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);

    /**
     * 根据博客标题和用户或者博客标签和用户分页查找博客内容，并逆序排列
     *
     * @param title
     * @param user
     * @param tags
     * @param user2
     * @param pageable
     * @return
     */
    Page<Blog> findByTitleLikeAndUserOrTagsLikeAndUserOrderByCreateTimeDesc(String title, User user, String tags, User user2, Pageable pageable);
}
