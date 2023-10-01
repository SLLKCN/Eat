package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.cookbook.PostCookbook;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.cookbook.CookbookCollectRes;
import com.example.eat.model.dto.res.cookbook.CookbooksGetRes;
import com.example.eat.model.po.cookbook.Cookbook;
import org.springframework.web.multipart.MultipartFile;

public interface CookbookService extends IService<Cookbook> {

    CommonResult<CookbooksGetRes> getPersonalizeCookbooks(Integer pageNum, Integer pageSize, String type);

    CommonResult<CookbooksGetRes> getTodayCookbooks(Integer pageNum, Integer pageSize, String type);

    CommonResult<CookbooksGetRes> getCollectionCookbooks(Integer pageNum, Integer pageSize);

    CommonResult<CookbooksGetRes> searchCookbooks(Integer pageNum, Integer pageSize, String keyword);

    CommonResult<BlankRes> collectCookbook(Integer isCollect,Integer cookbookId);


    CommonResult<BlankRes> addCookbook(PostCookbook postCookbook);

    CommonResult<BlankRes> insertImage(Integer cookbookid,MultipartFile file);

    CommonResult<CookbookCollectRes> getCollect(Integer cookbookId);
}
