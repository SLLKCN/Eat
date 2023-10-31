package com.example.eat.model.dto.res.wristband;

import lombok.Data;

@Data
public class TemperatureRes {
    private Integer userid;
    private Double avg;
    private Double min;
    private Double max;
    private String date;
}
