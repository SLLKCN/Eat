package com.example.eat.model.dto.res.music;

import com.example.eat.model.po.music.MusicList;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MusicListsGetRes {
    private Long total;
    private List<MusicListRes> musicListResList=new ArrayList<>();
    public MusicListsGetRes(List<MusicList> musicListList){
        for (MusicList musicList:musicListList) {
            MusicListRes musicListRes=new MusicListRes(musicList);
            this.musicListResList.add(musicListRes);
        }
    }


}
