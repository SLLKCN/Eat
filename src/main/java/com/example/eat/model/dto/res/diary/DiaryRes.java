package com.example.eat.model.dto.res.diary;

import com.example.eat.model.po.diary.DiaryInfo;
import com.example.eat.model.po.diary.FoodInfo;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class DiaryRes {
    private Integer id;
    private String title;
    private String content;
    private String image;
    private String createTime;
    private Timestamp updateTime;
    private FoodInfoRes foodInfoRes;
    public DiaryRes(DiaryInfo diaryInfo, FoodInfo foodInfo){
        this.id=diaryInfo.getId();
        this.title= diaryInfo.getTitle();
        this.content= diaryInfo.getContent();
        this.image= diaryInfo.getImage();
        this.updateTime=diaryInfo.getUpdateTime();
        this.foodInfoRes=new FoodInfoRes(foodInfo);

        LocalDateTime timestamp = LocalDateTime.parse(diaryInfo.getCreateTime().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
        int hour = timestamp.getHour();
        int minute=timestamp.getMinute();
        createTime=hour+":"+minute;
    }
}
