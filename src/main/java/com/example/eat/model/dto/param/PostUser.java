package com.example.eat.model.dto.param;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostUser {
    @NotNull(message = "账号不能为空")
    @Size(max = 30,message = "账号过长")
    private String account;
    @NotNull(message = "密码不能为空")
    @Size(max = 30,message = "密码过长")
    private String password;
}
