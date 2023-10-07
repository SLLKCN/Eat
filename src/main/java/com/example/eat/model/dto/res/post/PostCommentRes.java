package com.example.eat.model.dto.res.post;


import com.example.eat.model.dto.res.user.UserRes;
import com.example.eat.model.po.post.PostComment;
import lombok.Data;



@Data
public class PostCommentRes {
    private Integer id;
    private String content;
    private UserRes userRes;
    public PostCommentRes(PostComment postComment){
        this.id= postComment.getId();
        this.content=postComment.getContent();
    }
}
