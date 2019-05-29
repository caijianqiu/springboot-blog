package com.cjq.springbootblog.controller;

import com.cjq.springbootblog.entity.Blog;
import com.cjq.springbootblog.entity.Comment;
import com.cjq.springbootblog.entity.User;
import com.cjq.springbootblog.service.BlogService;
import com.cjq.springbootblog.service.CommentService;
import com.cjq.springbootblog.util.ConstraintViolationExceptionHandler;
import com.cjq.springbootblog.vo.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private BlogService blogService;

    /**
     * 获得用户的评论列表
     *
     * @param blogId
     * @param model
     * @return
     */
    @GetMapping
    public String listComments(@RequestParam("blogId") Long blogId, Model model) {
        Optional<Blog> blogOptional = blogService.getBlogById(blogId);
        List<Comment> commentList = null;

        if (blogOptional.isPresent()) {
            commentList = blogOptional.get().getCommentList();
        }

        String commentOwer = "";
        //判断操作的用户是否为评论的作者,获得用户名
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) { //没有登录则默认的用户名是anonymousUser，登录过后用户名为登录使用的用户名。
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null) {
                commentOwer = principal.getUsername();
            }
        }

        model.addAttribute("commentOwer", commentOwer);
        model.addAttribute("comments", commentList);

        return "/userspace/blog::#mainContainerRepleace";
    }

    /**
     * 发表评论
     *
     * @param blogId
     * @param commentContent
     * @return
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  //指定的角色才能进行操作
    public ResponseEntity<ResponseResult> createComment(Long blogId, String commentContent) {

        try {
            Blog blogOptional = blogService.createComment(blogId, commentContent);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new ResponseResult(false, ConstraintViolationExceptionHandler.getMessage(e))); //规则约束的异常提示信息需要返回前端
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ResponseResult(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new ResponseResult(true, "处理成功", null));
    }

    /**
     * 删除评论
     *
     * @param commentId
     * @param blogId
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  //指定的角色才能进行操作
    public ResponseEntity<ResponseResult> delete(@PathVariable("id") Long commentId, Long blogId) {
        boolean isOwner = false;
        Optional<Comment> commentOptional = commentService.getCommentById(commentId);

        User user = null;
        if (commentOptional.isPresent()) {
            user = commentOptional.get().getUser();
        } else {
            return ResponseEntity.ok().body(new ResponseResult(false, "不存在该评论!"));
        }

        //如果认证的用户刚好就是当前访问的用户
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principle = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principle != null && user.getUsername().equals(principle.getUsername()))
                isOwner = true;
        }

        if (!isOwner) {
            return ResponseEntity.ok().body(new ResponseResult(false, "当前用户没有操作权限"));
        }

        try {
            blogService.removeComment(blogId, commentId);
            commentService.removeComment(commentId);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new ResponseResult(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ResponseResult(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new ResponseResult(true, "处理成功", null));
    }
}
