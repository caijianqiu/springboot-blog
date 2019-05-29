package com.cjq.springbootblog.controller;

import com.cjq.springbootblog.entity.Authority;
import com.cjq.springbootblog.entity.User;
import com.cjq.springbootblog.service.AuthorityService;
import com.cjq.springbootblog.service.UserServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.Set;

@Controller
public class MainController {
    @Autowired
    private UserServcie userServcie;
    @Autowired
    private AuthorityService authorityService;

    private static final Long ROLE_USER_AUTHORITY_ID = 2L;

    /**
     * 主页面
     *
     * @return
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    /**
     * 主页面
     *
     * @return
     */
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    /**
     * 登录页面
     *
     * @return
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * @param model
     * @return
     */
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMsg", "登录失败，用户名或密码错误");
        return "login";
    }

    /**
     * 注册页面
     *
     * @return
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /**
     * 注册用户
     */
    @PostMapping("/register")
    public String registerUser(User user) {
        //所有注册的用户拥有id为2的权限
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID).get());
        user.setAuthorities(authorities);
        user.setEncodePassword(user.getPassword());

        userServcie.registerUser(user);
        return "redirect:/login";
    }
}
