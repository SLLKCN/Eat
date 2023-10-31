package com.example.eat.model.dto.res.diary;

import com.example.eat.model.po.diary.FoodInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FoodInfoRes {
    private String name;
    private Integer calorie;
    private List<Integer> nutritionList=new ArrayList<>();
    public FoodInfoRes(FoodInfo foodInfo){
        this.name=foodInfo.getName();
        this.calorie=foodInfo.getCalorie();
        nutritionList.add(foodInfo.getVitamin());
        nutritionList.add(foodInfo.getProtein());
        nutritionList.add(foodInfo.getFiber());
    }
}
