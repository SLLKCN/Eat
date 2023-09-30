package com.example.eat.controller;

import com.example.eat.aop.Pass;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.Filename;
import com.example.eat.model.dto.param.PostUser;
import com.example.eat.model.dto.res.user.LoginRes;
import com.example.eat.service.UserService;
import com.example.eat.util.MinioUtil;
import jakarta.validation.Valid;
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
    @Pass
    @GetMapping("/file")
    CommonResult<String> getfile(@RequestBody Filename filename){
        String url;
        try{
            url=minioUtil.downloadFile(filename.getFilename());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取文件失败");
        }
        return CommonResult.success("获取文件成功",url);
    }

}
