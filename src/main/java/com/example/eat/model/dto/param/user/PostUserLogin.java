package com.example.eat.model.dto.param.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostUserLogin {
    @NotNull(message = "账号不能为空")
    @Size(max = 11,message = "账号格式错误")
    private String account;
    @NotNull(message = "密码不能为空")
    @Size(max = 30,message = "密码过长")
    private String password;
}
