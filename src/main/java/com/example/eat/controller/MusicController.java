package com.example.eat.controller;

import com.example.eat.aop.Pass;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.Music.PostMusic;
import com.example.eat.model.dto.param.Music.PostMusicInMusicList;
import com.example.eat.model.dto.param.Music.PostMusicList;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.music.FavouriteCountRes;
import com.example.eat.model.dto.res.music.MusicFavouriteRes;
import com.example.eat.model.dto.res.music.MusicGetRes;
import com.example.eat.model.dto.res.music.MusicListsGetRes;
import com.example.eat.service.MusicService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@CrossOrigin
public class MusicController {
    @Autowired
    MusicService musicService;
    @GetMapping("/users/music")
    CommonResult<MusicGetRes> getFavouriteMusic(){
        return musicService.getFavouriteMusic();
    }
    @GetMapping("/music/list")
    CommonResult<MusicListsGetRes> getMusicList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize){
        return musicService.getMusicList(pageNum,pageSize);
    }
    @GetMapping("/music/{musicListId}")
    CommonResult<MusicGetRes> getMusic(@PathVariable("musicListId")@NotBlank(message = "歌单id不能为空") String musicListId){
        return musicService.getMusic(Integer.parseInt(musicListId));
    }
    @PutMapping("/music/{musicId}/favourite")
    CommonResult<BlankRes> favouriteMusic(@PathVariable("musicId")@NotBlank(message = "歌曲id不能为空") String musicId){
        return musicService.favouriteMusic(Integer.parseInt(musicId));
    }
    @GetMapping("/music/{musicId}/favourite")
    CommonResult<MusicFavouriteRes> getFavourite(@PathVariable("musicId")@NotBlank(message = "歌曲id不能为空") String musicId){
        return musicService.getFavourite(Integer.parseInt(musicId));
    }

    @PutMapping("/music/{musicId}/click")
    CommonResult<BlankRes> clickMusic(@PathVariable("musicId")@NotBlank(message = "歌曲id不能为空") String musicId){
        return musicService.clickMusic(Integer.parseInt(musicId));
    }

    @PutMapping("/music/list/{musicListId}/click")
    CommonResult<BlankRes> clickMusicList(@PathVariable("musicListId")@NotBlank(message = "歌单id不能为空") String musicListId){
        return musicService.clickMusicList(Integer.parseInt(musicListId));
    }
    @GetMapping("/music/healing")
    CommonResult<MusicGetRes> getHealingMusic(@RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize){
        return musicService.getHealingMusic(pageNum,pageSize);
    }
    @GetMapping("/music/lists/personalize")
    CommonResult<MusicListsGetRes> getPersonalizeMusicList(@RequestParam(defaultValue = "1") Integer pageNum,
                                                           @RequestParam(defaultValue = "10") Integer pageSize){
        return musicService.getPersonalizeMusicList(pageNum,pageSize);
    }
    @GetMapping("/user/favourite/count")
    CommonResult<FavouriteCountRes> getFavouriteCount(){
        return musicService.getFavouriteCount();
    }

    @GetMapping("/musiclist/{musiclistId}/favourite")
    CommonResult<MusicFavouriteRes> getMusiclistFavourite(@PathVariable("musiclistId")@NotBlank(message = "歌单id不能为空") String musiclistId){
        return musicService.getMusiclistFavourite(Integer.parseInt(musiclistId));
    }

    @PutMapping("/musiclist/{musiclistId}/favourite")
    CommonResult<BlankRes> favouriteMusiclist(@PathVariable("musiclistId")@NotBlank(message = "歌单id不能为空") String musiclistId){
        return musicService.favouriteMusiclist(Integer.parseInt(musiclistId));
    }

    @GetMapping("/users/musiclist")
    CommonResult<MusicListsGetRes> getFavouriteMusiclist(){
        return musicService.getFavouriteMusiclist();
    }




    @Pass
    @PostMapping("/music")
    CommonResult<BlankRes> addMusic(@RequestBody PostMusic postMusic){
        return musicService.addMusic(postMusic);
    }
    @Pass
    @PostMapping("/music/{musicId}/audio")
    CommonResult<BlankRes> insertMusicAudio(@PathVariable("musicId") Integer musicId,
                                            @RequestParam("audio") MultipartFile file){
        return musicService.insertMusicAudio(musicId,file);
    }
    @Pass
    @PostMapping("/music/{musicId}/image")
    CommonResult<BlankRes> insertMusicImage(@PathVariable("musicId") Integer musicId,
                                            @RequestParam("image") MultipartFile file){
        return musicService.insertMusicImage(musicId,file);
    }
    @Pass
    @PostMapping("/musicList")
    CommonResult<BlankRes> addMusicList(@RequestBody PostMusicList postMusicList){
        return musicService.addMusicList(postMusicList);
    }
    @Pass
    @DeleteMapping("/music/{musicId}")
    CommonResult<BlankRes> deleteMusic(@PathVariable("musicId") Integer musicId){
        return musicService.deleteMusic(musicId);
    }
    @Pass
    @DeleteMapping("/musiclist/{musiclistId}")
    CommonResult<BlankRes> deleteMusicList(@PathVariable("musiclistId") Integer musiclistId){
        return musicService.deleteMusicList(musiclistId);
    }
    @Pass
    @PostMapping("/musicList/{musiclistId}/image")
    CommonResult<BlankRes> insertMusicListImage(@PathVariable("musiclistId") Integer musiclistId,
                                                @RequestParam("image") MultipartFile file){
        return musicService.insertMusicListImage(musiclistId,file);
    }
    @Pass
    @PostMapping("/musicInMusicList")
    CommonResult<BlankRes> addMusicInMusicList(@RequestBody PostMusicInMusicList postMusicInMusicList){
        return musicService.addMusicInMusicList(postMusicInMusicList);
    }
}
