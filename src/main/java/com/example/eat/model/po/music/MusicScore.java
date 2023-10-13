package com.example.eat.model.po.music;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("music_score")
public class MusicScore {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer musicId;
    private Integer score;
}
