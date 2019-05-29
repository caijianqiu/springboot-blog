package com.cjq.springbootblog.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

/**
 * 权限实体类
 */
@Entity
public class Authority implements GrantedAuthority {
    private static final long serialVersionUID = 1L;

    // 权限id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //权限名称
    @Column(nullable = false)
    private String name;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getAuthority() {
        return name;
    }
}
