package com.example.eat.model.dto.res.post;


import com.example.eat.model.po.post.PostComment;
import lombok.Data;



@Data
public class PostCommentRes {
    private Integer id;
    private Integer userId;

    private String content;
    public PostCommentRes(PostComment postComment){
        this.id= postComment.getPostId();
        this.userId=postComment.getUserId();
        this.content=postComment.getContent();
    }
}
