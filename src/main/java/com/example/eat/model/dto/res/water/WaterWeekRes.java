package com.example.eat.model.dto.res.water;

import lombok.Data;

@Data
public class WaterWeekRes {
    private Integer cup;
    private Integer score;
    public WaterWeekRes(int cup){
        this.cup=cup;
        score=cup*100/56;
    }
}
