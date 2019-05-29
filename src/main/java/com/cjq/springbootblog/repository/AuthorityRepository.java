package com.cjq.springbootblog.repository;

import com.cjq.springbootblog.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority,Long> {
}
