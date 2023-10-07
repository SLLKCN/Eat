package com.example.eat.model.po.cookbook;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class CookbookScore {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer cookbookId;
    private Integer score;
}
