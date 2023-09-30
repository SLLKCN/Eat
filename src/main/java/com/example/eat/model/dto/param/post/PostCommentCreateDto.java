package com.example.eat.model.dto.param.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostCommentCreateDto {
    @NotNull(message = "帖子id不能为空")
    private Integer postId;
    @NotNull(message = "评论内容不能为空")
    private String content;
}
