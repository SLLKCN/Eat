package com.example.eat.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.component.XfXhStreamClient;
import com.example.eat.config.XfXhConfig;
import com.example.eat.dao.question.QuestionInfoDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.question.MsgDTO;
import com.example.eat.model.dto.param.question.QuestionDto;
import com.example.eat.model.dto.res.question.QuestionRes;
import com.example.eat.model.dto.res.question.QuestionsGetRes;
import com.example.eat.model.po.question.QuestionInfo;
import com.example.eat.service.QuestionService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import com.example.eat.util.XfXhWebSocketListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class QuestionServiceImpl extends ServiceImpl<QuestionInfoDao, QuestionInfo> implements QuestionService {

    @Resource
    private XfXhStreamClient xfXhStreamClient;

    @Resource
    private XfXhConfig xfXhConfig;


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


            // 如果是无效字符串，则不对大模型进行请求
            if (StrUtil.isBlank(questionDto.getQuestion())) {
                return CommonResult.fail("无效问题，请重新输入");
            }
            // 获取连接令牌
            if (!xfXhStreamClient.operateToken(XfXhStreamClient.GET_TOKEN_STATUS)) {
                return CommonResult.fail("当前大模型连接数过多，请稍后再试");
            }

            // 创建消息对象
            MsgDTO msgDTO = MsgDTO.createUserMsg(questionDto.getQuestion());
            // 创建监听器
            XfXhWebSocketListener listener = new XfXhWebSocketListener();
            // 发送问题给大模型，生成 websocket 连接
            WebSocket webSocket = xfXhStreamClient.sendMsg(UUID.randomUUID().toString().substring(0, 10), Collections.singletonList(msgDTO), listener);
            if (webSocket == null) {
                // 归还令牌
                xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
                return CommonResult.fail("系统内部错误，请联系管理员");
            }
            try {
                int count = 0;
                // 为了避免死循环，设置循环次数来定义超时时长
                int maxCount = xfXhConfig.getMaxResponseTime() * 5;
                while (count <= maxCount) {
                    Thread.sleep(200);
                    if (listener.isWsCloseFlag()) {
                        break;
                    }
                    count++;
                }
                if (count > maxCount) {
                    return CommonResult.fail("大模型响应超时，请联系管理员");
                }
                // 响应大模型的答案
                answer=listener.getAnswer().toString();


            } catch (InterruptedException e) {
                log.error("错误：" + e.getMessage());
                return CommonResult.fail("系统内部错误，请联系管理员");
            } finally {
                // 关闭 websocket 连接
                webSocket.close(1000, "");
                // 归还令牌
                xfXhStreamClient.operateToken(XfXhStreamClient.BACK_TOKEN_STATUS);
            }













            QuestionInfo questionInfo=new QuestionInfo();
            questionInfo.setUserId(userId);
            questionInfo.setQuestion(questionDto.getQuestion());
            questionInfo.setAnswer(answer);
            this.save(questionInfo);

            questionRes.setUserId(userId);
            questionRes.setQuestion(questionDto.getQuestion());
            questionRes.setAnswer(answer);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("询问失败");
        }
        return CommonResult.success("询问成功",questionRes);
    }

    @Override
    public CommonResult<QuestionsGetRes> getQuestionHistory(Integer pageNum, Integer pageSize) {
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
            Page<QuestionInfo> questionInfoPage=new Page<>(pageNum,pageSize);
            QueryWrapper<QuestionInfo> questionInfoQueryWrapper=new QueryWrapper<>();
            questionInfoQueryWrapper.eq("user_id",userId);
            questionInfoQueryWrapper.orderByDesc("time");
            IPage<QuestionInfo> questionInfoIPage=page(questionInfoPage,questionInfoQueryWrapper);
            questionsGetRes=new QuestionsGetRes(questionInfoIPage.getRecords());
            questionsGetRes.setTotal(questionInfoIPage.getTotal());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查找历史问题失败");
        }
        return CommonResult.success("查找历史问题成功",questionsGetRes);
    }
}
