package com.example.eat.service;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.res.file.FileNamesGetRes;
import com.example.eat.model.dto.res.file.FileUrlRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    CommonResult<FileUrlRes> getFileUrl(String name);

    CommonResult<FileNamesGetRes> updateFile(List<MultipartFile> files);
}
