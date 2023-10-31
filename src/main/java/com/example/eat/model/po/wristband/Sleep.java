package com.example.eat.model.po.wristband;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;

@Data
@TableName("sleep_info")
public class Sleep {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer sleepMinute;
    private Integer inBedMinute;
    @TableField(fill = FieldFill.INSERT,jdbcType = TIMESTAMP)
    private Timestamp date;
}
