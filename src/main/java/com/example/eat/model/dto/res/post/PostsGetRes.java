package com.example.eat.model.dto.res.post;

import lombok.Data;

import java.util.List;

@Data
public class PostsGetRes {
    private Long total;
    private List<PostRes> postResList;
}
