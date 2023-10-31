package com.example.eat.model.po.wristband;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;


@Data
@TableName("wristband_info")
public class Wristband {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    @TableField(fill = FieldFill.INSERT,jdbcType = TIMESTAMP)
    private Timestamp time;
    private Integer step;
    private Integer calories;
    private Integer heartrate;
    private Integer distance;

}
