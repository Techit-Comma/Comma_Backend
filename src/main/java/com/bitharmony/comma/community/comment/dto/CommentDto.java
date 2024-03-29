package com.bitharmony.comma.community.comment.dto;

import com.bitharmony.comma.community.comment.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
        long commentId,
        long articleId,
        String username,
        String profileUrl,
        String content,
        LocalDateTime createDate,
        LocalDateTime modifyDate
) {
    public CommentDto(Comment comment){
        this(
                comment.getId(),
                comment.getArticle().getId(),
                comment.getCommenter().getUsername(),
                comment.getCommenter().getImageUrl(),
                comment.getContent(),
                comment.getCreateDate(),
                comment.getModifyDate()
        );
    }

}
