package com.example.eat.dao.post;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.po.post.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostDao extends BaseMapper<Post> {
}
