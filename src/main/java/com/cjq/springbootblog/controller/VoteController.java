package com.cjq.springbootblog.controller;

import com.cjq.springbootblog.entity.User;
import com.cjq.springbootblog.entity.Vote;
import com.cjq.springbootblog.service.BlogService;
import com.cjq.springbootblog.service.VoteService;
import com.cjq.springbootblog.util.ConstraintViolationExceptionHandler;
import com.cjq.springbootblog.vo.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

@Controller
@RequestMapping("/votes")
public class VoteController {
    @Autowired
    private BlogService blogService;
    @Autowired
    private VoteService voteService;

    /**
     * 发表点赞，前台发起ajax 的http-post请求，后端获得请求向前台返回json数据，以实体的形式封装
     *
     * @param blogId
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<ResponseResult> createVote(Long blogId) {
        try {
            blogService.createVote(blogId);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new ResponseResult(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ResponseResult(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new ResponseResult(true, "点赞成功"));
    }

    /**
     * 删除点赞
     *
     * @param id
     * @param blogId
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<ResponseResult> delete(@PathVariable("id") Long id, Long blogId) {
        boolean isOwer = false;
        Optional<Vote> voteOptional = voteService.getVoteById(id);
        User user = null;
        if (voteOptional.isPresent()) {
            user = voteOptional.get().getUser();
        } else {
            return ResponseEntity.ok().body(new ResponseResult(false, "不存在该点赞！"));
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) { //未认证默认的用户名是anonymousUser
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername()))
                isOwer = true;  //当前要删除的评论所属的用户和当前认证的用户刚好匹配
        }

        if (!isOwer)  //权限认证，认证失败拒绝操作
            return ResponseEntity.ok().body(new ResponseResult(false, "没有操作权限"));

        try {
            blogService.removeVote(blogId, id);
            voteService.removeVotes(id);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new ResponseResult(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ResponseResult(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new ResponseResult(true, "取消点赞成功"));
    }
}
