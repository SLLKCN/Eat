package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.question.QuestionDto;
import com.example.eat.model.dto.res.question.QuestionRes;
import com.example.eat.model.dto.res.question.QuestionsGetRes;
import com.example.eat.model.po.question.QuestionInfo;

public interface QuestionService extends IService<QuestionInfo> {
    CommonResult<QuestionRes> sentQuestion(QuestionDto questionDto);

    CommonResult<QuestionsGetRes> getQuestionHistory(Integer pageNum, Integer pageSize);
}
