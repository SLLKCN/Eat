package com.example.eat.controller;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.diary.DiaryCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.diary.DiarysGetRes;
import com.example.eat.model.dto.res.diary.NutritionDayRes;
import com.example.eat.model.dto.res.diary.NutritionWeekRes;
import com.example.eat.service.DiaryInfoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
    CommonResult<DiarysGetRes> getDiaryByData(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date){
        return diaryInfoService.getDiaryByData(date);
    }

    @GetMapping("/nutrition/today")
    CommonResult<NutritionDayRes> getTodayNutrition(){
        return diaryInfoService.getTodayNutrition();
    }

    @GetMapping("/nutrition/thisweek")
    CommonResult<NutritionWeekRes> getThisWeekNutrition(){
        return diaryInfoService.getThisWeekNutrition();
    }

    @PutMapping("/diarys/{diaryId}")
    CommonResult<BlankRes> updateDiary(@PathVariable("diaryId")@NotBlank(message = "菜谱号不能为空") String diaryId,
                                       @RequestBody DiaryCreateDto diaryCreateDto){
        return diaryInfoService.updateDiary(Integer.parseInt(diaryId),diaryCreateDto);
    }

    @DeleteMapping("/diarys/{diaryId}")
    CommonResult<BlankRes> deleteDiary(@PathVariable("diaryId")@NotBlank(message = "菜谱号不能为空") String diaryId){
        return diaryInfoService.deleteDiary(Integer.parseInt(diaryId));
    }
}
