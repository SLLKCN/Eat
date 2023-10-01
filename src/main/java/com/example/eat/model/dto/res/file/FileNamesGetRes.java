package com.example.eat.model.dto.res.file;

import lombok.Data;

import java.util.List;
@Data
public class FileNamesGetRes {
    private Long total;
    private List<FileNameRes> NameList;
}
