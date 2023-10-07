package com.example.eat.model.po.water;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;

@Data
@TableName("water_record")
public class Water {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer cup;
    @TableField(fill = FieldFill.INSERT,jdbcType = TIMESTAMP)
    private Timestamp time;
}
