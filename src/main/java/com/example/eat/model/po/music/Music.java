package com.example.eat.model.po.music;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("music_info")
public class Music {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String introduction;
    private String image;
    private String music;
    private Integer favouriteCount;
}
