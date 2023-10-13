package com.example.eat.controller;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.question.QuestionDto;
import com.example.eat.model.dto.res.question.QuestionRes;
import com.example.eat.model.dto.res.question.QuestionsGetRes;
import com.example.eat.service.QuestionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @PostMapping("/questions")
    CommonResult<QuestionRes> sentQuestion(@Valid @RequestBody QuestionDto questionDto){
        return questionService.sentQuestion(questionDto);
    }
    @GetMapping("/questions/history")
    CommonResult<QuestionsGetRes> getQuestionHistory(@RequestParam(defaultValue = "1") Integer pageNum,
                                                     @RequestParam(defaultValue = "10") Integer pageSize){
        return questionService.getQuestionHistory(pageNum,pageSize);
    }
}
