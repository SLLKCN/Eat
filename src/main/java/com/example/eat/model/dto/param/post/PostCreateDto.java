package com.example.eat.model.dto.param.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PostCreateDto {
    @NotNull(message = "帖子标题不能为空")
    private String title;
    @NotNull(message = "帖子内容不能为空")
    private String content;
    private List<PostImageUploadDto> images;
}
