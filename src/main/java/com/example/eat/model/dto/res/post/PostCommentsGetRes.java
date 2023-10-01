package com.example.eat.model.dto.res.post;

import com.example.eat.model.po.post.PostComment;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostCommentsGetRes {
    private Long total;
    private List<PostCommentRes> postCommentResList=new ArrayList<>();
    public PostCommentsGetRes(List<PostComment> postCommentList){
        for (PostComment temp:postCommentList) {
            PostCommentRes postCommentRes=new PostCommentRes(temp);
            postCommentResList.add(postCommentRes);
        }
    }
}
