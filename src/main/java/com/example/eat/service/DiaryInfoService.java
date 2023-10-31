package com.example.eat.service;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.diary.DiaryCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.diary.DiarysGetRes;
import com.example.eat.model.dto.res.diary.NutritionDayRes;
import com.example.eat.model.dto.res.diary.NutritionWeekRes;

import java.time.LocalDate;

public interface DiaryInfoService {
    CommonResult<BlankRes> addDiary(DiaryCreateDto diaryCreateDto);

    CommonResult<DiarysGetRes> getTodayDiary();

    CommonResult<DiarysGetRes> getDiaryByData(LocalDate date);

    CommonResult<NutritionDayRes> getTodayNutrition();

    CommonResult<NutritionWeekRes> getThisWeekNutrition();

    CommonResult<BlankRes> updateDiary(Integer diaryId, DiaryCreateDto diaryCreateDto);

    CommonResult<BlankRes> deleteDiary(Integer diaryId);
}
