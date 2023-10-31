package com.example.eat.model.dto.res.wristband;

import com.example.eat.model.po.wristband.Sleep;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
public class SleepGetRes {
    private Integer userid;
    private List<SleepRes> sleepResList=new ArrayList<>();
    private String quality="";
    private String tip="";
    @Data
    public class SleepRes{
        private Integer sleepMinute;
        private Integer inBedMinute;
        private Integer score;
        private String date;
        public SleepRes(Sleep sleep){
            this.sleepMinute=sleep.getSleepMinute();
            this.inBedMinute=sleep.getInBedMinute();
            this.score=this.sleepMinute*100/this.inBedMinute;

            LocalDateTime timestamp = LocalDateTime.parse(sleep.getDate().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
            String formattedDate=timestamp.getMonthValue()+"."+timestamp.getDayOfMonth();

            this.date=formattedDate;
        }
    }
    public SleepGetRes(List<Sleep> sleepList){
        for(Sleep temp:sleepList){
            SleepRes sleepRes=new SleepRes(temp);
            sleepResList.add(sleepRes);
        }
        int score=sleepResList.get(0).getScore();
        int sleepMinute=sleepResList.get(0).getSleepMinute();
        if(score<40){
            this.quality+="睡眠质量差";
        }else if(score<60){
            this.quality+="睡眠质量不好";
        }else if(score<80){
            this.quality+="睡眠质量中等";
        }
        else if (score<90){
            this.quality+="睡眠质量好";
        }else {
            this.quality+="睡眠质量极佳";
        }
        List<String> tips=new ArrayList<>();
        tips.add("睡前做拉伸运动、放松身体有助于睡眠哦");
        tips.add("睡前一杯温牛奶，有助于睡眠哦");
        tips.add("睡前就不要喝咖啡啦");
        tips.add("睡前看手机会影响睡眠哦");
        tips.add("洗一个舒服的热水澡，让身体放松下来");
        tips.add("听听轻音乐吧，放松一下心灵");

        Random random = new Random();
        int randomNumber = random.nextInt(6);
        this.tip=tips.get(randomNumber);
    }

}
