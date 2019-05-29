package com.cjq.springbootblog.repository;

import com.cjq.springbootblog.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
}
