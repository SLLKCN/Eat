package com.example.eat.model.dto.res.diary;

import com.example.eat.model.po.diary.DiaryInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiarysGetRes {
    private Long total;
    private List<DiaryRes> diaryResList=new ArrayList<>();
    public DiarysGetRes(List<DiaryInfo> diaryInfoList){
        for(DiaryInfo temp:diaryInfoList){
            DiaryRes diaryRes=new DiaryRes(temp);
            diaryResList.add(diaryRes);
        }
    }
}
