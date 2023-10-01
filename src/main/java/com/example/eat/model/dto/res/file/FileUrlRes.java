package com.example.eat.model.dto.res.file;

import lombok.Data;

@Data
public class FileUrlRes {
    private String url;
    public FileUrlRes(String url){
        this.url=url;
    }
}
