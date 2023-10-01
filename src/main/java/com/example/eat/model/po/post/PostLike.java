package com.example.eat.model.po.post;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author CZCZCZ
 * &#064;date  2023-08-05 11:54
 */
@Data
@TableName("post_like_info")
public class PostLike {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer postId;

}
