package com.example.eat.dao.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.po.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
