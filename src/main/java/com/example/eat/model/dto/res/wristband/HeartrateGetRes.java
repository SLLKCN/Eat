package com.example.eat.model.dto.res.wristband;

import com.example.eat.model.po.wristband.Wristband;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class HeartrateGetRes {
    private Integer userId;
    private Integer avg;
    private Integer min;
    private Integer max;
    List<HeartrateRes> heartrateResList=new ArrayList<>();
    @Data
    public class HeartrateRes{
        private Integer heartrate;
        private String hour;
    }
    public HeartrateGetRes(List<Wristband> wristbandList){
        int sum=0;
        int count=0;
        int max=0;
        int min=10000;

        for(Wristband temp:wristbandList){
            int heartrate=temp.getHeartrate();
            sum+=heartrate;
            count++;
            if(max<heartrate){
                max=heartrate;
            }
            if(min>heartrate){
                min=heartrate;
            }

            HeartrateRes heartrateRes=new HeartrateRes();
            heartrateRes.setHeartrate(heartrate);

            LocalDateTime timestamp = LocalDateTime.parse(temp.getTime().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
            int hour = timestamp.getHour();

            String hourString = hour+":00";
            heartrateRes.setHour(hourString);
            heartrateResList.add(heartrateRes);
        }
        sum=sum/count;
        this.avg=sum;
        this.max=max;
        this.min=min;
    }

}
