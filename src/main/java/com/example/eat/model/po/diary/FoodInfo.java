package com.example.eat.model.po.diary;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("food_info")
public class FoodInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private String name;
    private String calorie;
}
