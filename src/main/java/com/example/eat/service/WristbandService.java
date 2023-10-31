package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.wristband.FamilyCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.wristband.*;
import com.example.eat.model.po.wristband.Wristband;

import java.util.List;

public interface WristbandService extends IService<Wristband> {
    CommonResult<String> getUrl();

    CommonResult<BlankRes> setToken();

    CommonResult<BlankRes> recordWristbandData();

    CommonResult<ActivitiesGetRes> getActivities();

    CommonResult<SleepGetRes> getSleep();

    CommonResult<HeartrateGetRes> getHeartrate();

    CommonResult<TemperatureRes> getTemperature();

    CommonResult<Spo2Res> getSpo2();

    CommonResult<List<RecommendActivitieRes>> getRecommendActivitie();

    CommonResult<EncodeRes> getMyEncodeRes();

    CommonResult<ActivitiesGetRes> getFamilyActivities(String encode);

    CommonResult<SleepGetRes> getFamilySleep(String encode);

    CommonResult<HeartrateGetRes> getFamilyHeartrate(String encode);

    CommonResult<TemperatureRes> getFamilyTemperature(String encode);

    CommonResult<Spo2Res> getFamilySpo2(String encode);

    CommonResult<BlankRes> addFamily(FamilyCreateDto familyCreateDto);

    CommonResult<List<FamilyRes>> getFamily();

    CommonResult<CaloriesWeekRes> getWeekCalories();

    CommonResult<SleepWeekRes> getWeekSleep();

    CommonResult<BodyWeekRes> getWeekBody();

    CommonResult<BlankRes> getWristbandData();
}
