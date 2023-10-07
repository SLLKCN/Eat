package com.example.eat.dao.diary;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.po.diary.FoodInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodInfoDao extends BaseMapper<FoodInfo> {
}
