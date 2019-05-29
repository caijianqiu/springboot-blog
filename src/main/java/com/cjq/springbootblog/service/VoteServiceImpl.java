package com.cjq.springbootblog.service;

import com.cjq.springbootblog.entity.Vote;
import com.cjq.springbootblog.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {
    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Optional<Vote> getVoteById(Long id) {
        Optional<Vote> voteOptional = voteRepository.findById(id);
        return voteOptional;
    }

    @Override
    public void removeVotes(Long id) {
        voteRepository.deleteById(id);
    }
}
