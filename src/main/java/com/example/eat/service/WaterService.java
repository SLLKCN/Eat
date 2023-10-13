package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.water.WaterRes;
import com.example.eat.model.dto.res.water.WaterWeekRes;
import com.example.eat.model.po.water.Water;

public interface WaterService extends IService<Water> {
    CommonResult<WaterRes> drinkWater();

    CommonResult<WaterRes> getWaterCup();

    CommonResult<WaterWeekRes> getThisweekWaterCup();
}
