package com.cjq.springbootblog.entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 点赞实体类
 */
@Entity
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.DETACH, fetch = FetchType.LAZY, targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;  //点赞者

    @Column(nullable = false)
    @CreationTimestamp  //由数据库创建该字段
    private Timestamp createTime;

    public Long getId() {
        return id;
    }

    protected Vote() {
    }

    public Vote(User user) {
        this.user = user;
    }

    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
