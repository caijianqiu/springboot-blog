package com.cjq.springbootblog.service;

import com.cjq.springbootblog.entity.Authority;

import java.util.Optional;

public interface AuthorityService {

    /**
     * 根据id查询权限信息
     *
     */
    Optional<Authority> getAuthorityById(Long id);
}
