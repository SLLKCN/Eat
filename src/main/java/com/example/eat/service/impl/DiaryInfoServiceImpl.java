package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.diary.DiaryInfoDao;
import com.example.eat.dao.diary.FoodInfoDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.diary.DateDto;
import com.example.eat.model.dto.param.diary.DiaryCreateDto;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.diary.DiaryRes;
import com.example.eat.model.dto.res.diary.DiarysGetRes;
import com.example.eat.model.dto.res.diary.NutritionDayRes;
import com.example.eat.model.dto.res.diary.NutritionWeekRes;
import com.example.eat.model.po.diary.DiaryInfo;
import com.example.eat.model.po.diary.FoodInfo;
import com.example.eat.service.DiaryInfoService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
            QueryWrapper<FoodInfo> foodInfoQueryWrapper=new QueryWrapper<>();
            foodInfoQueryWrapper.eq("name",diaryCreateDto.getName());
            FoodInfo foodInfo=foodInfoDao.selectOne(foodInfoQueryWrapper);
            if(foodInfo==null){
                return CommonResult.fail("数据库中未记录该食品数据");
            }
            diaryInfo.setFoodId(foodInfo.getId());


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
            diaryInfoQueryWrapper.eq("user_id",userId);
            diaryInfoQueryWrapper.between("create_time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            diaryInfoQueryWrapper.orderByAsc("create_time");
            List<DiaryInfo> diaryInfoList=this.list(diaryInfoQueryWrapper);

            List<DiaryRes> diaryResList=new ArrayList<>();
            for(DiaryInfo temp:diaryInfoList){
                FoodInfo foodInfo=foodInfoDao.selectById(temp.getFoodId());
                DiaryRes diaryRes=new DiaryRes(temp,foodInfo);
                diaryResList.add(diaryRes);
            }
            diarysGetRes=new DiarysGetRes();
            diarysGetRes.setDiaryResList(diaryResList);
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
            diaryInfoQueryWrapper.eq("user_id",userId);
            diaryInfoQueryWrapper.between("create_time", dateDto.getDate().atStartOfDay(), dateDto.getDate().plusDays(1).atStartOfDay());
            diaryInfoQueryWrapper.orderByAsc("create_time");
            List<DiaryInfo> diaryInfoList=this.list(diaryInfoQueryWrapper);

            List<DiaryRes> diaryResList=new ArrayList<>();
            for(DiaryInfo temp:diaryInfoList){
                FoodInfo foodInfo=foodInfoDao.selectById(temp.getFoodId());
                DiaryRes diaryRes=new DiaryRes(temp,foodInfo);
                diaryResList.add(diaryRes);
            }
            diarysGetRes=new DiarysGetRes();
            diarysGetRes.setDiaryResList(diaryResList);
            diarysGetRes.setTotal(this.count(diaryInfoQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取指定日记失败");
        }
        return CommonResult.success("获取指定日记成功",diarysGetRes);
    }

    @Override
    public CommonResult<NutritionDayRes> getTodayNutrition() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        NutritionDayRes nutritionDayRes;
        try{
            QueryWrapper<DiaryInfo> diaryInfoQueryWrapper=new QueryWrapper<>();
            diaryInfoQueryWrapper.eq("user_id",userId);
            diaryInfoQueryWrapper.between("create_time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            List<DiaryInfo> diaryInfoList=this.list(diaryInfoQueryWrapper);

            int calorie=0;
            List<Integer> nutritionList=new ArrayList<>();
            if(diaryInfoList==null||diaryInfoList.size()==0){
                for(int i=0;i<6;i++){
                    nutritionList.add(0);
                }
            }else {
                int[] nutrition=new int[6];
                Arrays.fill(nutrition, 0);
                for(DiaryInfo temp:diaryInfoList){
                    FoodInfo foodInfo=foodInfoDao.selectById(temp.getFoodId());
                    calorie+=foodInfo.getCalorie();
                    nutrition[0]+=foodInfo.getCarbon();
                    nutrition[1]+=foodInfo.getProtein();
                    nutrition[2]+=foodInfo.getVitamin();
                    nutrition[3]+=foodInfo.getFat();
                    nutrition[4]+=foodInfo.getFiber();
                    nutrition[5]+=foodInfo.getMinerals();
                }
                for(int i=0;i<6;i++){
                    nutritionList.add(nutrition[i]);
                }
            }

            nutritionDayRes=new NutritionDayRes(nutritionList);
            nutritionDayRes.setUser_id(userId);
            nutritionDayRes.setCalorie(calorie);




        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取今日摄入失败");
        }
        return CommonResult.success("获取今日摄入成功",nutritionDayRes);
    }

    @Override
    public CommonResult<NutritionWeekRes> getThisWeekNutrition() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        NutritionWeekRes nutritionWeekRes;
        try{
            QueryWrapper<DiaryInfo> diaryInfoQueryWrapper=new QueryWrapper<>();
            diaryInfoQueryWrapper.eq("user_id",userId);
            diaryInfoQueryWrapper.between("create_time", LocalDate.now().minusDays(6).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            List<DiaryInfo> diaryInfoList=this.list(diaryInfoQueryWrapper);

            int[] nutrition=new int[6];
            Arrays.fill(nutrition,0);
            for(DiaryInfo temp:diaryInfoList){
                FoodInfo foodInfo=foodInfoDao.selectById(temp.getFoodId());
                nutrition[0]+=foodInfo.getCarbon();
                nutrition[1]+=foodInfo.getProtein();
                nutrition[2]+=foodInfo.getVitamin();
                nutrition[3]+=foodInfo.getFat();
                nutrition[4]+=foodInfo.getFiber();
                nutrition[5]+=foodInfo.getMinerals();
            }

            nutritionWeekRes=new NutritionWeekRes(nutrition);
            nutritionWeekRes.setUser_id(userId);




        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取每周摄入失败");
        }
        return CommonResult.success("获取每周摄入成功",nutritionWeekRes);
    }
}
