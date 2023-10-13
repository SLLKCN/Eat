package com.example.eat.config.MP;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class TimeMetaObjectHandler implements MetaObjectHandler {


    @Override
    public void insertFill(org.apache.ibatis.reflection.MetaObject metaObject) {
        this.setFieldValByName("createTime",new Timestamp(System.currentTimeMillis()), metaObject);
        this.setFieldValByName("updateTime", new Timestamp(System.currentTimeMillis()), metaObject);
        this.setFieldValByName("time",new Timestamp(System.currentTimeMillis()), metaObject);
        this.setFieldValByName("date",new Timestamp(System.currentTimeMillis()), metaObject);
    }

    @Override
    public void updateFill(org.apache.ibatis.reflection.MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Timestamp(System.currentTimeMillis()), metaObject);
    }

}
