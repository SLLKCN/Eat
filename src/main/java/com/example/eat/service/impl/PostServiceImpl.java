package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.post.PostCommentDao;
import com.example.eat.dao.post.PostDao;
import com.example.eat.dao.post.PostImageDao;
import com.example.eat.dao.post.PostLikeDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.post.PostCommentCreateDto;
import com.example.eat.model.dto.param.post.PostCreateDto;
import com.example.eat.model.dto.param.post.PostImageUploadDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.post.PostCommentsGetRes;
import com.example.eat.model.dto.res.post.PostLikeStatusRes;
import com.example.eat.model.dto.res.post.PostRes;
import com.example.eat.model.dto.res.post.PostsGetRes;
import com.example.eat.model.po.post.Post;
import com.example.eat.model.po.post.PostComment;
import com.example.eat.model.po.post.PostImage;
import com.example.eat.model.po.post.PostLike;
import com.example.eat.service.PostService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PostServiceImpl extends ServiceImpl<PostDao, Post> implements PostService {
    @Autowired
    PostCommentDao postCommentDao;
    @Autowired
    PostImageDao postImageDao;
    @Autowired
    PostLikeDao postLikeDao;
    @Override
    public CommonResult<BlankRes> addPost(PostCreateDto postCreateDto) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        //添加帖子基本信息
        Post post=new Post();
        try{
            post.setUserId(userId);
            post.setContent(postCreateDto.getContent());
            post.setLikeCount(0);
            post.setCommentCount(0);
            this.save(post);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加帖子失败");
        }

        //添加帖子图片
        try{
            for (PostImageUploadDto temp:postCreateDto.getImages()) {
                PostImage postImage=new PostImage();
                postImage.setPostId(post.getId());
                postImage.setPosition(temp.getPosition());
                postImage.setImage(temp.getName());
                postImageDao.insert(postImage);
            }
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加帖子图片失败");
        }

        return CommonResult.fail("添加帖子成功");
    }

    @Override
    public CommonResult<BlankRes> deletePost(Integer postId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        try{
            Post post=this.getById(postId);
            //鉴别是否为该用户的帖子
            if(!post.getUserId().equals(userId)){
                return CommonResult.fail("并非该用户帖子，无权删除");
            }
            this.deletePost(postId);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("删除帖子失败");
        }
        return CommonResult.success("成功删除帖子");
    }

    @Override
    public CommonResult<PostRes> getPost(Integer postId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }


        PostRes postRes;
        try{
            postRes=getPostByPostId(postId);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("查找单个帖子失败");
        }
        return CommonResult.success("查找单个帖子成功",postRes);
    }

    @Override
    public CommonResult<PostsGetRes> getPosts(Integer pageNum, Integer pageSize) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        Page<Post> postPage = new Page<>(pageNum, pageSize);
        PostsGetRes postsGetRes=new PostsGetRes();
        try{
            //获取Post
            IPage<Post> postIPage=page(postPage);
            List<PostRes> postResList=new ArrayList<>();
            //将帖子及其图片封装
            for (Post temp: postIPage.getRecords()) {
                postResList.add(getPostByPostId(temp.getId()));
            }

            postsGetRes.setPostResList(postResList);
            postsGetRes.setTotal(postIPage.getTotal());
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("分页查询帖子失败");
        }
        return CommonResult.success("分页查询帖子成功",postsGetRes);
    }

    @Override
    public CommonResult<PostsGetRes> getOnesPosts(Integer pageNum, Integer pageSize, Integer onesUserId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        PostsGetRes postsGetRes;
        try{
            postsGetRes=getPostByUserId(pageNum,pageSize,onesUserId);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询指定人帖子失败");
        }
        return CommonResult.success("查询指定人帖子成功",postsGetRes);
    }

    @Override
    public CommonResult<PostsGetRes> getMyPosts(Integer pageNum, Integer pageSize) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        PostsGetRes postsGetRes;
        try{
            postsGetRes=getPostByUserId(pageNum,pageSize,userId);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询我的帖子失败");
        }
        return CommonResult.success("查询我的帖子成功",postsGetRes);
    }

    @Override
    public CommonResult<BlankRes> addPostComment(PostCommentCreateDto postCommentCreateDto) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        try{
            PostComment postComment=new PostComment();
            postComment.setUserId(userId);
            postComment.setPostId(postCommentCreateDto.getPostId());
            postComment.setContent(postCommentCreateDto.getContent());
            postCommentDao.insert(postComment);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加帖子评论失败");
        }
        return CommonResult.success("添加帖子评论成功");
    }

    @Override
    public CommonResult<PostCommentsGetRes> getPostComment(Integer pageNum, Integer pageSize, Integer postId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        Page<PostComment> postCommentPage=new Page<>(pageNum,pageSize);
        PostCommentsGetRes postCommentsGetRes;
        try{
            QueryWrapper<PostComment> postCommentQueryWrapper=new QueryWrapper<>();
            postCommentQueryWrapper.eq("post_id",postId);
            IPage<PostComment> postCommentIPage=postCommentDao.selectPage(postCommentPage,postCommentQueryWrapper);
            postCommentsGetRes=new PostCommentsGetRes(postCommentIPage.getRecords());
            postCommentsGetRes.setTotal(postCommentIPage.getTotal());
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取帖子评论失败");
        }
        return CommonResult.success("获取帖子评论成功",postCommentsGetRes);
    }

    @Override
    public CommonResult<BlankRes> deleteComment(Integer commentId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        try{
            PostComment postComment=postCommentDao.selectById(commentId);
            if(postComment==null){
                return CommonResult.fail("未找到该评论");
            }
            if(!postComment.getUserId().equals(userId)){
                return CommonResult.fail("该评论不属于你，无权限操作");
            }
            postCommentDao.deleteById(commentId);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("删除帖子评论失败");
        }
        return CommonResult.success("删除帖子评论成功");
    }

    @Override
    public CommonResult<BlankRes> likePost(Integer isLike, Integer postId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        try{
            if(isLike.equals(0)){
                QueryWrapper<PostLike> postLikeQueryWrapper=new QueryWrapper<>();
                postLikeQueryWrapper.eq("post_id",postId);
                postLikeQueryWrapper.eq("user_id",userId);
                postLikeDao.delete(postLikeQueryWrapper);
                return CommonResult.success("删除帖子点赞成功");
            }
            PostLike postLike=new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLikeDao.insert(postLike);
        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("修改帖子点赞状态失败");
        }
        return CommonResult.success("修改帖子点赞状态成功");
    }

    @Override
    public CommonResult<PostLikeStatusRes> getLike(Integer postId) {
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        PostLikeStatusRes postLikeStatusRes=new PostLikeStatusRes();
        try{
            QueryWrapper<PostLike> postLikeQueryWrapper=new QueryWrapper<>();
            postLikeQueryWrapper.eq("post_id",postId);
            postLikeQueryWrapper.eq("user_id",userId);
            PostLike postLike=postLikeDao.selectOne(postLikeQueryWrapper);
            if(postLike==null){
                postLikeStatusRes.setIsLike(0);
                return CommonResult.success("查看帖子点赞状态失败",postLikeStatusRes);
            }
            postLikeStatusRes.setIsLike(1);

        }catch(Exception e){
            e.printStackTrace();
            return CommonResult.fail("查看帖子点赞状态失败");
        }
        return CommonResult.success("查看帖子点赞状态失败",postLikeStatusRes);
    }


    //根据帖子id获取PostRes
    public PostRes getPostByPostId(Integer postId){
        PostRes postRes;
        try{
            Post post=this.getById(postId);
            QueryWrapper<PostImage> postImageQueryWrapper=new QueryWrapper<>();
            postImageQueryWrapper.eq("post_id",postId);
            List<PostImage> postImageList=postImageDao.selectList(postImageQueryWrapper);
            postRes=new PostRes(post,postImageList);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return postRes;
    }
    //获取指定id的用户发布的帖子
    public PostsGetRes getPostByUserId(Integer pageNum, Integer pageSize,Integer userId){
        Page<Post> postPage = new Page<>(pageNum, pageSize);
        PostsGetRes postsGetRes=new PostsGetRes();
        try{
            QueryWrapper<Post> postQueryWrapper=new QueryWrapper<>();
            postQueryWrapper.eq("user_id",userId);
            //获取Post
            IPage<Post> postIPage=page(postPage,postQueryWrapper);
            List<PostRes> postResList=new ArrayList<>();
            //将帖子及其图片封装
            for (Post temp: postIPage.getRecords()) {
                postResList.add(getPostByPostId(temp.getId()));
            }

            postsGetRes.setPostResList(postResList);
            postsGetRes.setTotal(postIPage.getTotal());
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return postsGetRes;
    }
}
