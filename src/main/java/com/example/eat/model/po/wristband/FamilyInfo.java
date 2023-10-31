package com.example.eat.model.po.wristband;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("family_info")
public class FamilyInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String familyEncode;
    private String memo;
}
