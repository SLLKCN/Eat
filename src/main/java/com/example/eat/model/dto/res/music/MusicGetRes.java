package com.example.eat.model.dto.res.music;

import com.example.eat.model.po.music.Music;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class MusicGetRes {

    private Long total;
    private List<MusicRes> musicResList=new ArrayList<>();
    public MusicGetRes(List<Music> musicList){
        for (Music music:musicList) {
            MusicRes musicRes=new MusicRes(music);
            musicResList.add(musicRes);
        }
    }
}
