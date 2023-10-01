package com.example.eat.controller;

import com.example.eat.aop.Pass;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.user.PostUser;
import com.example.eat.model.dto.param.user.PutUser;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.model.dto.res.user.UserRes;
import com.example.eat.service.UserService;
import com.example.eat.util.MinioUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin
public class UserController {
    @Autowired
    MinioUtil minioUtil;
    @Autowired
    UserService userService;
    @Pass
    @PostMapping("/register")
    CommonResult<LoginRes> register(@Valid @RequestBody PostUser postUser){
        return userService.register(postUser);
    }
    @Pass
    @PostMapping("/login")
    CommonResult<LoginRes> login(@Valid @RequestBody PostUser postUser){
        return userService.login(postUser);
    }
    @PutMapping("/user")
    CommonResult<BlankRes> updateUserInfo(@Valid @RequestBody PutUser putUser){
        return userService.updateUserInfo(putUser);
    }
    @GetMapping("/users/{userId}")
    CommonResult<UserRes> getOnesUserInfo(@NotNull(message = "userId不能为空") @PathVariable("userId") String userId){
        return userService.getOnesUserInfo(Integer.parseInt(userId));
    }
    @GetMapping("/users/my")
    CommonResult<UserRes> getMyUserInfo(){
        return userService.getMyUserInfo();
    }
}
