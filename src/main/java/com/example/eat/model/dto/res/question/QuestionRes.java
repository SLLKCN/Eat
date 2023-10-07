package com.example.eat.model.dto.res.question;

import com.example.eat.model.po.question.QuestionInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@NoArgsConstructor
@Data
public class QuestionRes {
    private Integer userId;
    private String question;
    private String answer;
    private Timestamp time;
    public QuestionRes(QuestionInfo questionInfo){
        this.userId= questionInfo.getUserId();
        this.question=questionInfo.getQuestion();
        this.answer=questionInfo.getAnswer();
        this.time=questionInfo.getTime();
    }
}
