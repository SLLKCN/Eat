package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.music.*;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.Music.PostMusic;
import com.example.eat.model.dto.param.Music.PostMusicInMusicList;
import com.example.eat.model.dto.param.Music.PostMusicList;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.music.MusicFavouriteRes;
import com.example.eat.model.dto.res.music.MusicGetRes;
import com.example.eat.model.dto.res.music.MusicListsGetRes;
import com.example.eat.model.po.music.*;
import com.example.eat.service.MusicService;
import com.example.eat.service.UserService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.MinioUtil;
import com.example.eat.util.TokenThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MusicServiceImpl extends ServiceImpl<MusicDao, Music> implements MusicService {
    @Autowired
    MinioUtil minioUtil;
    @Autowired
    FavouriteDao favouriteDao;
    @Autowired
    MusicInListDao musicInListDao;
    @Autowired
    MusicListDao musicListDao;
    @Autowired
    UserService userService;
    @Autowired
    MusicScoreDao musicScoreDao;

    @Override
    public CommonResult<MusicGetRes> getFavouriteMusic() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        MusicGetRes musicGetRes;
        try{
            QueryWrapper<Favourite> favouriteQueryWrapper=new QueryWrapper<>();
            favouriteQueryWrapper.eq("user_id",userId);
            List<Favourite> favouriteList=favouriteDao.selectList(favouriteQueryWrapper);
            //转换成对应音乐id列表
            List<Integer> musicId=new ArrayList<>();
            for (Favourite favourite:favouriteList) {
                musicId.add(favourite.getMusicId());
            }

            QueryWrapper<Music> musicQueryWrapper=new QueryWrapper<>();
            if(musicId.size()==0){
                musicGetRes=new MusicGetRes(new ArrayList<>());
                return CommonResult.success("查询喜欢歌曲成功",musicGetRes);
            }
            musicQueryWrapper.in("id",musicId);

            musicGetRes=new MusicGetRes(this.getBaseMapper().selectList(musicQueryWrapper));
            musicGetRes.setTotal(this.getBaseMapper().selectCount(musicQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询喜欢歌曲失败");
        }
        return CommonResult.success("查询喜欢歌曲成功",musicGetRes);
    }

    @Override
    public CommonResult<MusicListsGetRes> getMusicList(Integer pageNum, Integer pageSize) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }
        MusicListsGetRes musicListsGetRes;
        try{
            Page<MusicList> musicListPage = new Page<>(pageNum, pageSize);
            QueryWrapper<MusicList> musicListQueryWrapper=new QueryWrapper<>();
            IPage<MusicList> musicListIPage=musicListDao.selectPage(musicListPage,musicListQueryWrapper);
            List<MusicList> musicListList=musicListIPage.getRecords();
            musicListsGetRes=new MusicListsGetRes(musicListList);
            musicListsGetRes.setTotal(musicListIPage.getTotal());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询歌单失败");
        }
        return CommonResult.success("获取歌单成功",musicListsGetRes);
    }

    @Override
    public CommonResult<MusicGetRes> getMusic(Integer musicListId) {
        MusicGetRes musicGetRes;
        try{
            QueryWrapper<MusicInList> musicInListQueryWrapper=new QueryWrapper<>();
            musicInListQueryWrapper.eq("musiclist_id",musicListId);
            List<MusicInList> musicInListList=musicInListDao.selectList(musicInListQueryWrapper);
            //转换成对应音乐id列表
            List<Integer> musicId=new ArrayList<>();
            for (MusicInList musicInList:musicInListList) {
                musicId.add(musicInList.getMusicId());
            }

            QueryWrapper<Music> musicQueryWrapper=new QueryWrapper<>();
            musicQueryWrapper.in("id",musicId);

            musicGetRes=new MusicGetRes(this.getBaseMapper().selectList(musicQueryWrapper));
            musicGetRes.setTotal(this.getBaseMapper().selectCount(musicQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询音乐失败");
        }
        return CommonResult.success("查询音乐成功",musicGetRes);
    }

    @Override
    public CommonResult<BlankRes> favouriteMusic(Integer musicId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }




        try {
            Integer isFavourite=checkFavourite(userId,musicId);
            //判断音乐是否存在
            Music music=this.getById(musicId);
            if(music==null){
                return CommonResult.fail("未找到该音乐");
            }

            if(isFavourite.equals(1)){
                QueryWrapper<Favourite> favouriteQueryWrapper=new QueryWrapper<>();
                favouriteQueryWrapper.eq("user_id",userId);
                favouriteQueryWrapper.eq("music_id",musicId);
                favouriteDao.delete(favouriteQueryWrapper);

                //喜欢数减一
                music.setFavouriteCount(music.getFavouriteCount()-1);
                this.updateById(music);
                //判断喜欢数是否小于0
                if(music.getFavouriteCount()<0){
                    return CommonResult.fail("音乐喜欢数异常");
                }

                return CommonResult.success("取消音乐喜欢");
            }
            Favourite favourite=new Favourite();
            favourite.setUserId(userId);
            favourite.setMusicId(musicId);
            favouriteDao.insert(favourite);
            //喜欢数加一
            music.setFavouriteCount(music.getFavouriteCount()+1);
            this.updateById(music);


        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("修改音乐喜欢状态失败");
        }
        return CommonResult.success("喜欢音乐");
    }

    @Override
    public CommonResult<MusicFavouriteRes> getFavourite(Integer musicId) {
        //判断是否存在该用户
        Integer userId=null;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        MusicFavouriteRes musicFavouriteRes=new MusicFavouriteRes();
        try{
            musicFavouriteRes.setIsFavourite(checkFavourite(userId,musicId));

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取音乐喜欢状态失败");
        }
        return CommonResult.success("获取音乐喜欢状态成功",musicFavouriteRes);


    }

    @Override
    public CommonResult<BlankRes> clickMusic(Integer musicId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        try{
            QueryWrapper<MusicScore> musicScoreQueryWrapper=new QueryWrapper<>();
            musicScoreQueryWrapper.eq("user_id",userId);
            musicScoreQueryWrapper.eq("music_id",musicId);
            MusicScore musicScore=musicScoreDao.selectOne(musicScoreQueryWrapper);
            if(musicScore==null){
                musicScore.setUserId(userId);
                musicScore.setMusicId(musicId);
                musicScore.setScore(1);
                musicScoreDao.insert(musicScore);
                return CommonResult.success("点击成功");
            }
            musicScore.setScore(musicScore.getScore()+1);
            musicScoreDao.updateById(musicScore);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("点击失败");
        }
        return CommonResult.success("点击成功");
    }


    @Override
    public CommonResult<BlankRes> addMusic(PostMusic postMusic) {
        try {
            Music music=new Music();
            music.setName(postMusic.getName());
            music.setIntroduction(postMusic.getIntroduction());
            music.setFavouriteCount(0);
            this.save(music);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加音乐失败");
        }
        return CommonResult.fail("添加音乐成功");
    }

    @Override
    public CommonResult<BlankRes> insertMusicImage(Integer musicId, MultipartFile file) {

        try {
            Music music=new Music();
            music.setId(musicId);
            String fileName = minioUtil.uploadFileByFile(file);
            //判断minio上传是否失败
            if (fileName == null){
                log.error("minio上传失败！");
                return CommonResult.fail("设置失败！");
            }
            music.setImage(fileName);
            this.updateById(music);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加音乐图片失败");
        }

        return CommonResult.success("添加音乐图片成功");

    }

    @Override
    public CommonResult<BlankRes> insertMusicAudio(Integer musicId, MultipartFile file) {
        try {
            Music music=new Music();
            music.setId(musicId);
            String fileName = minioUtil.uploadFileByFile(file);
            //判断minio上传是否失败
            if (fileName == null){
                log.error("minio上传失败！");
                return CommonResult.fail("设置失败！");
            }
            music.setMusic(fileName);
            this.updateById(music);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加音乐音频失败");
        }

        return CommonResult.success("添加音乐音频成功");
    }

    @Override
    public CommonResult<BlankRes> addMusicList(PostMusicList postMusicList) {
        try {
            MusicList musicList=new MusicList();
            musicList.setName(postMusicList.getName());
            musicList.setIntroduction(postMusicList.getIntroduction());
            musicListDao.insert(musicList);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加音乐失败");
        }
        return CommonResult.fail("添加音乐成功");
    }

    @Override
    public CommonResult<BlankRes> deleteMusic(Integer musicId) {
        try{
            this.removeById(musicId);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("删除音乐失败");
        }
        return CommonResult.success("删除音乐成功");
    }

    @Override
    public CommonResult<BlankRes> deleteMusicList(Integer musiclistId) {
        try{
            musicListDao.deleteById(musiclistId);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("删除歌单失败");
        }
        return CommonResult.success("删除歌单成功");
    }

    @Override
    public CommonResult<BlankRes> insertMusicListImage(Integer musiclistId, MultipartFile file) {
        try {
            MusicList musicList=new MusicList();
            musicList.setId(musiclistId);
            String fileName = minioUtil.uploadFileByFile(file);
            //判断minio上传是否失败
            if (fileName == null){
                log.error("minio上传失败！");
                return CommonResult.fail("设置失败！");
            }
            musicList.setImage(fileName);
            musicListDao.updateById(musicList);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("添加歌单图片失败");
        }

        return CommonResult.success("添加歌单图片成功");
    }

    @Override
    public CommonResult<BlankRes> addMusicInMusicList(PostMusicInMusicList postMusicInMusicList) {
        try {
            MusicInList musicInList=new MusicInList();
            musicInList.setMusiclistId(postMusicInMusicList.getMusicListId());
            musicInList.setMusicId(postMusicInMusicList.getMusicId());
            musicInListDao.insert(musicInList);
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.fail("歌单添加歌曲失败");
        }
        return CommonResult.success("歌单添加歌曲成功");
    }

    //查看歌曲是否被喜欢
    public Integer checkFavourite(Integer userId,Integer musicId){
        try{
            QueryWrapper<Favourite> favouriteQueryWrapper=new QueryWrapper<>();
            favouriteQueryWrapper.eq("user_id",userId);
            favouriteQueryWrapper.eq("music_id",musicId);
            Favourite favourite=favouriteDao.selectOne(favouriteQueryWrapper);
            if(favourite!=null){
                return 0;
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
