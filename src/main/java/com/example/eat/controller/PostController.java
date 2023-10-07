package com.example.eat.controller;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.post.PostCommentCreateDto;
import com.example.eat.model.dto.param.post.PostCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.post.PostCommentsGetRes;
import com.example.eat.model.dto.res.post.PostLikeStatusRes;
import com.example.eat.model.dto.res.post.PostRes;
import com.example.eat.model.dto.res.post.PostsGetRes;
import com.example.eat.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
public class PostController {
    @Autowired
    PostService postService;
    @PostMapping("/post")
    CommonResult<BlankRes> addPost(@Valid @RequestBody PostCreateDto postCreateDto){
        return postService.addPost(postCreateDto);
    }
    @DeleteMapping("/post/{postId}")
    CommonResult<BlankRes> deletePost(@NotNull(message = "postId不能为空") @PathVariable("postId") String postId){
        return postService.deletePost(Integer.parseInt(postId));
    }
    @GetMapping("/posts/{postId}")
    CommonResult<PostRes> getPost(@NotNull(message = "postId不能为空") @PathVariable("postId") String postId){
        return postService.getPost(Integer.parseInt(postId));
    }
    @GetMapping("/posts")
    CommonResult<PostsGetRes> getPosts(@RequestParam(defaultValue = "1") Integer pageNum,
                                       @RequestParam(defaultValue = "10") Integer pageSize){
        return postService.getPosts(pageNum,pageSize);
    }
    @GetMapping("/users/{userId}/posts")
    CommonResult<PostsGetRes> getOnesPosts(@RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @NotNull(message = "userId不能为空") @PathVariable("userId") String userId){
        return postService.getOnesPosts(pageNum,pageSize,Integer.parseInt(userId));
    }
    @GetMapping("/users/my/posts")
    CommonResult<PostsGetRes> getMyPosts(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return postService.getMyPosts(pageNum,pageSize);
    }

    @PostMapping("/post/comment")
    CommonResult<BlankRes> addPostComment(@Valid @RequestBody PostCommentCreateDto postCommentCreateDto){
        return postService.addPostComment(postCommentCreateDto);
    }
    @GetMapping("/posts/{postId}/comment")
    CommonResult<PostCommentsGetRes> getPostComment(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @NotNull(message = "postId不能为空") @PathVariable("postId") String postId){
        return postService.getPostComment(pageNum,pageSize,Integer.parseInt(postId));
    }
    @DeleteMapping("/post/comment/{commentId}")
    CommonResult<BlankRes> deleteComment(@NotNull(message = "commentId不能为空") @PathVariable("commentId") String commentId){
        return postService.deleteComment(Integer.parseInt(commentId));
    }

    @PutMapping("/posts/{postId}/like")
    CommonResult<BlankRes> likePost(@PathVariable("postId")@NotBlank(message = "帖子id不能为空") String postId){
        return postService.likePost(Integer.parseInt(postId));
    }
    @GetMapping("/posts/{postId}/like")
    CommonResult<PostLikeStatusRes> getLike(@PathVariable("postId")@NotBlank(message = "帖子id不能为空") String postId){
        return postService.getLike(Integer.parseInt(postId));
    }

}
