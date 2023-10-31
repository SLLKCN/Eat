package com.example.eat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.wristband.*;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.wristband.FamilyCreateDto;
import com.example.eat.model.dto.param.wristband.FitBit;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.param.wristband.TokenResponseDTO;
import com.example.eat.model.dto.res.wristband.*;
import com.example.eat.model.po.wristband.*;
import com.example.eat.service.WristbandService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class WristbandServiceImpl extends ServiceImpl<WristbandDao, Wristband> implements WristbandService {
    @Autowired
    FitbitTokenDao fitbitTokenDao;
    @Autowired
    Spo2Dao spo2Dao;
    @Autowired
    TemperatureDao temperatureDao;
    @Autowired
    SleepDao sleepDao;
    @Autowired
    FamilyInfoDao familyInfoDao;
    @Override
    public CommonResult<String> getUrl() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        FitBit fitBit=new FitBit();
        String redirectURL="";
        try {


            byte[] bytes = fitBit.getCodeVerifier().getBytes(StandardCharsets.US_ASCII);
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest(bytes);
            Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
            fitBit.setCodeChallenge(encoder.encodeToString(digest));


            HttpClient httpClient = HttpClients.createDefault();
            String postData ="client_id="+fitBit.getClientId()+"&response_type=code&code_challenge="+fitBit.getCodeChallenge()+"&code_challenge_method=S256&scope=activity+heartrate+location+nutrition+oxygen_saturation+profile+respiratory_rate+settings+sleep+social+temperature+weight&state&prompt=none";
            HttpPost httpPost = new HttpPost(fitBit.getUserAuthorizationUri());
            // 设置POST请求的Content-Type
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            // 设置POST请求的参数
            StringEntity entity = new StringEntity(postData, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode==302){
                Header locationHeader = response.getFirstHeader("Location");
                if (locationHeader != null) {
                    redirectURL = locationHeader.getValue();
                    System.out.println("==================以下为fitbit服务信息===============");
                    System.out.println("重定向的路径：" + redirectURL);
                    fitBit.setCode(redirectURL.substring(redirectURL.indexOf('=')+1).split("#")[0]);
                    System.out.println("Authorize为"+fitBit.getCode());

                } else {
                    System.out.println("未找到重定向路径");
                    return CommonResult.fail("未找到重定向路径");
                }

            }else {
                log.error("fitbit获取code失败");
                return CommonResult.fail("fitbit获取code失败");
            }
        }catch (Exception e){
            log.error("获取fitbit的token失败！");
            e.printStackTrace();
            return CommonResult.fail("获取fitbit的token失败");
        }
        return CommonResult.success("获取url成功",redirectURL);
    }

    @Override
    public CommonResult<BlankRes> setToken() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper1=new QueryWrapper<>();
            fitbitTokenQueryWrapper1.eq("user_id",userId);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper1);
            if(fitbitToken==null){
                log.warn("未设置用户手环信息");
                return CommonResult.fail("未设置用户手环信息");
            }
            String code=fitbitToken.getCode();
            if(code==null){
                log.warn("未设置code");
                return CommonResult.fail("未设置code");
            }
            fitbitToken=null;
            FitBit fitBit=new FitBit();
            String tokenPostData = "client_id="+fitBit.getClientId()+"&code="+code+"&code_verifier="+fitBit.getCodeVerifier()+"&grant_type=authorization_code"  ;


            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://api.fitbit.com/oauth2/token");

            // 设置请求头

            httpPost.setHeader("Authorization","Basic "+ Base64.getEncoder().encodeToString((fitBit.getClientId()+":"+fitBit.getClientSecret()).getBytes(StandardCharsets.UTF_8)));
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

            StringEntity tokenEntity = new StringEntity(tokenPostData, StandardCharsets.UTF_8);
            httpPost.setEntity(tokenEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);

            TokenResponseDTO tokenResponseDTO= JSONObject.parseObject(responseBody,TokenResponseDTO.class);

            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("user_id",userId);
            fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            //若没有手环token记录，添加一个
            if(fitbitToken==null){
                fitbitToken=new FitbitToken();
                fitbitToken.setUserId(userId);
                fitbitToken.setAccessToken(tokenResponseDTO.getAccessToken());
                fitbitToken.setFitbitId(tokenResponseDTO.getUserId());
                UUID uuid = UUID.randomUUID();
                fitbitToken.setEncode(uuid.toString());
                fitbitTokenDao.insert(fitbitToken);
            }else {
                //若有手环token，更新token
                fitbitToken.setAccessToken(tokenResponseDTO.getAccessToken());
                fitbitToken.setFitbitId(tokenResponseDTO.getUserId());
            }

            fitbitTokenDao.updateById(fitbitToken);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取token失败");
        }
        return CommonResult.success("获取token成功");
    }

    @Override
    public CommonResult<BlankRes> recordWristbandData() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("user_id",userId);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            //
            if(fitbitToken==null){
                return CommonResult.fail("未连接fitbit手环");
            }
            String token=fitbitToken.getAccessToken();








        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取手环信息失败");
        }
        return CommonResult.success("获取手环信息成功");
    }

    @Override
    public CommonResult<ActivitiesGetRes> getActivities() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        ActivitiesGetRes activitiesGetRes;
        try{
            QueryWrapper<Wristband> wristbandQueryWrapper=new QueryWrapper<>();
            wristbandQueryWrapper.eq("user_id",userId);
            wristbandQueryWrapper.between("time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            wristbandQueryWrapper.orderByAsc("time");

            List<Wristband> wristbandList=this.list(wristbandQueryWrapper);
            if(wristbandList.size()==0||wristbandList==null){
                return CommonResult.fail("未获取到数据");
            }
            activitiesGetRes=new ActivitiesGetRes(wristbandList);

            Wristband lastest=wristbandList.get(wristbandList.size()-1);
            activitiesGetRes.setStep(lastest.getStep());
            activitiesGetRes.setDistance(lastest.getDistance());
            activitiesGetRes.setCalories(lastest.getCalories());
            activitiesGetRes.setUserId(userId);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取活动失败");
        }
        return CommonResult.success("获取活动成功",activitiesGetRes);
    }

    @Override
    public CommonResult<SleepGetRes> getSleep() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        SleepGetRes sleepGetRes;

        try{

            QueryWrapper<Sleep> sleepQueryWrapper=new QueryWrapper<>();
            sleepQueryWrapper.eq("user_id",userId);
            sleepQueryWrapper.between("date", LocalDate.now().minusDays(6).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            sleepQueryWrapper.orderByDesc("date");

            List<Sleep> sleepList=sleepDao.selectList(sleepQueryWrapper);
            if(sleepList==null||sleepList.size()==0){
                return CommonResult.fail("未记录数据");
            }
            sleepGetRes=new SleepGetRes(sleepList);
            sleepGetRes.setUserid(userId);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取睡眠失败");
        }
        return CommonResult.success("获取睡眠成功", sleepGetRes);
    }

    @Override
    public CommonResult<HeartrateGetRes> getHeartrate() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        HeartrateGetRes heartrateGetRes;
        try{
            QueryWrapper<Wristband> wristbandQueryWrapper=new QueryWrapper<>();
            wristbandQueryWrapper.eq("user_id",userId);
            wristbandQueryWrapper.between("time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            wristbandQueryWrapper.orderByDesc("time");

            List<Wristband> wristbandList=this.list(wristbandQueryWrapper);
            if(wristbandList==null||wristbandList.size()==0){
                return CommonResult.fail("未记录数据");
            }
            heartrateGetRes=new HeartrateGetRes(wristbandList);
            heartrateGetRes.setUserId(userId);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取心率失败");
        }
        return CommonResult.success("获取心率成功",heartrateGetRes);
    }

    @Override
    public CommonResult<TemperatureRes> getTemperature() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        TemperatureRes temperatureRes=new TemperatureRes();
        try{
            QueryWrapper<Temperature> temperatureQueryWrapper=new QueryWrapper<>();
            temperatureQueryWrapper.eq("user_id",userId);
            temperatureQueryWrapper.between("date", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            Temperature temperature=temperatureDao.selectOne(temperatureQueryWrapper);
            if (temperature==null){
                return CommonResult.fail("未记录数据");
            }
            temperatureRes.setUserid(temperature.getUserId());
            temperatureRes.setAvg(temperature.getAvg());
            temperatureRes.setMin(temperature.getMin());
            temperatureRes.setMax(temperature.getMax());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取温度失败");
        }
        return CommonResult.success("获取温度成功",temperatureRes);
    }

    @Override
    public CommonResult<Spo2Res> getSpo2() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        Spo2Res spo2Res=new Spo2Res();
        try{
            QueryWrapper<Spo2> spo2QueryWrapper=new QueryWrapper<>();
            spo2QueryWrapper.eq("user_id",userId);
            spo2QueryWrapper.between("date", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            Spo2 spo2=spo2Dao.selectOne(spo2QueryWrapper);
            if(spo2==null){
                return CommonResult.fail("未记录数据");
            }
            spo2Res.setUserid(spo2.getUserId());
            spo2Res.setAvg(spo2.getAvg());
            spo2Res.setMin(spo2.getMin());
            spo2Res.setMax(spo2.getMax());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取血氧比失败");
        }
        return CommonResult.success("获取血氧比成功",spo2Res);
    }

    @Override
    public CommonResult<List<RecommendActivitieRes>> getRecommendActivitie() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        List<RecommendActivitieRes> recommendActivitieRes=new ArrayList<>();
        try{
            QueryWrapper<Wristband> wristbandQueryWrapper=new QueryWrapper<>();
            wristbandQueryWrapper.between("time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            wristbandQueryWrapper.orderByDesc("time");

            List<Wristband> wristbandList=this.list(wristbandQueryWrapper);
            int heartrate=0;
            if(wristbandList.size()!=0||wristbandList!=null){
                heartrate=wristbandList.get(0).getHeartrate();
            }

            if(heartrate>90){
                RecommendActivitieRes temp1=new RecommendActivitieRes("骑车四公里，锻炼身体","消耗两百卡","a3b469ee-743b-469f-8a2e-507d0255caeaimage.jpeg");
                RecommendActivitieRes temp2=new RecommendActivitieRes("慢跑两公里，可以缓解压力","消耗两百卡","a3b469ee-743b-469f-8a2e-507d0255caeaimage.jpeg");
                RecommendActivitieRes temp3=new RecommendActivitieRes("跳绳五百个，消耗热量","消耗一百卡","a3b469ee-743b-469f-8a2e-507d0255caeaimage.jpeg");

                recommendActivitieRes.add(temp1);
                recommendActivitieRes.add(temp2);
                recommendActivitieRes.add(temp3);
            }else {
                RecommendActivitieRes temp1=new RecommendActivitieRes("散散步，放松一下心情","消耗两百卡","a3b469ee-743b-469f-8a2e-507d0255caeaimage.jpeg");
                RecommendActivitieRes temp2=new RecommendActivitieRes("开合跳，燃脂！","十分钟消耗一百大卡","a3b469ee-743b-469f-8a2e-507d0255caeaimage.jpeg");
                RecommendActivitieRes temp3=new RecommendActivitieRes("深蹲10*10","消耗八十卡","a3b469ee-743b-469f-8a2e-507d0255caeaimage.jpeg");

                recommendActivitieRes.add(temp1);
                recommendActivitieRes.add(temp2);
                recommendActivitieRes.add(temp3);
            }
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取运动建议失败");
        }
        return CommonResult.success("获取运动建议成功",recommendActivitieRes);
    }

    @Override
    public CommonResult<EncodeRes> getMyEncodeRes() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        EncodeRes encodeRes=new EncodeRes();
        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("user_id",userId);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            if(fitbitToken==null){
                return CommonResult.fail("未连接过手环");
            }
            encodeRes.setEncode(fitbitToken.getEncode());
        }catch (Exception e) {
            return CommonResult.fail("获取我的手环编码失败");
        }
        return CommonResult.success("获取我的手环编码成功",encodeRes);
    }

    @Override
    public CommonResult<ActivitiesGetRes> getFamilyActivities(String encode) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        ActivitiesGetRes activitiesGetRes;
        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("encode",encode);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            if(fitbitToken==null){
                return CommonResult.fail("手环编号错误");
            }

            QueryWrapper<Wristband> wristbandQueryWrapper=new QueryWrapper<>();
            wristbandQueryWrapper.eq("user_id",fitbitToken.getUserId());
            wristbandQueryWrapper.between("time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            wristbandQueryWrapper.orderByAsc("time");

            List<Wristband> wristbandList=this.list(wristbandQueryWrapper);
            if(wristbandList.size()==0||wristbandList==null){
                return CommonResult.fail("未获取到数据");
            }
            activitiesGetRes=new ActivitiesGetRes(wristbandList);

            Wristband lastest=wristbandList.get(0);
            activitiesGetRes.setStep(lastest.getStep());
            activitiesGetRes.setDistance(lastest.getDistance());
            activitiesGetRes.setCalories(lastest.getCalories());
            activitiesGetRes.setUserId(userId);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取活动失败");
        }
        return CommonResult.success("获取活动成功",activitiesGetRes);
    }

    @Override
    public CommonResult<SleepGetRes> getFamilySleep(String encode) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        SleepGetRes sleepGetRes;

        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("encode",encode);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            if(fitbitToken==null){
                return CommonResult.fail("手环编号错误");
            }

            QueryWrapper<Sleep> sleepQueryWrapper=new QueryWrapper<>();
            sleepQueryWrapper.eq("user_id",fitbitToken.getUserId());
            sleepQueryWrapper.between("date", LocalDate.now().minusDays(6).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            sleepQueryWrapper.orderByDesc("date");

            List<Sleep> sleepList=sleepDao.selectList(sleepQueryWrapper);
            if(sleepList==null||sleepList.size()==0){
                return CommonResult.fail("未记录数据");
            }
            sleepGetRes=new SleepGetRes(sleepList);
            sleepGetRes.setUserid(userId);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取睡眠失败");
        }
        return CommonResult.success("获取睡眠成功", sleepGetRes);
    }

    @Override
    public CommonResult<HeartrateGetRes> getFamilyHeartrate(String encode) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        HeartrateGetRes heartrateGetRes;
        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("encode",encode);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            if(fitbitToken==null){
                return CommonResult.fail("手环编号错误");
            }

            QueryWrapper<Wristband> wristbandQueryWrapper=new QueryWrapper<>();
            wristbandQueryWrapper.eq("user_id",fitbitToken.getUserId());
            wristbandQueryWrapper.between("time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            wristbandQueryWrapper.orderByDesc("time");

            List<Wristband> wristbandList=this.list(wristbandQueryWrapper);
            if(wristbandList==null||wristbandList.size()==0){
                return CommonResult.fail("未记录数据");
            }
            heartrateGetRes=new HeartrateGetRes(wristbandList);
            heartrateGetRes.setUserId(userId);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取心率失败");
        }
        return CommonResult.success("获取心率成功",heartrateGetRes);
    }

    @Override
    public CommonResult<TemperatureRes> getFamilyTemperature(String encode) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        TemperatureRes temperatureRes=new TemperatureRes();
        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("encode",encode);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            if(fitbitToken==null){
                return CommonResult.fail("手环编号错误");
            }

            QueryWrapper<Temperature> temperatureQueryWrapper=new QueryWrapper<>();
            temperatureQueryWrapper.eq("user_id",fitbitToken.getUserId());
            temperatureQueryWrapper.between("date", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            Temperature temperature=temperatureDao.selectOne(temperatureQueryWrapper);
            if (temperature==null){
                return CommonResult.fail("未记录数据");
            }
            temperatureRes.setUserid(temperature.getUserId());
            temperatureRes.setAvg(temperature.getAvg());
            temperatureRes.setMin(temperature.getMin());
            temperatureRes.setMax(temperature.getMax());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取温度失败");
        }
        return CommonResult.success("获取温度成功",temperatureRes);
    }

    @Override
    public CommonResult<Spo2Res> getFamilySpo2(String encode) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        Spo2Res spo2Res=new Spo2Res();
        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("encode",encode);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            if(fitbitToken==null){
                return CommonResult.fail("手环编号错误");
            }

            QueryWrapper<Spo2> spo2QueryWrapper=new QueryWrapper<>();
            spo2QueryWrapper.eq("user_id",fitbitToken.getUserId());
            spo2QueryWrapper.between("date", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            Spo2 spo2=spo2Dao.selectOne(spo2QueryWrapper);
            if(spo2==null){
                return CommonResult.fail("未记录数据");
            }
            spo2Res.setUserid(spo2.getUserId());
            spo2Res.setAvg(spo2.getAvg());
            spo2Res.setMin(spo2.getMin());
            spo2Res.setMax(spo2.getMax());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取血氧比失败");
        }
        return CommonResult.success("获取血氧比成功",spo2Res);
    }

    @Override
    public CommonResult<BlankRes> addFamily(FamilyCreateDto familyCreateDto) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("encode",familyCreateDto.getEncode());
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            if(fitbitToken==null){
                return CommonResult.fail("不存在该手环编号");
            }
            if(fitbitToken.getUserId()==userId){
                return CommonResult.fail("不能设置自己为家人");
            }
            QueryWrapper<FamilyInfo> familyInfoQueryWrapper=new QueryWrapper<>();
            familyInfoQueryWrapper.eq("user_id",userId);
            familyInfoQueryWrapper.eq("family_encode",fitbitToken.getEncode());
            FamilyInfo familyInfo=familyInfoDao.selectOne(familyInfoQueryWrapper);
            if(familyInfo!=null){
                return CommonResult.fail("设置为家人重复");
            }


            familyInfo=new FamilyInfo();
            familyInfo.setUserId(userId);
            familyInfo.setMemo(familyCreateDto.getMemo());
            familyInfo.setFamilyEncode(familyCreateDto.getEncode());
            familyInfoDao.insert(familyInfo);
        }catch (Exception e) {
            return CommonResult.fail("添加家人失败");
        }
        return CommonResult.success("添加家人成功");
    }

    @Override
    public CommonResult<List<FamilyRes>> getFamily() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        List<FamilyRes> familyResList=new ArrayList<>();
        try{
            QueryWrapper<FamilyInfo> familyInfoQueryWrapper=new QueryWrapper<>();
            familyInfoQueryWrapper.eq("user_id",userId);
            List<FamilyInfo> familyInfoList=familyInfoDao.selectList(familyInfoQueryWrapper);
            for(FamilyInfo temp:familyInfoList){
                FamilyRes familyRes=new FamilyRes(temp.getMemo(),temp.getFamilyEncode());
                familyResList.add(familyRes);
            }
        }catch (Exception e) {
            return CommonResult.fail("获取家人失败");
        }
        return CommonResult.success("获取家人成功",familyResList);
    }

    @Override
    public CommonResult<CaloriesWeekRes> getWeekCalories() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        CaloriesWeekRes caloriesWeekRes=new CaloriesWeekRes();;
        try{
            int sum=0;
            for (int i = 0; i < 7; i++) {
                QueryWrapper<Wristband> wristbandQueryWrapper=new QueryWrapper<>();
                wristbandQueryWrapper.eq("user_id",userId);
                wristbandQueryWrapper.between("time", LocalDate.now().minusDays(i).atStartOfDay(), LocalDate.now().minusDays(i).plusDays(1).atStartOfDay());
                wristbandQueryWrapper.orderByDesc("time");

                List<Wristband> wristbandList=this.list(wristbandQueryWrapper);
                if(wristbandList==null||wristbandList.size()==0){
                    continue;
                }
                sum+=wristbandList.get(0).getCalories();
            }
            sum/=7;
            caloriesWeekRes.setAvg(sum);
            caloriesWeekRes.setScore(sum*100/1000);

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取每周消耗卡路里失败");
        }
        return CommonResult.success("获取每周消耗卡路里成功",caloriesWeekRes);
    }

    @Override
    public CommonResult<SleepWeekRes> getWeekSleep() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        SleepWeekRes sleepWeekRes;

        try{

            QueryWrapper<Sleep> sleepQueryWrapper=new QueryWrapper<>();
            sleepQueryWrapper.eq("user_id",userId);
            sleepQueryWrapper.between("date", LocalDate.now().minusDays(6).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            sleepQueryWrapper.orderByDesc("date");

            List<Sleep> sleepList=sleepDao.selectList(sleepQueryWrapper);
            if(sleepList==null||sleepList.size()==0){
                return CommonResult.fail("未记录数据");
            }
            sleepWeekRes=new SleepWeekRes(sleepList);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取睡眠失败");
        }
        return CommonResult.success("获取睡眠成功", sleepWeekRes);
    }

    @Override
    public CommonResult<BodyWeekRes> getWeekBody() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        BodyWeekRes bodyWeekRes;
        try{
            List<Integer> heartrateList=new ArrayList<>();
            List<Spo2> spo2List=new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                QueryWrapper<Spo2> spo2QueryWrapper=new QueryWrapper<>();
                spo2QueryWrapper.eq("user_id",userId);
                spo2QueryWrapper.between("date", LocalDate.now().minusDays(i).atStartOfDay(), LocalDate.now().minusDays(i).plusDays(1).atStartOfDay());
                Spo2 spo2=spo2Dao.selectOne(spo2QueryWrapper);
                if(spo2==null){
                    spo2=new Spo2();
                    spo2.setAvg(0.0);
                    Timestamp timestamp = Timestamp.valueOf(LocalDate.now().minusDays(i).atStartOfDay());
                    spo2.setDate(timestamp);
                }
                spo2List.add(spo2);

                QueryWrapper<Wristband> wristbandQueryWrapper=new QueryWrapper<>();
                wristbandQueryWrapper.eq("user_id",userId);
                wristbandQueryWrapper.between("time", LocalDate.now().minusDays(i).atStartOfDay(), LocalDate.now().minusDays(i).plusDays(1).atStartOfDay());
                List<Wristband> wristbandList=this.list(wristbandQueryWrapper);
                Integer avg=0;
                int count=0;
                for(Wristband temp:wristbandList){
                    avg+=temp.getHeartrate();
                    count++;
                }
                if(count!=0){
                    avg/=count;
                }

                heartrateList.add(avg);
            }
            bodyWeekRes=new BodyWeekRes(heartrateList,spo2List);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取身体信息失败");
        }
        return CommonResult.success("获取身体信息成功",bodyWeekRes);
    }

    @Override
    public CommonResult<BlankRes> getWristbandData() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        try{
            QueryWrapper<FitbitToken> fitbitTokenQueryWrapper=new QueryWrapper<>();
            fitbitTokenQueryWrapper.eq("user_id",userId);
            FitbitToken fitbitToken=fitbitTokenDao.selectOne(fitbitTokenQueryWrapper);
            String token="Bearer "+fitbitToken.getAccessToken();
            String user_id=fitbitToken.getFitbitId();
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String date = currentDate.format(formatter);
            String period="1d";



            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost("https://api.fitbit.com/1/user/"+user_id+"/activities/date/"+date+".json");

            // 设置请求头

            httpPost.setHeader("Authorization",token);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // 获取summary的steps
            int steps = jsonNode.get("summary").get("steps").asInt();

            // 获取distances中activity为total的distance
            JsonNode distancesNode = jsonNode.get("summary").get("distances");
            double totalDistance = 0.0;
            for (JsonNode distanceNode : distancesNode) {
                if (distanceNode.get("activity").asText().equals("total")) {
                    totalDistance = distanceNode.get("distance").asDouble();
                    break;
                }
            }

            // 获取caloriesOut
            int caloriesOut = jsonNode.get("summary").get("caloriesOut").asInt();

            System.out.println("Steps: " + steps);
            System.out.println("Total Distance: " + totalDistance);
            System.out.println("Calories Out: " + caloriesOut);



            httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet("https://api.fitbit.com/1/user/"+user_id+"/activities/heart/date/"+date+"/"+period+".json");

            // 设置请求头

            httpget.setHeader("Authorization",token);
            response = httpClient.execute(httpget);
            responseEntity = response.getEntity();
            responseBody = EntityUtils.toString(responseEntity);

            objectMapper = new ObjectMapper();
            jsonNode = objectMapper.readTree(responseBody);

            JsonNode activitiesHeartNode = jsonNode.get("activities-heart");
            int heartReateAvg=0;
            int minutesum=0;
            if (activitiesHeartNode!=null&&activitiesHeartNode.isArray()) {
                for (JsonNode node : activitiesHeartNode) {
                    JsonNode valueNode = node.get("value");
                    if (valueNode != null) {
                        JsonNode heartRateZonesNode = valueNode.get("heartRateZones");
                        if (heartRateZonesNode.isArray()) {
                            for (JsonNode zoneNode : heartRateZonesNode) {
                                JsonNode minutesNode = zoneNode.get("minutes");
                                JsonNode minNode = zoneNode.get("min");
                                JsonNode maxNode = zoneNode.get("max");
                                if (minutesNode != null&&minNode!=null&&maxNode!=null) {
                                    int minutes = minutesNode.asInt();
                                    int min=minNode.asInt();
                                    int max=maxNode.asInt();
                                    heartReateAvg+=(min+max)/2*minutes;
                                    minutesum+=minutes;
                                }
                            }
                        }
                    }
                }
            }
            if(minutesum!=0){
                heartReateAvg/=minutesum;
            }

            Wristband wristband=new Wristband();
            wristband.setUserId(userId);
            wristband.setStep(steps);
            wristband.setCalories(caloriesOut);
            wristband.setDistance((int)totalDistance);
            wristband.setHeartrate(heartReateAvg);
            this.save(wristband);


//            httpClient = HttpClients.createDefault();
//            httpPost = new HttpPost("https://api.fitbit.com/1.2/user/"+user_id+"/sleep/date/"+date+".json");
//
//            // 设置请求头
//
//            httpPost.setHeader("Authorization",token);
//            response = httpClient.execute(httpPost);
//            responseEntity = response.getEntity();
//            responseBody = EntityUtils.toString(responseEntity);
//
//            objectMapper = new ObjectMapper();
//            jsonNode = objectMapper.readTree(responseBody);
//            int totalMinutesAsleep = jsonNode.get("summary").get("totalMinutesAsleep").asInt();
//            int totalTimeInBed = jsonNode.get("summary").get("totalTimeInBed").asInt();
//
//            Sleep sleep = new Sleep();
//            sleep.setUserId(userId);
//            sleep.setInBedMinute(totalTimeInBed);
//            sleep.setSleepMinute(totalMinutesAsleep);
//            sleepDao.insert(sleep);
//
//
//            httpClient = HttpClients.createDefault();
//            httpPost = new HttpPost("https://api.fitbit.com/1/user/{user-id}/temp/core/date/{date}.json");
//
//            // 设置请求头
//
//            httpPost.setHeader("Authorization",token);
//            response = httpClient.execute(httpPost);
//            responseEntity = response.getEntity();
//            responseBody = EntityUtils.toString(responseEntity);
//
//            objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(responseBody);
//            JsonNode tempCoreNode = rootNode.get("tempCore");
//            JsonNode firstValueNode = tempCoreNode.get(0).get("value");
//            double firstValue = firstValueNode.asDouble();
//
//
//            httpClient = HttpClients.createDefault();
//            httpPost = new HttpPost("https://api.fitbit.com/1/user/"+user_id+"/spo2/date/"+date+".json");
//
//            // 设置请求头
//
//            httpPost.setHeader("Authorization",token);
//            response = httpClient.execute(httpPost);
//            responseEntity = response.getEntity();
//            responseBody = EntityUtils.toString(responseEntity);
//
//            objectMapper = new ObjectMapper();
//            jsonNode = objectMapper.readTree(responseBody);
//            Double spo2Avg = jsonNode.get("value").get("avg").asDouble();
//            Double spo2Max = jsonNode.get("value").get("max").asDouble();
//            Double spo2Min = jsonNode.get("value").get("min").asDouble();
//
//            QueryWrapper<Spo2> spo2QueryWrapper=new QueryWrapper<>();
//            spo2QueryWrapper.eq("user_id",userId);
//            spo2QueryWrapper.between("date", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
//            Spo2 spo2=spo2Dao.selectOne(spo2QueryWrapper);
//            if(spo2==null){
//                spo2=new Spo2();
//                spo2.setMin(spo2Min);
//                spo2.setAvg(spo2Avg);
//                spo2.setMax(spo2Max);
//                spo2Dao.insert(spo2);
//            }else {
//                spo2.setMin(spo2Min);
//                spo2.setAvg(spo2Avg);
//                spo2.setMax(spo2Max);
//                spo2Dao.updateById(spo2);
//            }

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("记录数据失败");
        }
        return CommonResult.success("记录数据成功");
    }


}
