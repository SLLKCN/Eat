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
import com.example.eat.model.dto.res.music.*;
import com.example.eat.model.po.music.*;
import com.example.eat.service.MusicService;
import com.example.eat.service.UserService;
import com.example.eat.util.JwtUtils;
import com.example.eat.util.MinioUtil;
import com.example.eat.util.RecommendUtil;
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
    @Autowired
    MusicListScoreDao musicListScoreDao;
    @Autowired
    FavouriteMusiclistDao favouriteMusiclistDao;
    RecommendUtil recommendUtil=new RecommendUtil();

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

            List<Music> musicList=this.getBaseMapper().selectList(musicQueryWrapper);
            for (Music music:musicList) {
                music.setFavouriteCount(checkFavourite(userId,music.getId()));
                music.setMusic(minioUtil.downloadFile(music.getMusic()));
            }
            musicGetRes=new MusicGetRes(musicList);
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
            QueryWrapper<MusicInList> musicInListQueryWrapper=new QueryWrapper<>();
            musicInListQueryWrapper.eq("musiclist_id",musicListId);
            List<MusicInList> musicInListList=musicInListDao.selectList(musicInListQueryWrapper);
            //转换成对应音乐id列表
            List<Integer> musicId=new ArrayList<>();
            for (MusicInList musicInList:musicInListList) {
                musicId.add(musicInList.getMusicId());
            }

            QueryWrapper<Music> musicQueryWrapper=new QueryWrapper<>();
            if(musicId.size()==0){
                musicGetRes=new MusicGetRes();
                musicGetRes.setTotal((long)0);
                return CommonResult.success("查询音乐成功",musicGetRes);
            }
            musicQueryWrapper.in("id",musicId);

            List<Music> musicList=this.getBaseMapper().selectList(musicQueryWrapper);
            for (Music music:musicList) {
                music.setFavouriteCount(checkFavourite(userId,music.getId()));
                music.setMusic(minioUtil.downloadFile(music.getMusic()));
            }

            musicGetRes=new MusicGetRes(musicList);
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
                musicScore=new MusicScore();
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
    public CommonResult<MusicGetRes> getHealingMusic(Integer pageNum, Integer pageSize) {
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
            Page<Music> musicPage=new Page<>(pageNum,pageSize);


            //选择疗愈音乐
            QueryWrapper<Music> musicQueryWrapper=new QueryWrapper<>();
            musicQueryWrapper.eq("type","疗愈音乐");

            IPage<Music> musicIPage=page(musicPage,musicQueryWrapper);
            List<Music> musicList=musicIPage.getRecords();
            for (Music music:musicList) {
                music.setFavouriteCount(checkFavourite(userId,music.getId()));
                music.setMusic(minioUtil.downloadFile(music.getMusic()));
            }

            musicGetRes=new MusicGetRes(musicList);
            musicGetRes.setTotal(this.getBaseMapper().selectCount(musicQueryWrapper));

        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询歌曲失败");
        }
        return CommonResult.success("查询歌曲成功",musicGetRes);
    }

    @Override
    public CommonResult<MusicListsGetRes> getPersonalizeMusicList(Integer pageNum, Integer pageSize) {
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

            //获取推荐歌单
            List<String> recommand=recommendUtil.getMusicListRecommend(userId);
            if(recommand==null||recommand.size()==0){
                recommand=new ArrayList<>();
                recommand.add("-1");
            }
            QueryWrapper<MusicList> musicListQueryWrapper=new QueryWrapper<>();
            musicListQueryWrapper.in("id",recommand);


            IPage<MusicList> musicListIPage=musicListDao.selectPage(musicListPage,musicListQueryWrapper);



            List<MusicList> musicListList=musicListIPage.getRecords();

            if(musicListList==null||musicListList.size()==0){
                musicListList=new ArrayList<>();
            }

            if(musicListList.size()<10){
                QueryWrapper<MusicList> musicListQueryWrapper1=new QueryWrapper<>();
                musicListQueryWrapper1.notIn("id",recommand);
                List<MusicList> musicListList1=musicListDao.selectList(musicListQueryWrapper1);
                for(MusicList temp:musicListList1){
                    if(musicListList.size()>=10){
                        break;
                    }
                    musicListList.add(temp);
                }
            }
            musicListsGetRes=new MusicListsGetRes(musicListList);
            musicListsGetRes.setTotal(musicListIPage.getTotal());
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询歌单失败");
        }
        return CommonResult.success("获取歌单成功",musicListsGetRes);
    }

    @Override
    public CommonResult<BlankRes> clickMusicList(Integer musicListId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        try{
            QueryWrapper<MusicListScore> musicListScoreQueryWrapper=new QueryWrapper<>();
            musicListScoreQueryWrapper.eq("user_id",userId);
            musicListScoreQueryWrapper.eq("musiclist_id",musicListId);
            MusicListScore musicListScore=musicListScoreDao.selectOne(musicListScoreQueryWrapper);
            if(musicListScore==null){
                musicListScore=new MusicListScore();
                musicListScore.setUserId(userId);
                musicListScore.setMusiclistId(musicListId);
                musicListScore.setScore(1);
                musicListScoreDao.insert(musicListScore);
                return CommonResult.success("点击成功");
            }
            musicListScore.setScore(musicListScore.getScore()+1);
            musicListScoreDao.updateById(musicListScore);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("点击失败");
        }
        return CommonResult.success("点击成功");
    }

    @Override
    public CommonResult<FavouriteCountRes> getFavouriteCount() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        FavouriteCountRes favouriteCountRes=new FavouriteCountRes();
        try{
            QueryWrapper<Favourite> favouriteQueryWrapper=new QueryWrapper<>();
            favouriteQueryWrapper.eq("user_id",userId);
            favouriteCountRes.setFavouriteCount(favouriteDao.selectCount(favouriteQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取我的喜欢数失败");
        }
        return CommonResult.success("获取我的喜欢数成功",favouriteCountRes);
    }

    @Override
    public CommonResult<MusicListsGetRes> getFavouriteMusiclist() {
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
            QueryWrapper<FavouriteMusiclist> favouriteMusiclistQueryWrapper=new QueryWrapper<>();
            favouriteMusiclistQueryWrapper.eq("user_id",userId);
            List<FavouriteMusiclist> favouriteMusiclistList=favouriteMusiclistDao.selectList(favouriteMusiclistQueryWrapper);
            //转换成对应音乐id列表
            List<Integer> musiclistIdList=new ArrayList<>();
            for (FavouriteMusiclist temp:favouriteMusiclistList) {
                musiclistIdList.add(temp.getMusiclistId());
            }

            QueryWrapper<MusicList> musicListQueryWrapper=new QueryWrapper<>();
            if(musiclistIdList.size()==0){
                musicListsGetRes=new MusicListsGetRes(new ArrayList<>());
                return CommonResult.success("查询喜欢歌单成功",musicListsGetRes);
            }
            musicListQueryWrapper.in("id",musiclistIdList);

            List<MusicList> musicListList=musicListDao.selectList(musicListQueryWrapper);

            musicListsGetRes=new MusicListsGetRes(musicListList);
            musicListsGetRes.setTotal(musicListDao.selectCount(musicListQueryWrapper));
            for (MusicListRes temp:musicListsGetRes.getMusicListResList()) {
                temp.setIsFavourite(checkFavourite(userId,temp.getId()));
            }
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("查询喜欢歌单失败");
        }
        return CommonResult.success("查询喜欢歌单成功",musicListsGetRes);
    }

    @Override
    public CommonResult<MusicFavouriteRes> getMusiclistFavourite(Integer musiclistId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }

        MusicFavouriteRes musicFavouriteRes=new MusicFavouriteRes();
        try{
            musicFavouriteRes.setIsFavourite(checkMsiclistFavourite(userId,musiclistId));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取歌单喜欢状态失败");
        }
        return CommonResult.success("获取歌单喜欢状态成功",musicFavouriteRes);
    }

    @Override
    public CommonResult<BlankRes> favouriteMusiclist(Integer musiclistId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }




        try {
            Integer isFavourite=checkMsiclistFavourite(userId,musiclistId);
            //判断音乐是否存在
            MusicList musicList=musicListDao.selectById(musiclistId);
            if(musicList==null){
                return CommonResult.fail("未找到该音乐");
            }

            if(isFavourite.equals(1)){
                QueryWrapper<FavouriteMusiclist> favouriteMusiclistQueryWrapper=new QueryWrapper<>();
                favouriteMusiclistQueryWrapper.eq("user_id",userId);
                favouriteMusiclistQueryWrapper.eq("musiclist_id",musiclistId);
                favouriteMusiclistDao.delete(favouriteMusiclistQueryWrapper);
                return CommonResult.success("取消歌单喜欢");
            }
            FavouriteMusiclist favouriteMusiclist=new FavouriteMusiclist();
            favouriteMusiclist.setUserId(userId);
            favouriteMusiclist.setMusiclistId(musiclistId);
            favouriteMusiclistDao.insert(favouriteMusiclist);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("修改歌单喜欢状态失败");
        }
        return CommonResult.success("喜欢歌单");
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
            if(favourite==null){
                return 0;
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public Integer checkMsiclistFavourite(Integer userId,Integer musiclistId){
        try{
            QueryWrapper<FavouriteMusiclist> favouriteMusiclistQueryWrapper=new QueryWrapper<>();
            favouriteMusiclistQueryWrapper.eq("user_id",userId);
            favouriteMusiclistQueryWrapper.eq("musiclist_id",musiclistId);
            FavouriteMusiclist favouriteMusiclist=favouriteMusiclistDao.selectOne(favouriteMusiclistQueryWrapper);
            if(favouriteMusiclist==null){
                return 0;
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
