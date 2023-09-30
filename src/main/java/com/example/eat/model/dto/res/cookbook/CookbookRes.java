package com.example.eat.model.dto.res.cookbook;

import com.example.eat.model.po.cookbook.Cookbook;
import lombok.Data;

@Data
public class CookbookRes {
    CookbookRes(Cookbook cookbook){
        this.cookbookId=cookbook.getId();
        this.cookName=cookbook.getName();
        this.introduction=cookbook.getIntroduction();
        this.content=cookbook.getContent();
        this.type=cookbook.getType();
        this.image=cookbook.getImage();
    }
    private Integer cookbookId;
    private String cookName;
    private String introduction;
    private String content;
    private String type;
    private String image;
}
