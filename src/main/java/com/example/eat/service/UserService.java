package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.user.PostUserLogin;
import com.example.eat.model.dto.param.user.PostUserRegister;
import com.example.eat.model.dto.param.user.PutUser;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.model.dto.res.user.UserRes;
import com.example.eat.model.po.user.User;

public interface UserService extends IService<User> {
    CommonResult<LoginRes> register(PostUserRegister postUserRegister);
    CommonResult<LoginRes> login(PostUserLogin postUserLogin);

    CommonResult<BlankRes> updateUserInfo(PutUser putUser);

    CommonResult<UserRes> getOnesUserInfo(Integer userGetId);

    CommonResult<UserRes> getMyUserInfo();

    CommonResult<BlankRes> updateUserStatus(String status);

    CommonResult<BlankRes> updateUserSignature(String signature);

    CommonResult<BlankRes> getCode(String telephone);
}
