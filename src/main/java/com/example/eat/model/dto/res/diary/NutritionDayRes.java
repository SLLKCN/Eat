package com.example.eat.model.dto.res.diary;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NutritionDayRes {
    private Integer user_id;
    private Integer calorie;
    private List<Nutrition> data=new ArrayList<>();
    private String analyze="";
    private String advice="";
    private String recommend="";
    @Data
    @AllArgsConstructor
    public class Nutrition{
        private String name;
        private Integer value;
    }
    public NutritionDayRes(List<Integer> nutritionList){
        int i=0;
        data.add(new Nutrition("碳水化合物",nutritionList.get(i)));
        if(data.get(i).getValue()>1000){
            analyze+="碳水摄入过多\n";
            advice+="建议减少碳水摄入\n";
        }else if(data.get(i).getValue()<500){
            analyze+="碳水摄入不足\n";
            advice+="建议多摄入碳水含量高的食物\n";
            recommend+="羊肉泡馍 牛肉面 ";
        }

        data.add(new Nutrition("蛋白质",nutritionList.get(++i)));
        if(data.get(i).getValue()>500){
            analyze+="蛋白质摄入过多\n";
            advice+="建议减少蛋白质摄入\n";
        }else if(data.get(i).getValue()<250){
            analyze+="蛋白质摄入不足\n";
            advice+="建议多摄入蛋白质含量高的食物\n";
            recommend+="牛肉干 鸡蛋 ";
        }

        data.add(new Nutrition("维生素",nutritionList.get(++i)));
        if(data.get(i).getValue()>200){
            analyze+="维生素摄入过多\n";
            advice+="建议减少维生素摄入\n";
        }else if(data.get(i).getValue()<100){
            analyze+="维生素摄入不足\n";
            advice+="建议多摄入维生素含量高的食物\n";
            recommend+="橙子 柠檬汁 ";
        }

        data.add(new Nutrition("脂肪",nutritionList.get(++i)));
        if(data.get(i).getValue()>100){
            analyze+="脂肪摄入过多\n";
            advice+="建议减少脂肪摄入\n";
        }else if(data.get(i).getValue()<50){
            analyze+="脂肪摄入不足\n";
            advice+="建议多摄入脂肪含量高的食物\n";
            recommend+="把子肉 红烧肉 ";
        }

        data.add(new Nutrition("膳食纤维",nutritionList.get(++i)));
        if(data.get(i).getValue()>50){
            analyze+="膳食纤维摄入过多\n";
            advice+="建议减少膳食纤维摄入\n";
        }else if(data.get(i).getValue()<25){
            analyze+="膳食纤维摄入不足\n";
            advice+="建议多摄入膳食纤维含量高的食物\n";
            recommend+="烤红薯 香蕉 ";
        }

        data.add(new Nutrition("矿物质",nutritionList.get(++i)));
        if(data.get(i).getValue()>20){
            analyze+="矿物质摄入过多\n";
            advice+="建议减少矿物质摄入\n";
        }else if(data.get(i).getValue()<10){
            analyze+="矿物质摄入不足\n";
            advice+="建议多摄入矿物质含量高的食物\n";
            recommend+="坚果 鸡蛋";
        }
    }
}
