package com.cjq.springbootblog.service;


import com.cjq.springbootblog.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserServcie {
    /**
     * 新增、编辑、保存用户
     *
     * @param user
     * @return
     */
    User saveOrUpdateUser(User user);

    /**
     * 注册用户
     *
     * @param user
     * @return
     */
    User registerUser(User user);

    /**
     * 删除用户
     *
     * @param id
     */
    void removeUser(Long id);

    /**
     * 根据id获取用户
     *
     * @param id
     * @return
     */
    Optional<User> getUserById(Long id);

    /**
     * 根据用户名进行分页分页模糊查询
     *
     * @param name
     * @param pageable
     * @return
     */
    Page<User> listUsersByNameLike(String name, Pageable pageable);
}
