package com.example.eat.model.dto.param.wristband;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FamilyCreateDto {
    @NotNull(message = "备注不能为空")
    private String memo;
    @NotNull(message = "编码不能为空")
    private String encode;
}
