package com.example.eat.model.dto.res.cookbook;

import com.example.eat.model.po.cookbook.Cookbook;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CookbooksGetRes {
    private Long total;
    private List<CookbookRes> cookbookResList = new ArrayList<>();
    public CookbooksGetRes(List<Cookbook> cookbookList){
        for (Cookbook cookbook:cookbookList) {
            CookbookRes cookbookRes=new CookbookRes(cookbook);
            this.cookbookResList.add(cookbookRes);
        }
    }

}
