package com.example.eat.model.dto.res.wristband;

import com.example.eat.model.po.wristband.Spo2;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Data
public class BodyWeekRes {
    private List<BodyRes> bodyResList=new ArrayList<>();
    private Integer score;
    @Data
    public class BodyRes{
        private Integer heartrate;
        private Double sqo2;
        private String date;
        public BodyRes(Integer heartrate,Spo2 spo2){
            this.heartrate=heartrate;

            LocalDateTime timestamp = LocalDateTime.parse(spo2.getDate().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
            String formattedDate=timestamp.getMonthValue()+"."+timestamp.getDayOfMonth();
            this.sqo2=spo2.getAvg();
            this.date=formattedDate;
        }
    }
    public BodyWeekRes(List<Integer> heartrateList, List<Spo2> spo2){
        double sum=0;
        for(int i=0;i<7;i++){
            BodyRes bodyRes=new BodyRes(heartrateList.get(i),spo2.get(i));
            bodyResList.add(bodyRes);
            sum+=bodyRes.getSqo2();
        }
        this.score=(int)(sum*100/700);

    }


}
