package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.question.QuestionInfoDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.question.QuestionDto;
import com.example.eat.model.dto.res.question.QuestionRes;
import com.example.eat.model.dto.res.question.QuestionsGetRes;
import com.example.eat.model.po.question.QuestionInfo;
import com.example.eat.service.QuestionService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionInfoDao, QuestionInfo> implements QuestionService {
    @Override
    public CommonResult<QuestionRes> sentQuestion(QuestionDto questionDto) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        QuestionRes questionRes=new QuestionRes();
        try {
            //发送问题，接收答案


            String answer="";




            QuestionInfo questionInfo=new QuestionInfo();
            questionInfo.setUserId(userId);
            questionInfo.setQuestion(questionDto.getQuestion());
            questionInfo.setAnswer(answer);
            this.save(questionInfo);

            questionRes.setQuestion(questionDto.getQuestion());
            questionRes.setQuestion(answer);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("询问失败");
        }
        return CommonResult.success("询问成功",questionRes);
    }

    @Override
    public CommonResult<QuestionsGetRes> getQuestionHistory() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        QuestionsGetRes questionsGetRes;
        try{
            QueryWrapper<QuestionInfo> questionInfoQueryWrapper=new QueryWrapper<>();
            questionInfoQueryWrapper.eq("user_id",userId);
            questionInfoQueryWrapper.orderByDesc("time");
            questionsGetRes=new QuestionsGetRes(this.list(questionInfoQueryWrapper));
            questionsGetRes.setTotal(this.count(questionInfoQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查找历史问题失败");
        }
        return CommonResult.success("查找历史问题成功",questionsGetRes);
    }
}
