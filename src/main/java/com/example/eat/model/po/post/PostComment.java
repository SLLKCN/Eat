package com.example.eat.model.po.post;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;

/**
 * @author CZCZCZ
 * &#064;date  2023-08-05 11:54
 */

@Data
@TableName("post_comment_info")
public class PostComment {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private Integer postId;

    private String content;

    @TableField(fill = FieldFill.INSERT,jdbcType = TIMESTAMP)
    private Timestamp createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE,jdbcType = TIMESTAMP)
    private Timestamp updateTime;


}
