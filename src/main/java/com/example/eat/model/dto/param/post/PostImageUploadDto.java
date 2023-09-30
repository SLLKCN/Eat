package com.example.eat.model.dto.param.post;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostImageUploadDto {
    @NotNull(message = "图片位置不能为空")
    private Integer position;
    @NotNull(message = "图片名字不能为空")
    private String name;
}
