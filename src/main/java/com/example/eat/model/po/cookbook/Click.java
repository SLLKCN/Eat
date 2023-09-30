package com.example.eat.model.po.cookbook;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("cookbook_clicks")
public class Click {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer cookbookId;
    private Integer count;
}
