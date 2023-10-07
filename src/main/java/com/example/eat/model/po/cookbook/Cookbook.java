package com.example.eat.model.po.cookbook;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("cookbook_info")
public class Cookbook {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String introduction;
    private String content;
    private String type;
    private String image;
    private Integer collectCount;
}
