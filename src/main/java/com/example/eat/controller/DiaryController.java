package com.example.eat.controller;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.diary.DateDto;
import com.example.eat.model.dto.param.diary.DiaryCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.diary.DiarysGetRes;
import com.example.eat.service.DiaryInfoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class DiaryController {
    @Autowired
    DiaryInfoService diaryInfoService;
    @PostMapping("/diarys")
    CommonResult<BlankRes> addDiary(@Valid @RequestBody DiaryCreateDto diaryCreateDto){
        return diaryInfoService.addDiary(diaryCreateDto);
    }

    @GetMapping("/diarys/today")
    CommonResult<DiarysGetRes> getTodayDiary(){
        return diaryInfoService.getTodayDiary();
    }

    @GetMapping("/diarys")
    CommonResult<DiarysGetRes> getDiaryByData(@Valid @RequestBody DateDto dateDto){
        return diaryInfoService.getDiaryByData(dateDto);
    }
}
