package com.example.eat.controller;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.wristband.FamilyCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.wristband.*;
import com.example.eat.service.WristbandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class WristbandController {
    @Autowired
    WristbandService wristbandService;
    @GetMapping("/fitbit/url")
    CommonResult<String> getUrl(){
        return wristbandService.getUrl();
    }
    @PostMapping("/fitbit/token")
    CommonResult<BlankRes> setToken(){
        return wristbandService.setToken();
    }
    @PostMapping("/fitbit/wristband")
    CommonResult<BlankRes> recordWristbandData(){
        return wristbandService.recordWristbandData();
    }

    @GetMapping("/user/activities")
    CommonResult<ActivitiesGetRes> getActivities(){
        return wristbandService.getActivities();
    }

    @GetMapping("/user/sleep")
    CommonResult<SleepGetRes> getSleep(){
        return wristbandService.getSleep();
    }

    @GetMapping("/user/heartrate")
    CommonResult<HeartrateGetRes> getHeartrate(){
        return wristbandService.getHeartrate();
    }

    @GetMapping("/user/temperature")
    CommonResult<TemperatureRes> getTemperature(){
        return wristbandService.getTemperature();
    }

    @GetMapping("/user/spo2")
    CommonResult<Spo2Res> getSpo2(){
        return wristbandService.getSpo2();
    }

    @GetMapping("/user/activitie/recommend")
    CommonResult<List<RecommendActivitieRes>> getRecommendActivitie(){
        return wristbandService.getRecommendActivitie();
    }

    @GetMapping("/user/my/encode")
    CommonResult<EncodeRes> getMyEncodeRes(){
        return wristbandService.getMyEncodeRes();
    }

    @GetMapping("/user/family/activities")
    CommonResult<ActivitiesGetRes> getFamilyActivities(@NotNull(message = "手环编码不可为空") @RequestParam("encode") String encode){
        return wristbandService.getFamilyActivities(encode);
    }

    @GetMapping("/user/family/sleep")
    CommonResult<SleepGetRes> getFamilySleep(@NotNull(message = "手环编码不可为空") @RequestParam("encode") String encode){
        return wristbandService.getFamilySleep(encode);
    }

    @GetMapping("/user/family/heartrate")
    CommonResult<HeartrateGetRes> getFamilyHeartrate(@NotNull(message = "手环编码不可为空") @RequestParam("encode") String encode){
        return wristbandService.getFamilyHeartrate(encode);
    }

    @GetMapping("/user/family/temperature")
    CommonResult<TemperatureRes> getFamilyTemperature(@NotNull(message = "手环编码不可为空") @RequestParam("encode") String encode){
        return wristbandService.getFamilyTemperature(encode);
    }

    @GetMapping("/user/family/spo2")
    CommonResult<Spo2Res> getFamilySpo2(@NotNull(message = "手环编码不可为空") @RequestParam("encode") String encode){
        return wristbandService.getFamilySpo2(encode);
    }

    @PostMapping("/family")
    CommonResult<BlankRes> addFamily(@Valid@RequestBody FamilyCreateDto familyCreateDto){
        return wristbandService.addFamily(familyCreateDto);
    }

    @GetMapping("/family")
    CommonResult<List<FamilyRes>> getFamily(){
        return wristbandService.getFamily();
    }
    @GetMapping("/calories/week")
    CommonResult<CaloriesWeekRes> getWeekCalories(){
        return wristbandService.getWeekCalories();
    }
    @GetMapping("/sleep/week")
    CommonResult<SleepWeekRes> getWeekSleep(){
        return wristbandService.getWeekSleep();
    }
    @GetMapping("/body/week")
    CommonResult<BodyWeekRes> getWeekBody(){
        return wristbandService.getWeekBody();
    }

    @PostMapping("/wristband")
    CommonResult<BlankRes> getWristbandData(){
        return wristbandService.getWristbandData();
    }
}
