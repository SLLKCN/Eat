package com.example.eat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.Music.PostMusic;
import com.example.eat.model.dto.param.Music.PostMusicInMusicList;
import com.example.eat.model.dto.param.Music.PostMusicList;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.music.MusicFavouriteRes;
import com.example.eat.model.dto.res.music.MusicGetRes;
import com.example.eat.model.dto.res.music.MusicListsGetRes;
import com.example.eat.model.po.music.Music;
import org.springframework.web.multipart.MultipartFile;

public interface MusicService extends IService<Music> {
    CommonResult<MusicGetRes> getFavouriteMusic();

    CommonResult<MusicListsGetRes> getMusicList(Integer pageNum, Integer pageSize);

    CommonResult<MusicGetRes> getMusic(Integer musicListId);

    CommonResult<BlankRes> favouriteMusic(Integer musicId);


    CommonResult<BlankRes> addMusic(PostMusic postMusic);

    CommonResult<BlankRes> insertMusicImage(Integer musicId, MultipartFile file);

    CommonResult<BlankRes> insertMusicAudio(Integer musicId, MultipartFile file);

    CommonResult<BlankRes> addMusicList(PostMusicList postMusicList);

    CommonResult<BlankRes> deleteMusic(Integer musicId);

    CommonResult<BlankRes> deleteMusicList(Integer musiclistId);

    CommonResult<BlankRes> insertMusicListImage(Integer musiclistId, MultipartFile file);

    CommonResult<BlankRes> addMusicInMusicList(PostMusicInMusicList postMusicInMusicList);

    CommonResult<MusicFavouriteRes> getFavourite(Integer musicId);

    CommonResult<BlankRes> clickMusic(Integer musicId);
}
