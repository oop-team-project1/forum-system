package com.company.web.forum.helpers;

import com.company.web.forum.models.Comment;
import com.company.web.forum.models.CommentDto;
import com.company.web.forum.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private final CommentService commentService;

    @Autowired

    public CommentMapper(CommentService commentService) {
        this.commentService = commentService;
    }

    public Comment fromDto(int id, CommentDto commentDto) {
        Comment comment = fromDto(commentDto);
        comment.setId(id);
        Comment repositoryComment = commentService.getById(id);
        comment.setCreatedBy(repositoryComment.getCreatedBy());
        comment.setPost(repositoryComment.getPost());
        comment.setParentComment(repositoryComment.getParentComment());
        comment.setDate_of_creation(repositoryComment.getDate_of_creation());
        return comment;
    }

    public Comment fromDto(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setCommentContent(commentDto.getCommentContent());
        // comment.setParentComment(comment);
        return comment;
    }

    public Comment fromDtoUpdatingComment(CommentDto commentDto, Comment oldComment) {
        Comment newComment = new Comment();
        newComment.setId(oldComment.getId());
        newComment.setCommentContent(commentDto.getCommentContent());
        newComment.setCreatedBy(oldComment.getCreatedBy());
        newComment.setParentComment(oldComment.getParentComment());
        newComment.setReplies(oldComment.getReplies());
        newComment.setPost(oldComment.getPost());
        return newComment;
    }

    public CommentDto toDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentContent(comment.getCommentContent());
        return commentDto;
    }

}
