package com.example.eat.dao.music;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.po.music.Favourite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavouriteDao extends BaseMapper<Favourite> {
}
