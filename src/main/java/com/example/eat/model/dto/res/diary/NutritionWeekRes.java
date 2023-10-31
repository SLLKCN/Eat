package com.example.eat.model.dto.res.diary;

import lombok.Data;

@Data
public class NutritionWeekRes {
    private Integer user_id;
    private int[] data=new int[6];
    private String analyze="";
    private String advice="";
    private String recommend="";
    private Integer score=100;
    public NutritionWeekRes(int[] nutrition){
        int i=0;
        data[i]=nutrition[i];
        if(data[i]>1000){
            analyze+="碳水摄入过多\n";
            advice+="建议减少碳水摄入\n";
            score-=10;
        }else if(data[i]<500){
            analyze+="碳水摄入不足\n";
            advice+="建议多摄入碳水含量高的食物\n";
            recommend+="羊肉泡馍 牛肉面 ";
            score-=10;
        }
        data[i]=data[i]*100/800;

        data[++i]=nutrition[i];
        if(data[i]>500){
            analyze+="蛋白质摄入过多\n";
            advice+="建议减少蛋白质摄入\n";
            score-=10;
        }else if(data[i]<250){
            analyze+="蛋白质摄入不足\n";
            advice+="建议多摄入蛋白质含量高的食物\n";
            recommend+="牛肉干 鸡蛋 ";
            score-=10;
        }
        data[i]=data[i]*100/800;

        data[++i]=nutrition[i];
        if(data[i]>200){
            analyze+="维生素摄入过多\n";
            advice+="建议减少维生素摄入\n";
            score-=10;
        }else if(data[i]<100){
            analyze+="维生素摄入不足\n";
            advice+="建议多摄入维生素含量高的食物\n";
            recommend+="橙子 柠檬汁 ";
            score-=10;
        }
        data[i]=data[i]*100/800;

        data[++i]=nutrition[i];
        if(data[i]>100){
            analyze+="脂肪摄入过多\n";
            advice+="建议减少脂肪摄入\n";
            score-=10;
        }else if(data[i]<50){
            analyze+="脂肪摄入不足\n";
            advice+="建议多摄入脂肪含量高的食物\n";
            recommend+="把子肉 红烧肉 ";
            score-=10;
        }
        data[i]=data[i]*100/800;

        data[++i]=nutrition[i];
        if(data[i]>50){
            analyze+="膳食纤维摄入过多\n";
            advice+="建议减少膳食纤维摄入\n";
            score-=10;
        }else if(data[i]<25){
            analyze+="膳食纤维摄入不足\n";
            advice+="建议多摄入膳食纤维含量高的食物\n";
            recommend+="烤红薯 香蕉 ";
            score-=10;
        }
        data[i]=data[i]*100/800;

        data[++i]=nutrition[i];
        if(data[i]>20){
            analyze+="矿物质摄入过多\n";
            advice+="建议减少矿物质摄入\n";
            score-=10;
        }else if(data[i]<10){
            analyze+="矿物质摄入不足\n";
            advice+="建议多摄入矿物质含量高的食物\n";
            recommend+="坚果 鸡蛋";
            score-=10;
        }
        data[i]=data[i]*100/800;
    }
}
