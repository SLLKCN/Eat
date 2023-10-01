package com.example.eat.model.dto.param.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PutUser {
    @Size(max = 30,message = "昵称过长")
    private String nickname;
    private String avatar;
}
