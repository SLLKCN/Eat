package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.user.PostUser;
import com.example.eat.model.dto.param.user.PutUser;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.model.dto.res.user.UserRes;
import com.example.eat.model.po.user.User;

public interface UserService extends IService<User> {
    CommonResult<LoginRes> register(PostUser postUser);
    CommonResult<LoginRes> login(PostUser postUser);

    CommonResult<BlankRes> updateUserInfo(PutUser putUser);

    CommonResult<UserRes> getOnesUserInfo(Integer userGetId);

    CommonResult<UserRes> getMyUserInfo();
}
