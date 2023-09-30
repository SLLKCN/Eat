package com.example.eat.model.dto.res.music;

import com.example.eat.model.po.music.Music;
import lombok.Data;

@Data
public class MusicRes {
    MusicRes(Music music){
        this.name=music.getName();
        this.introduction=music.getIntroduction();
        this.image= music.getImage();
        this.music=music.getMusic();
    }

    private String name;
    private String introduction;
    private String image;
    private String music;


}