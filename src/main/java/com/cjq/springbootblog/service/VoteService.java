package com.cjq.springbootblog.service;

import com.cjq.springbootblog.entity.Vote;

import java.util.Optional;

public interface VoteService {
    /**
     * 根据id获取点赞
     *
     * @param id
     * @return
     */
    Optional<Vote> getVoteById(Long id);

    /**
     * 删除点赞
     *
     * @param id
     */
    void removeVotes(Long id);
}
