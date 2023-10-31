package com.example.eat.dao.wristband;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.po.wristband.Temperature;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TemperatureDao extends BaseMapper<Temperature> {
}
