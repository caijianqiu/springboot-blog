package com.cjq.springbootblog.controller;

import com.cjq.springbootblog.entity.Blog;
import com.cjq.springbootblog.entity.User;
import com.cjq.springbootblog.entity.Vote;
import com.cjq.springbootblog.service.BlogService;
import com.cjq.springbootblog.service.UserServcie;
import com.cjq.springbootblog.util.ConstraintViolationExceptionHandler;
import com.cjq.springbootblog.vo.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/u")
public class UserspaceController {
    @Autowired
    private UserServcie userServcie;
    @Autowired
    BlogService blogService;

    @Qualifier("userServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    //文件服务器的地址
    @Value("${file.server.url}")
    private String fileServerUrl;

    /**
     * 访问用户主页
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);  //根据用户名获得用户的信息
        model.addAttribute("user", user);

        return "redirect:/u/" + username + "/blogs";
    }

    /**
     * 获取用户设置的界面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)") // 预先做验证
    public ModelAndView profile(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("fileServerUrl", fileServerUrl); // 将文件服务器的url返回前端，请求该url可以拿到头像

        return new ModelAndView("/userspace/profile", "userModel", model);
    }

    /**
     * 修改后的保存页面逻辑
     *
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)") // 预先做验证
    public String saveProfile(@PathVariable("username") String username, User user) {
        //先根据id拿到数据库中的用户，修改数据库中的内容
        User originalUser = userServcie.getUserById(user.getId()).get();
        originalUser.setEmail(user.getEmail());
        originalUser.setName(user.getName());

        //判断密码是否做了修改，使用BCrypt加密算法对密码做加密处理
        String rawPassword = originalUser.getPassword(); //数据库中的原始密码
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(user.getPassword()); //新的编码密码
        boolean isMatch = encoder.matches(rawPassword, encodePassword);
        if (!isMatch) {  //如果不匹配,将页面新的密码写入数据库
            originalUser.setEncodePassword(user.getPassword());
        }

        //将修改后的结果写入数据库
        userServcie.saveOrUpdateUser(originalUser);

        return "redirect:/u/" + username + "/profile";
    }

    /**
     * 获取编辑头像的页面
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)") // 预先做验证
    public ModelAndView avatar(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("/userspace/avatar", "userModel", model);
    }

    /**
     * 保存头像
     *
     * @param username
     * @param user
     * @return
     */
    @PostMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)") // 预先做验证
    public ResponseEntity<ResponseResult> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
        String avatarUrl = user.getAvatar();

        User originalUser = userServcie.getUserById(user.getId()).get();
        userServcie.saveOrUpdateUser(originalUser);

        return ResponseEntity.ok().body(new ResponseResult(true, "处理成功", avatarUrl));
    }

    /**
     * 分类查询
     *
     * @param username
     * @param order
     * @param catalogId
     * @param keyword
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(@PathVariable("username") String username,
                                   @RequestParam(value = "order", required = false, defaultValue = "new") String order,
                                   @RequestParam(value = "catalogId", required = false) Long catalogId,  //博客分类
                                   @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                   @RequestParam(value = "async", required = false) boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                   Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);

        Page<Blog> page = null;

        if (catalogId != null && catalogId > 0) {

        } else if (order.equals("hot")) {  //最热查询
            Sort sort = new Sort(Sort.Direction.DESC, "reading", "comments", "voting");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

            page = blogService.listBlogsByTitleVoteAndSort(user, keyword, pageable);
        } else if (order.equals("new")) {
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByTitleVote(user, keyword, pageable);
        }

        List<Blog> list = page.getContent();

        model.addAttribute("user", user);
        model.addAttribute("order", order);
        model.addAttribute("catalogId", catalogId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("page", page);
        model.addAttribute("blogList", list);

        return async == true ? "/userspace/u::#mainContainerRepleace" : "/userspace/u";
    }

    /**
     * 获取博客展示界面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {
        User principal = null;
        Blog blog = blogService.getBlogById(id).get();

        // 每次读取博客，可以简单地认为阅读量增加1次
        blogService.readingIncrease(id);

        boolean isBlogOwner = false;

        // 判断操作用户是否是博客的所有者
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()  //已经认证
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && username.equals(principal.getUsername())) {
                isBlogOwner = true;
            }
        }

        //判断用户的点赞情况
        List<Vote> votes = blog.getVotes();
        Vote currentVote = null;

        if (principal != null) {
            for (Vote vote : votes) {
                if (vote.getUser().getUsername().equals(principal.getUsername())) {
                    currentVote = vote;
                    break;
                }
            }
        }

        model.addAttribute("isBlogOwner", isBlogOwner);  //告诉前台现在的登陆者是博客的所有者
        model.addAttribute("blogModel", blogService.getBlogById(id).get());
        model.addAttribute("currentVote", currentVote);

        return "/userspace/blog";
    }

    /**
     * 获取新增博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit")
    public ModelAndView createBlog(Model model) {
        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("fileServerUrl", fileServerUrl);
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    /**
     * 获取编辑博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    public ModelAndView editBlog(@PathVariable("username") String username, @PathVariable("id") Long id, Model model) {

        model.addAttribute("blog", blogService.getBlogById(id).get());
        //model.addAttribute("fileServerUrl",fileServerUrl);
        return new ModelAndView("/userspace/blogedit", "blogModel", model);
    }

    /**
     * 保存博客
     *
     * @param username
     * @param blog
     * @return
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<ResponseResult> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
        Long blogId = blog.getId();
        try {
            if (blogId != null) { //当前博客存在,需要修改
                Optional<Blog> optionalBlog = blogService.getBlogById(blogId);
                if (optionalBlog.isPresent()) {
                    Blog originalBlog = optionalBlog.get();
                    originalBlog.setTitle(blog.getTitle());
                    originalBlog.setContent(blog.getContent());
                    originalBlog.setSummary(blog.getSummary());

                    blogService.saveBlog(originalBlog);
                }
            } else {  //页面上博客的Id为空，将页面的博客写入数据库中
                User user = (User) userDetailsService.loadUserByUsername(username);  //根据用户名获得用户的全部信息
                blog.setUser(user); // 为博客设置用户
                blogService.saveBlog(blog);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new ResponseResult(false, ConstraintViolationExceptionHandler.getMessage(e))); //将验证的异常信息全部以字符串的形式封装
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ResponseResult(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return ResponseEntity.ok().body(new ResponseResult(true, "处理成功", redirectUrl));
    }

    /**
     * 删除博客
     *
     * @param id
     * @param
     * @return
     */
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<ResponseResult> deleteBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {

        try {
            blogService.removeBlog(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ResponseResult(false, e.getMessage()));
        }

        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new ResponseResult(true, "处理成功", redirectUrl));
    }
}
