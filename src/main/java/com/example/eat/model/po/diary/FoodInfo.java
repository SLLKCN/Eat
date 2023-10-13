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
    private String name;
    private Integer calorie;
    private Integer vitamin;
    private Integer protein;
    private Integer carbon;
    private Integer fiber;
    private Integer fat;
    private Integer minerals;
}
