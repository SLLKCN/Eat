package com.example.eat.dao.cookbook;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.eat.model.po.cookbook.Collection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CollectionDao extends BaseMapper<Collection> {
}
