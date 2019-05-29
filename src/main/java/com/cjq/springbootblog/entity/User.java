package com.cjq.springbootblog.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

@Entity
public class User implements UserDetails, Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "姓名不能为空") //为空时出现提示信息
    @Size(min = 2, max = 20)
    @Column(nullable = false, length = 20)
    private String name; //注册的姓名

    @NotEmpty(message = "邮箱不能为空")
    @Size(max = 50)
    @Email(message = "邮箱格式不对")
    @Column(nullable = false, length = 50, unique = true)
    private String email;  //邮箱

    @NotEmpty(message = "账号不能为空")
    @Size(min = 3, max = 20)
    @Column(nullable = false, length = 20, unique = true)
    private String username;  //账号

    @NotEmpty(message = "密码不能为空")
    @Size(max = 100)
    @Column(length = 100)
    private String password; //密码

    @Column(length = 200)
    private String avatar; //用户头像图片的地址

    /**
     * 用户和权限是多对多的关系
     */
    @ManyToMany(targetEntity = Authority.class, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_id", referencedColumnName = "id")})
    private Set<Authority> authorities = new HashSet<>();

    //无参构造函数，设置为protected可防止被直接使用
    protected User() {
    }

    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }


    /**
     * 获得用户的权限列表
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //类中的权限是一个集合，需要将其转化为字符串类型的SimpleGrantedAuthority权限名称才能在前端做展示
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();

        for (Authority authority : this.authorities) {
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }
        return simpleGrantedAuthorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEncodePassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePassword = encoder.encode(password);
        this.password = encodePassword;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * 账号不过期？
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账号是否被启用
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
