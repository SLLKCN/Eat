package com.example.eat.model.dto.res.music;

import com.example.eat.model.po.music.MusicList;
import lombok.Data;

@Data
public class MusicListRes {
    private Integer id;
    private String name;
    private String introduction;
    private String image;
    MusicListRes(MusicList musicList){
        this.id=musicList.getId();
        this.name=musicList.getName();
        this.introduction=musicList.getIntroduction();
        this.image=musicList.getImage();
    }

}
