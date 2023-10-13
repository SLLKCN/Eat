package com.example.eat.controller;

import com.example.eat.aop.Pass;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.cookbook.PostCookbook;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.cookbook.CollectionCountRes;
import com.example.eat.model.dto.res.cookbook.CookbookCollectRes;
import com.example.eat.model.dto.res.cookbook.CookbookRes;
import com.example.eat.model.dto.res.cookbook.CookbooksGetRes;
import com.example.eat.service.CookbookService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@CrossOrigin
public class CookbookController {
    @Autowired
    CookbookService cookbookService;

    @GetMapping("/cookbooks/personalize")
    CommonResult<CookbooksGetRes> getPersonalizeCookbooks(@RequestParam(defaultValue = "1") Integer pageNum,
                                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                                          @RequestParam(required = false) @Size(max = 30,message = "类型过长") String type){
        return cookbookService.getPersonalizeCookbooks(pageNum,pageSize,type);
    }
    @GetMapping("/cookbooks/today")
    CommonResult<CookbooksGetRes> getTodayCookbooks(@RequestParam(defaultValue = "1") Integer pageNum,
                                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                                    @RequestParam(required = false) @Size(max = 30,message = "类型过长") String type){
        return cookbookService.getTodayCookbooks(pageNum,pageSize,type);
    }
    @GetMapping("/cookbooks/collection")
    CommonResult<CookbooksGetRes> getCollectionCookbooks(@RequestParam(defaultValue = "1") Integer pageNum,
                                                         @RequestParam(defaultValue = "10") Integer pageSize){
        return cookbookService.getCollectionCookbooks(pageNum,pageSize);
    }
    @GetMapping("/cookbooks/search")
    CommonResult<CookbooksGetRes> searchCookbooks(@RequestParam(defaultValue = "1") Integer pageNum,
                                                  @RequestParam(defaultValue = "10") Integer pageSize,
                                                  @RequestParam(required = false) @Size(max = 30,message = "搜索关键词过长") String keyword){
        return cookbookService.searchCookbooks(pageNum,pageSize,keyword);
    }
    @PutMapping("/cookbooks/{cookbookId}/collect")
    CommonResult<BlankRes> collectCookbook(@PathVariable("cookbookId")@NotBlank(message = "菜谱号不能为空") String cookbookId){
        log.info("收藏菜谱号:{}",cookbookId);
        return cookbookService.collectCookbook(Integer.parseInt(cookbookId));
    }

    @GetMapping("/cookbooks/{cookbookId}/collect")
    CommonResult<CookbookCollectRes> getCollect(@PathVariable("cookbookId")@NotBlank(message = "菜谱号不能为空") String cookbookId){
        return cookbookService.getCollect(Integer.parseInt(cookbookId));
    }

    @PutMapping("/cookbooks/{cookbookId}/click")
    CommonResult<BlankRes> clickCookbook(@PathVariable("cookbookId")@NotBlank(message = "菜谱号不能为空") String cookbookId){
        return cookbookService.clickCookbook(Integer.parseInt(cookbookId));
    }

    @GetMapping("/user/collection/count")
    CommonResult<CollectionCountRes> getCollectionCount(){
        return cookbookService.getCollectionCount();
    }


    @Pass
    @PostMapping("/cookbook")
    CommonResult<BlankRes> addCookbook(@RequestBody PostCookbook postCookbook){
        return cookbookService.addCookbook(postCookbook);
    }
    @Pass
    @PostMapping("/cookbooks/{cookbookId}/image")
    public CommonResult<BlankRes> insertImage(@PathVariable("cookbookId") Integer cookbookid,
                                            @RequestParam("image") MultipartFile file){
        log.info("文件上传");
        return  cookbookService.insertImage(cookbookid,file);
    }

    @GetMapping("/cookbook/{cookbookId}")
    CommonResult<CookbookRes> getCookbookById(@PathVariable("cookbookId") Integer cookbookid){
        return  cookbookService.getCookbookById(cookbookid);
    }
}
