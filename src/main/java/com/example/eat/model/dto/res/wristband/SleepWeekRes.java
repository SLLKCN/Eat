package com.example.eat.model.dto.res.wristband;

import com.example.eat.model.po.wristband.Sleep;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class SleepWeekRes {
    private List<SleepRes> sleepResList=new ArrayList<>();
    private Integer score;
    @Data
    public class SleepRes{
        private Integer sleepMinute;
        private String date;
        public SleepRes(Sleep sleep){
            this.sleepMinute=sleep.getSleepMinute();

            LocalDateTime timestamp = LocalDateTime.parse(sleep.getDate().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));

            this.date=timestamp.getMonthValue()+"."+timestamp.getDayOfMonth();
        }
    }
    public SleepWeekRes(List<Sleep> sleepList){
        int sum=0;
        for(Sleep temp:sleepList){
            SleepWeekRes.SleepRes sleepRes=new SleepWeekRes.SleepRes(temp);
            sleepResList.add(sleepRes);
            sum+=sleepRes.getSleepMinute();
        }
        this.score=sum*100/3780;
    }

}
