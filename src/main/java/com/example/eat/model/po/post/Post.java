package com.example.eat.model.po.post;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;

@Data
@TableName("post_info")
public class Post {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private String title;

    private String content;

    private Integer likeCount;

    private Integer commentCount;

    @TableField(fill = FieldFill.INSERT,jdbcType = TIMESTAMP)
    private Timestamp createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE,jdbcType = TIMESTAMP)
    private Timestamp updateTime;
}
