package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.user.UserDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.PostUser;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.model.po.user.User;
import com.example.eat.service.UserService;
import com.example.eat.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
    @Autowired
    UserDao userDao;
    @Override
    public CommonResult<LoginRes> register(PostUser postUser) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("account",postUser.getAccount());
        if(this.count(queryWrapper)>0){
            return CommonResult.fail("已存在该账号");
        }
        User user=new User();
        user.setAccount(postUser.getAccount());
        user.setPassword(postUser.getPassword());
        this.save(user);
        LoginRes loginRes=new LoginRes();
        loginRes.setToken(JwtUtils.sign(user));
        return CommonResult.success("注册成功",loginRes);
    }
    @Override
    public CommonResult<LoginRes> login(PostUser postUser) {
        QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("account",postUser.getAccount());



        User user=userDao.selectOne(queryWrapper);
        if(user==null){
            return CommonResult.fail("账号不存在");
        }
        if(!postUser.getPassword().equals(user.getPassword())){
            return CommonResult.fail("密码错误");
        }
        LoginRes loginRes=new LoginRes();
        loginRes.setToken(JwtUtils.sign(user));
        return CommonResult.success("登陆成功",loginRes);
    }


}
