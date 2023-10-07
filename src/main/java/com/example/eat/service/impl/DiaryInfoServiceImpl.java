package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.diary.DiaryInfoDao;
import com.example.eat.dao.diary.FoodInfoDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.diary.DateDto;
import com.example.eat.model.dto.param.diary.DiaryCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.diary.DiarysGetRes;
import com.example.eat.model.po.diary.DiaryInfo;
import com.example.eat.service.DiaryInfoService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Service
public class DiaryInfoServiceImpl extends ServiceImpl<DiaryInfoDao, DiaryInfo> implements DiaryInfoService {

    @Autowired
    FoodInfoDao foodInfoDao;

    @Override
    public CommonResult<BlankRes> addDiary(DiaryCreateDto diaryCreateDto) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            DiaryInfo diaryInfo=new DiaryInfo();
            diaryInfo.setUserId(userId);
            diaryInfo.setTitle(diaryCreateDto.getTitle());
            diaryInfo.setContent(diaryCreateDto.getContent());
            diaryInfo.setImage(diaryCreateDto.getImage());

            //根据算法获取食物数据



            this.save(diaryInfo);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("创建日记失败");
        }
        return CommonResult.success("创建日记成功");
    }

    @Override
    public CommonResult<DiarysGetRes> getTodayDiary() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        DiarysGetRes diarysGetRes;
        try{
            QueryWrapper<DiaryInfo> diaryInfoQueryWrapper=new QueryWrapper<>();
            diaryInfoQueryWrapper.between("creat_time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            List<DiaryInfo> diaryInfoList=this.list(diaryInfoQueryWrapper);
            diarysGetRes=new DiarysGetRes(diaryInfoList);
            diarysGetRes.setTotal(this.count(diaryInfoQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取今日日记失败");
        }
        return CommonResult.success("获取今日日记成功",diarysGetRes);
    }

    @Override
    public CommonResult<DiarysGetRes> getDiaryByData(DateDto dateDto) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        DiarysGetRes diarysGetRes;
        try{
            QueryWrapper<DiaryInfo> diaryInfoQueryWrapper=new QueryWrapper<>();
            diaryInfoQueryWrapper.between("creat_time", dateDto.getDate().atStartOfDay(), dateDto.getDate().plusDays(1).atStartOfDay());
            List<DiaryInfo> diaryInfoList=this.list(diaryInfoQueryWrapper);
            diarysGetRes=new DiarysGetRes(diaryInfoList);
            diarysGetRes.setTotal(this.count(diaryInfoQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取指定日记失败");
        }
        return CommonResult.success("获取指定日记成功",diarysGetRes);
    }
}
