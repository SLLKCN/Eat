package com.example.eat.controller;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.water.WaterRes;
import com.example.eat.model.dto.res.water.WaterWeekRes;
import com.example.eat.service.WaterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class WaterController {
    @Autowired
    WaterService waterService;

    @PutMapping("/water")
    CommonResult<WaterRes> drinkWater(){
        return waterService.drinkWater();
    }
    @GetMapping("/water")
    CommonResult<WaterRes> getWaterCup(){
        return waterService.getWaterCup();
    }
    @GetMapping("/water/thisweek")
    CommonResult<WaterWeekRes> getThisweekWaterCup(){
        return waterService.getThisweekWaterCup();
    }

}
