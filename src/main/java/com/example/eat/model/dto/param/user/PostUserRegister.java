package com.example.eat.model.dto.param.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostUserRegister {
    @NotNull(message = "账号不能为空")
    @Size(max = 10,message = "账号过长")
    private String account;
    @NotNull(message = "密码不能为空")
    @Size(max = 30,message = "密码过长")
    private String password;
    @NotNull(message = "电话号不能为空")
    @Size(min = 11,max = 11,message = "电话号长度错误")
    private String telephone;
    @NotNull(message = "验证码不能为空")
    @Size(min = 6,max = 6,message = "验证码长度错误")
    private String code;
}
