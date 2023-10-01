package com.example.eat.model.dto.res.user;

import com.example.eat.model.po.user.User;
import lombok.Data;

@Data
public class UserRes {
    private Integer id;
    private String nickname;
    private String avatar;
    public UserRes(User user){
        this.id=user.getId();
        this.nickname= user.getNickname();
        this.avatar= user.getAvatar();
    }
}
