package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.post.PostCommentCreateDto;
import com.example.eat.model.dto.param.post.PostCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.post.PostCommentsGetRes;
import com.example.eat.model.dto.res.post.PostLikeStatusRes;
import com.example.eat.model.dto.res.post.PostRes;
import com.example.eat.model.dto.res.post.PostsGetRes;
import com.example.eat.model.po.post.Post;

public interface PostService extends IService<Post> {
    CommonResult<BlankRes> addPost(PostCreateDto postCreateDto);

    CommonResult<BlankRes> deletePost(Integer postId);

    CommonResult<PostRes> getPost(Integer postId);

    CommonResult<PostsGetRes> getPosts(Integer pageNum, Integer pageSize);

    CommonResult<PostsGetRes> getOnesPosts(Integer pageNum, Integer pageSize, Integer userId);

    CommonResult<PostsGetRes> getMyPosts(Integer pageNum, Integer pageSize);

    CommonResult<BlankRes> addPostComment(PostCommentCreateDto postCommentCreateDto);

    CommonResult<PostCommentsGetRes> getPostComment(Integer pageNum, Integer pageSize, Integer postId);

    CommonResult<BlankRes> deleteComment(Integer commentId);

    CommonResult<BlankRes> likePost(Integer postId);

    CommonResult<PostLikeStatusRes> getLike(Integer postId);
}
