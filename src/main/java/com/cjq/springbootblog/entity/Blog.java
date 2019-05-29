package com.cjq.springbootblog.entity;

import com.github.rjeschke.txtmark.Processor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 博客实体类
 */
@Entity
public class Blog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @NotEmpty(message = "标题不能为空")
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50) // 映射为字段，值不能为空
    private String title; //博客标题

    @NotEmpty(message = "摘要不能为空")
    @Size(min = 2, max = 300)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String summary; //博客摘要

    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String content;  //博客内容

    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(min = 2)
    @Column(nullable = false) // 映射为字段，值不能为空
    private String htmlContent; // 将 md 转为 html


    @OneToOne(cascade = CascadeType.DETACH, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;  //博客所属的用户

    @Column(nullable = false) // 映射为字段，值不能为空
    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    private Timestamp createTime; //创建时间

    @Column(name = "reading")
    private Long reading = 0L; // 访问量、阅读量

    @Column(name = "comments")
    private Long comments = 0L;  // 评论量

    @Column(name = "voting")
    private Long voting = 0L;  // 点赞量

    @Column(name = "tags", length = 100)
    private String tags;

    @OneToMany(targetEntity = Comment.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "comment_id", referencedColumnName = "id")
    List<Comment> commentList = new ArrayList<>();

    @OneToMany(targetEntity = Vote.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_vote", referencedColumnName = "id")
    private List<Vote> votes = new ArrayList<>();

    protected Blog() {
        //
    }

    public Blog(String title, String summary, String content) {
        this.title = title;
        this.summary = summary;
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        this.htmlContent = Processor.process(content);  //将String类型的类容转化为html格式
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }

    public Long getVoting() {
        return voting;
    }

    public void setVoting(Long voting) {
        this.voting = voting;
    }

    public Long getReading() {
        return reading;
    }

    public void setReading(Long reading) {
        this.reading = reading;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
        this.comments = (long) this.commentList.size();  //评论量=评论列表的长度
    }

    /**
     * 添加评论
     *
     * @param comment
     */
    public void addComment(Comment comment) {
        this.commentList.add(comment);
        this.comments = (long) this.commentList.size();  //评论量=评论列表的长度
    }

    /**
     * 根据id删除评论
     *
     * @param commentId
     */
    public void removeComment(Long commentId) {
        for (int index = 0; index < this.commentList.size(); index++) {
            if (commentList.get(index).getId() == commentId) {
                this.commentList.remove(index);
                break;
            }
        }

        this.comments = (long) this.commentList.size();
    }

    /**
     * 添加点赞
     *
     * @param vote
     * @return
     */
    public boolean addVote(Vote vote) {
        boolean isExist = false;
        //用户的点赞已经存在
        for (int index = 0; index < this.votes.size(); index++) {
            if (this.votes.get(index).getUser().getId() == vote.getUser().getId()) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            this.votes.add(vote);
            this.voting = (long) this.votes.size();  // 重新计算取决的投票
        }
        return isExist;
    }

    /**
     * 取消点赞
     *
     * @param voteId
     */
    public void removeVote(Long voteId) {
        for (int index = 0; index < this.votes.size(); index++) {
            if (this.votes.get(index).getId() == voteId) {
                this.votes.remove(index);
                break;
            }
        }

        this.voting = (long) this.votes.size();
    }
}
