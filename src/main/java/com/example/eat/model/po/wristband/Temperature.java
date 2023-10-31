package com.example.eat.model.po.wristband;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;

@Data
@TableName("temperature_info")
public class Temperature {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Double avg;
    private Double min;
    private Double max;
    @TableField(fill = FieldFill.INSERT,jdbcType = TIMESTAMP)
    private Timestamp date;
}
