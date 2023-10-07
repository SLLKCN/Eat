package com.example.eat.model.dto.res.diary;

import com.example.eat.model.po.diary.DiaryInfo;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class DiaryRes {
    private Integer id;
    private String title;
    private String content;
    private String image;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Double vitamin;
    public DiaryRes(DiaryInfo diaryInfo){
        this.id=diaryInfo.getId();
        this.title= diaryInfo.getTitle();
        this.content= diaryInfo.getContent();
        this.image= diaryInfo.getImage();
        this.createTime=diaryInfo.getCreateTime();
        this.updateTime=diaryInfo.getUpdateTime();
        this.vitamin=diaryInfo.getVitamin();
    }
}
