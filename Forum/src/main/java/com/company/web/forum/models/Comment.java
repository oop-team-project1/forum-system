package com.company.web.forum.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int id;
    @Column(name = "content")
    private String commentContent;

    @Column(updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern = "dd.MM.yyyy")
    private Date date_of_creation;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parent_comment")
    private Comment parentComment;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentComment",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> replies;

    public Set<Comment> getReplies() {
        return replies;
    }

    public void setReplies(Set<Comment> replies) {
        this.replies = replies;
    }

    public Date getDate_of_creation() {
        return date_of_creation;
    }

    public void setDate_of_creation(Date date_of_creation) {
        this.date_of_creation = date_of_creation;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }


    public Comment() {
    }
}
