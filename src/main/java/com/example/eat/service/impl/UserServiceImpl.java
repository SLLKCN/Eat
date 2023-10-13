package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.user.UserDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.user.PostUser;
import com.example.eat.model.dto.param.user.PutUser;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.post.PostRes;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.model.dto.res.user.UserRes;
import com.example.eat.model.po.user.User;
import com.example.eat.service.UserService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
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
        LoginRes loginRes=new LoginRes();
        try{
            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("account",postUser.getAccount());
            if(this.count(queryWrapper)>0){
                return CommonResult.fail("已存在该账号");
            }
            User user=new User();
            user.setAccount(postUser.getAccount());
            user.setPassword(postUser.getPassword());
            this.save(user);

            loginRes.setToken(JwtUtils.sign(user));
        }catch (Exception e){
            log.error("用户注册失败");
            return CommonResult.fail("用户注册失败");
        }

        return CommonResult.success("注册成功",loginRes);
    }
    @Override
    public CommonResult<LoginRes> login(PostUser postUser) {
        LoginRes loginRes=new LoginRes();
        try{
            QueryWrapper<User> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("account",postUser.getAccount());



            User user=userDao.selectOne(queryWrapper);
            if(user==null){
                return CommonResult.fail("账号不存在");
            }
            if(!postUser.getPassword().equals(user.getPassword())){
                return CommonResult.fail("密码错误");
            }

            loginRes.setToken(JwtUtils.sign(user));
        }catch (Exception e){
            log.error("用户登录失败");
            return CommonResult.fail("用户登录失败");
        }



        return CommonResult.success("登陆成功",loginRes);
    }

    @Override
    public CommonResult<BlankRes> updateUserInfo(PutUser putUser) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        try{
            User user=new User();
            user.setId(userId);
            user.setNickname(putUser.getNickname());
            user.setAvatar(putUser.getAvatar());
            user.setSex(putUser.getSex());
            user.setAge(putUser.getAge());
            user.setHeight(putUser.getHeight());
            user.setWeight(putUser.getWeight());
            user.setIdentity(putUser.getIdentity());
            user.setDisease(putUser.getDisease());
            user.setSignature(putUser.getSignature());
            this.updateById(user);
        }catch (Exception e){
            log.error("更新用户信息失败");
            return CommonResult.fail("更新用户信息失败");
        }
        return CommonResult.success("更新用户信息成功");
    }

    @Override
    public CommonResult<UserRes> getOnesUserInfo(Integer userGetId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        UserRes userRes;
        try{
            userRes=getUserResById(userGetId);
        }catch (Exception e){
            log.error("查找指定用户信息失败");
            return CommonResult.fail("查找指定用户信息失败");
        }
        return CommonResult.success("查找指定用户信息成功",userRes);
    }

    @Override
    public CommonResult<UserRes> getMyUserInfo() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        UserRes userRes;
        try{
            userRes=getUserResById(userId);
        }catch (Exception e){
            log.error("查找我的用户信息失败");
            return CommonResult.fail("查找我的用户信息失败");
        }
        return CommonResult.success("查找我的用户信息成功",userRes);
    }

    @Override
    public CommonResult<BlankRes> updateUserStatus(String status) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            User user=new User();
            user.setId(userId);
            user.setStatus(status);
            this.updateById(user);
        }catch (Exception e){
            log.error("设置状态失败");
            return CommonResult.fail("设置状态失败");
        }
        return CommonResult.success("设置状态成功");
    }

    @Override
    public CommonResult<BlankRes> updateUserSignature(String signature) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            User user=new User();
            user.setId(userId);
            user.setSignature(signature);
            this.updateById(user);
        }catch (Exception e){
            log.error("更新用户信息失败");
            return CommonResult.fail("更新用户信息失败");
        }
        return CommonResult.success("更新用户信息成功");
    }

    public UserRes getUserResById(Integer userId){
        UserRes userRes;
        try {
            User user=this.getById(userId);
            userRes=new UserRes(user);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return userRes;
    }
}
