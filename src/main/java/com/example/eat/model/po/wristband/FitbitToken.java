package com.example.eat.model.po.wristband;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fitbit_token")
public class FitbitToken {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String encode;
    private String fitbitId;
    private String accessToken;

    private String code;
}
