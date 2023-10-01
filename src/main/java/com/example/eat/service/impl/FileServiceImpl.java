package com.example.eat.service.impl;

import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.res.file.FileNameRes;
import com.example.eat.model.dto.res.file.FileNamesGetRes;
import com.example.eat.model.dto.res.file.FileUrlRes;
import com.example.eat.service.FileService;
import com.example.eat.util.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    MinioUtil minioUtil;
    @Override
    public CommonResult<FileUrlRes> getFileUrl(String name) {
        String url;
        try{
            url=minioUtil.downloadFile(name);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取文件失败");
        }
        FileUrlRes fileUrlRes=new FileUrlRes(url);
        return CommonResult.success("获取文件成功",fileUrlRes);
    }

    @Override
    public CommonResult<FileNamesGetRes> updateFile(List<MultipartFile> files) {
        FileNamesGetRes fileNamesGetRes=new FileNamesGetRes();
        List<FileNameRes> nameList=new ArrayList<>();
        try{
            Integer position=1;
            for (MultipartFile file:files) {
                String fileName = minioUtil.uploadFileByFile(file);
                //判断minio上传是否失败
                if (fileName == null){
                    log.error("minio上传失败！");
                    return CommonResult.fail("第"+position+"个文件上传失败！");
                }
                //封装文件名字类
                FileNameRes temp=new FileNameRes();
                temp.setPosition(position++);
                temp.setName(fileName);
                //装入结果集
                nameList.add(temp);
            }
            fileNamesGetRes.setNameList(nameList);
            fileNamesGetRes.setTotal(Long.valueOf(position-1));
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.fail("文件上传失败！");
        }
        return CommonResult.success("上传成功",fileNamesGetRes);
    }
}
