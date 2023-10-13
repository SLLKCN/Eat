package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.water.WaterDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.water.WaterRes;
import com.example.eat.model.dto.res.water.WaterWeekRes;
import com.example.eat.model.po.water.Water;
import com.example.eat.service.WaterService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.TokenThreadLocalUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WaterServiceImpl extends ServiceImpl<WaterDao, Water> implements WaterService {
    @Override
    public CommonResult<WaterRes> drinkWater() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        WaterRes waterRes=new WaterRes();
        try{
            QueryWrapper<Water> waterQueryWrapper=new QueryWrapper<>();
            waterQueryWrapper.between("time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            Water water=this.getOne(waterQueryWrapper);
            //如果今日无饮水记录，则新建一个
            if(water==null){
                water=new Water();
                water.setUserId(userId);
                water.setCup(1);

                this.save(water);
                waterRes.setCup(water.getCup());
                return CommonResult.success("喝水成功",waterRes);
            }
            //如果有，cup加一
            water.setCup(water.getCup()+1);
            this.updateById(water);
            waterRes.setCup(water.getCup());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("喝水失败");
        }

        return CommonResult.success("喝水成功",waterRes);
    }

    @Override
    public CommonResult<WaterRes> getWaterCup() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        WaterRes waterRes=new WaterRes();
        try{
            QueryWrapper<Water> waterQueryWrapper=new QueryWrapper<>();
            waterQueryWrapper.between("time", LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            Water water=this.getOne(waterQueryWrapper);
            //如果今日无饮水记录，则新建一个
            if(water==null){
                water=new Water();
                water.setUserId(userId);
                water.setCup(0);

                this.save(water);
                waterRes.setCup(0);
                return CommonResult.success("查看饮水量成功",waterRes);
            }
            waterRes.setCup(water.getCup());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查看饮水量失败");
        }

        return CommonResult.success("查看饮水量成功",waterRes);
    }

    @Override
    public CommonResult<WaterWeekRes> getThisweekWaterCup() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        WaterWeekRes waterWeekRes;
        try{
            QueryWrapper<Water> waterQueryWrapper=new QueryWrapper<>();
            waterQueryWrapper.between("time", LocalDate.now().minusDays(6).atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
            List<Water> waterList=this.list(waterQueryWrapper);

            int sum=0;
            for (Water temp:waterList){
                sum+=temp.getCup();
            }
            waterWeekRes=new WaterWeekRes(sum);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查看饮水量失败");
        }

        return CommonResult.success("查看饮水量成功",waterWeekRes);
    }
}
