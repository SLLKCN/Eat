package com.example.eat.model.dto.res.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.eat.model.po.user.User;
import lombok.Data;

@Data
public class UserRes {
    private Integer id;
    private String nickname;
    private String avatar;
    private Integer sex;
    private Integer age;
    private Double height;
    private Double weight;
    private String identity;
    private String disease;
    private String signature;
    private String status;
    public UserRes(User user){
        this.id=user.getId();
        this.nickname= user.getNickname();
        this.avatar= user.getAvatar();
        this.sex= user.getSex();
        this.age= user.getAge();
        this.height= user.getHeight();
        this.weight= user.getWeight();
        this.identity= user.getIdentity();
        this.disease= user.getDisease();
        this.signature=user.getSignature();
        this.status=user.getStatus();
    }
}
