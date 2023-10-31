package com.example.eat.model.po.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_info")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String account;
    private String nickname;
    private String password;
    private String telephone;
    private String avatar;
    private Integer sex;
    private Integer age;
    private Double height;
    private Double weight;
    private String identity;
    private String disease;
    private String signature;
    private String status;
}
