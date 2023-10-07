package com.example.eat.service;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.diary.DateDto;
import com.example.eat.model.dto.param.diary.DiaryCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.diary.DiarysGetRes;

public interface DiaryInfoService {
    CommonResult<BlankRes> addDiary(DiaryCreateDto diaryCreateDto);

    CommonResult<DiarysGetRes> getTodayDiary();

    CommonResult<DiarysGetRes> getDiaryByData(DateDto dateDto);
}
