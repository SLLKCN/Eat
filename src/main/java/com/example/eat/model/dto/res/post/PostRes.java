package com.example.eat.model.dto.res.post;

import com.example.eat.model.po.post.Post;
import com.example.eat.model.po.post.PostImage;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostRes {
    private Integer id;
    private Integer userId;
    private String content;
    private String commentCount;
    private String likeCount;
    private Timestamp createTime;
    private Timestamp updateTime;
    private List<PostImagesRes> images=new ArrayList<>();
    public PostRes(Post post, List<PostImage> imageList){
        this.id = post.getId();
        this.userId= post.getUserId();
        this.content = post.getContent();
        this.setCommentCount(post.getContent());
        this.setLikeCount(post.getContent());
        this.setCreateTime(post.getCreateTime());
        this.setUpdateTime(post.getUpdateTime());

        for (PostImage temp:imageList) {
            PostImagesRes postImagesRes=new PostImagesRes();
            postImagesRes.setImage(temp.getImage());
            postImagesRes.setPosition(temp.getPosition());
            this.images.add(postImagesRes);
        }
    }

}
