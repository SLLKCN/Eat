package com.example.eat.model.dto.res.wristband;

import lombok.Data;

@Data
public class Spo2Res {
    private Integer userid;
    private Double avg;
    private Double min;
    private Double max;
    private String date;
}
