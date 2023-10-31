package com.example.eat.model.dto.res.wristband;

import com.example.eat.model.po.wristband.Wristband;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class ActivitiesGetRes {
    private Integer userId;
    private Integer step;
    private Integer distance;
    private Integer calories;
    private List<ActivitiesRes> activitiesResList=new ArrayList<>();
    @Data
    public class ActivitiesRes{

        private Integer calories;
        private String hour;

    }

    public ActivitiesGetRes(List<Wristband> wristbandList){
        int len=wristbandList.size();
        for (int i = 0; i < len; i++) {
            Wristband temp=wristbandList.get(i);
            ActivitiesRes activitiesRes=new ActivitiesRes();
            LocalDateTime timestamp = LocalDateTime.parse(temp.getTime().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
            int hour = timestamp.getHour();

            String hourString = hour+":00";
            activitiesRes.setHour(hourString);
            if(i==0){
                activitiesRes.setCalories(temp.getCalories());
            }else {
                activitiesRes.setCalories(temp.getCalories()-wristbandList.get(i-1).getCalories());
            }
            activitiesResList.add(activitiesRes);
        }
    }

}
