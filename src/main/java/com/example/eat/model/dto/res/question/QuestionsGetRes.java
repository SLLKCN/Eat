package com.example.eat.model.dto.res.question;

import com.example.eat.model.po.question.QuestionInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class QuestionsGetRes {
    private Long total;
    private List<QuestionRes> questionResList=new ArrayList<>();
    public QuestionsGetRes(List<QuestionInfo> questionInfoList){
        for(QuestionInfo temp:questionInfoList){
            QuestionRes questionRes=new QuestionRes(temp);
            questionResList.add(questionRes);
        }
    }
}
