package com.cjq.springbootblog.controller;

import com.cjq.springbootblog.entity.Authority;
import com.cjq.springbootblog.entity.User;
import com.cjq.springbootblog.service.AuthorityService;
import com.cjq.springbootblog.service.UserServcie;
import com.cjq.springbootblog.util.ConstraintViolationExceptionHandler;
import com.cjq.springbootblog.vo.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServcie userServcie;
    @Autowired
    private AuthorityService authorityService;


    /**
     * 查询所有的用户的列表
     *
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param name
     * @param model
     * @return
     */
    @GetMapping
    public ModelAndView list(@RequestParam(value = "async", required = false) boolean async,
                             @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                             @RequestParam(value = "name", required = false, defaultValue = "") String name,
                             Model model) {

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<User> page = userServcie.listUsersByNameLike(name, pageable);
        List<User> users = page.getContent();  //从分页查询的结果中获内容

        model.addAttribute("page", page);
        model.addAttribute("userList", users);

        return new ModelAndView(async == true ? "users/list::#mainContrainerReplace" : "users/list", "userModel", model);
    }

    @GetMapping("/add")
    public ModelAndView createForm(Model model) {
        model.addAttribute("user", new User(null, null, null, null));
        return new ModelAndView("users/add", "userModel", model);
    }

    /**
     * 保存或更新用户,同时需要给出用户的权限
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseEntity<ResponseResult> saveOrUpdateUser(User user, Long authorityId) {

        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityService.getAuthorityById(authorityId).get());
        user.setAuthorities(authorities);

        try {
            userServcie.saveOrUpdateUser(user);
        } catch (ConstraintViolationException exception) {
            return ResponseEntity.ok().body(new ResponseResult(false, ConstraintViolationExceptionHandler.getMessage(exception)));
        }
        return ResponseEntity.ok().body(new ResponseResult(true, "处理成功"));
    }

    /**
     * 删除用户
     *
     * @param id
     * @param model
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseResult> delete(@PathVariable("id") Long id, Model model) {
        try {
            userServcie.removeUser(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ResponseResult(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new ResponseResult(true, "处理成功!"));
    }

    /**
     * 用户编辑
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit/{id}")
    public ModelAndView modifyForm(@PathVariable("id") Long id, Model model) {
        Optional<User> user = userServcie.getUserById(id);
        model.addAttribute("user", user.get());
        return new ModelAndView("users/edit", "userModel", model);
    }

}
