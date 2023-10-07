package com.example.eat.model.po.question;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.sql.Timestamp;

import static org.apache.ibatis.type.JdbcType.TIMESTAMP;

@Data
@TableName("question_info")
public class QuestionInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;

    private String question;
    private String answer;
    @TableField(fill = FieldFill.INSERT,jdbcType = TIMESTAMP)
    private Timestamp time;
}
