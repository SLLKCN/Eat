package com.example.eat.model.dto.param.cookbook;

import lombok.Data;

@Data
public class PostCookbook {
    private Integer id;
    private String name;
    private String introduction;
    private String content;
    private String type;
}
