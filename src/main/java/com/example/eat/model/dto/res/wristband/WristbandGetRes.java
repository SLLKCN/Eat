package com.example.eat.model.dto.res.wristband;

import com.example.eat.model.po.wristband.Wristband;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WristbandGetRes {
    private Long total;
    private List<WristbandRes> wristbandResList=new ArrayList<>();
    public WristbandGetRes(List<Wristband> wristbandList){
        for(Wristband wristband:wristbandList){
            WristbandRes wristbandRes =new WristbandRes(wristband);
            wristbandResList.add(wristbandRes);
        }

    }
}
