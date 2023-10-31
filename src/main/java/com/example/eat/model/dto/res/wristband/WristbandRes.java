package com.example.eat.model.dto.res.wristband;

import com.example.eat.model.po.wristband.Wristband;
import lombok.Data;

import java.sql.Timestamp;
@Data
public class WristbandRes {
    private Integer userid;
    private Timestamp time;
    private Integer step;
    private Integer calories;
    private Integer heartrate;
    private Integer sleepMinute;
    private Integer distance;
    public WristbandRes(Wristband wristband){
        this.userid= wristband.getUserId();
        this.time=wristband.getTime();
        this.step= wristband.getStep();
        this.calories= wristband.getCalories();
        this.heartrate= wristband.getHeartrate();
        this.distance= wristband.getDistance();
    }
}
