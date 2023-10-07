package com.example.eat.controller;

import com.example.eat.aop.Pass;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.res.file.FileNamesGetRes;
import com.example.eat.model.dto.res.file.FileUrlRes;
import com.example.eat.service.FileService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
public class FileController {
    @Autowired
    FileService fileService;
    @Pass
    @GetMapping("/file")
    CommonResult<FileUrlRes> getFileUrl(@NotNull(message = "文件名不可为空") @RequestParam("filename") String name){
        return fileService.getFileUrl(name);
    }
    @Pass
    @PostMapping("/file")
    CommonResult<FileNamesGetRes> updateFile(@RequestParam("files") List<MultipartFile> files){
        return fileService.updateFile(files);
    }
}
