package com.example.eat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.eat.dao.cookbook.ClickDao;
import com.example.eat.dao.cookbook.CollectionDao;
import com.example.eat.dao.cookbook.CookbookDao;
import com.example.eat.dao.cookbook.CookbookScoreDao;
import com.example.eat.model.dto.CommonResult;
import com.example.eat.model.dto.param.cookbook.PostCookbook;
import com.example.eat.model.dto.res.BlankRes;
import com.example.eat.model.dto.res.cookbook.CollectionCountRes;
import com.example.eat.model.dto.res.cookbook.CookbookCollectRes;
import com.example.eat.model.dto.res.cookbook.CookbookRes;
import com.example.eat.model.dto.res.cookbook.CookbooksGetRes;
import com.example.eat.model.po.cookbook.Click;
import com.example.eat.model.po.cookbook.Collection;
import com.example.eat.model.po.cookbook.Cookbook;
import com.example.eat.model.po.cookbook.CookbookScore;
import com.example.eat.service.CookbookService;
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
public class CookbookServiceImpl extends ServiceImpl<CookbookDao, Cookbook> implements CookbookService {
    @Autowired
    ClickServiceImpl clickService;
    @Autowired
    CollectionServiceImpl collectionService;
    @Autowired
    UserService userService;
    @Autowired
    MinioUtil minioUtil;
    @Autowired
    CookbookScoreDao cookbookScoreDao;
    @Autowired
    RecommendUtil recommendUtil;

    @Override
    public CommonResult<CookbooksGetRes> getPersonalizeCookbooks(Integer pageNum, Integer pageSize, String type) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        Page<Cookbook> page = new Page<>(pageNum, pageSize);
        CookbooksGetRes cookbooksGetRes;

        try{
            List<String> recommand=recommendUtil.getCoobookRecommend(userId);
            if(recommand==null||recommand.size()==0){
                recommand=new ArrayList<>();
                recommand.add("-1");
            }

            // 构建查询条件
            QueryWrapper<Cookbook> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id",recommand);
            if (type!=null&&type.length()!=0) {
                queryWrapper.eq("type", type);
            }
            // 执行分页查询
            IPage<Cookbook> cookbookIPage = page(page, queryWrapper);
            List<Cookbook> cookbookList=cookbookIPage.getRecords();

            if(cookbookList==null||cookbookList.size()==0){
                cookbookList=new ArrayList<>();
            }

            if(cookbookList.size()<10){
                QueryWrapper<Cookbook> queryWrapper1=new QueryWrapper<>();
                queryWrapper1.notIn("id",recommand);
                if (type!=null&&type.length()!=0) {
                    queryWrapper1.eq("type", type);
                }
                List<Cookbook> cookbookList1=this.list(queryWrapper1);
                for(Cookbook temp:cookbookList1){
                    if(cookbookList.size()>=10){
                        break;
                    }
                    cookbookList.add(temp);
                }
            }


            cookbooksGetRes=new CookbooksGetRes(cookbookList);
            cookbooksGetRes.setTotal(cookbookIPage.getTotal());
        }catch (Exception e){
            log.error("查询菜谱失败");
            return CommonResult.fail("查询菜谱失败");
        }
        return CommonResult.success("查询成功",cookbooksGetRes);
    }

    @Override
    public CommonResult<CookbooksGetRes> getTodayCookbooks(Integer pageNum, Integer pageSize, String type) {
        Page<Cookbook> page = new Page<>(pageNum, pageSize);
        CookbooksGetRes cookbooksGetRes;

        try{
            // 构建查询条件
            QueryWrapper<Cookbook> queryWrapper = new QueryWrapper<>();
            if (type!=null&&type.length()!=0) {
                queryWrapper.eq("type", type);
            }
            // 执行分页查询
            IPage<Cookbook> cookbookIPage = page(page, queryWrapper);
            List<Cookbook> cookbookList=cookbookIPage.getRecords();

            cookbooksGetRes=new CookbooksGetRes(cookbookList);
        }catch (Exception e){
            log.error("查询菜谱失败");
            return CommonResult.fail("查询菜谱失败");
        }
        return CommonResult.success("查询成功",cookbooksGetRes);
    }

    @Override
    public CommonResult<CookbooksGetRes> getCollectionCookbooks(Integer pageNum, Integer pageSize) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        Page<Cookbook> page = new Page<>(pageNum, pageSize);
        CookbooksGetRes cookbooksGetRes;

        try{
            Page<Collection> collectionPage = new Page<>(pageNum, pageSize);
            QueryWrapper<Collection> collectionQueryWrapper=new QueryWrapper<>();
            collectionQueryWrapper.eq("user_id",userId);
            IPage<Collection> collectionIPage=collectionService.page(collectionPage,collectionQueryWrapper);
            //转换成对应菜谱id列表
            List<Integer> cookbookIdList=new ArrayList<>();
            for (Collection collection:collectionIPage.getRecords()) {
                cookbookIdList.add(collection.getCookbookId());
            }


            // 构建查询条件
            QueryWrapper<Cookbook> queryWrapper = new QueryWrapper<>();
            if(cookbookIdList.size()==0||cookbookIdList==null){
                cookbooksGetRes=new CookbooksGetRes(new ArrayList<>());
                return CommonResult.success("查询成功",cookbooksGetRes);
            }
            queryWrapper.in("id",cookbookIdList);
            // 执行分页查询
            IPage<Cookbook> cookbookIPage = page(page, queryWrapper);
            List<Cookbook> cookbookList=cookbookIPage.getRecords();
            cookbooksGetRes=new CookbooksGetRes(cookbookList);
        }catch (Exception e){
            log.error("查询菜谱失败");
            return CommonResult.fail("查询菜谱失败");
        }
        return CommonResult.success("查询成功",cookbooksGetRes);
    }

    @Override
    public CommonResult<CookbooksGetRes> searchCookbooks(Integer pageNum, Integer pageSize, String keyword) {
        Page<Cookbook> page = new Page<>(pageNum, pageSize);
        CookbooksGetRes cookbooksGetRes;

        try{
            // 构建查询条件
            QueryWrapper<Cookbook> queryWrapper = new QueryWrapper<>();
            if (keyword!=null) {
                queryWrapper.like("name",keyword);
            }
            // 执行分页查询
            IPage<Cookbook> cookbookIPage = page(page, queryWrapper);
            List<Cookbook> cookbookList=cookbookIPage.getRecords();
            cookbooksGetRes=new CookbooksGetRes(cookbookList);
            cookbooksGetRes.setTotal(cookbookIPage.getTotal());
        }catch (Exception e){
            log.error("查询菜谱失败");
            return CommonResult.fail("查询菜谱失败");
        }
        return CommonResult.success("查询成功",cookbooksGetRes);
    }

    @Override
    public CommonResult<BlankRes> collectCookbook(Integer cookbookId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }




        try{
            Integer isCollect=checkCollect(userId,cookbookId);
            //查看该菜谱是否存在
            Cookbook cookbook=this.getById(cookbookId);
            if(cookbook==null){
                return CommonResult.fail("菜谱不存在");
            }


            if(isCollect.equals(1)){
                QueryWrapper<Collection> collectionQueryWrapper=new QueryWrapper<>();
                collectionQueryWrapper.eq("user_id",userId);
                collectionQueryWrapper.eq("cookbook_id",cookbookId);
                collectionService.remove(collectionQueryWrapper);

                //收藏数减一
                cookbook.setCollectCount(cookbook.getCollectCount()-1);
                this.updateById(cookbook);
                //检查收藏数
                if(cookbook.getCollectCount()<0){
                    return CommonResult.fail("菜谱收藏数异常");
                }

                return CommonResult.success("取消菜谱收藏");
            }
            Collection collection;
            collection=new Collection();
            collection.setUserId(userId);
            collection.setCookbookId(cookbookId);
            collectionService.save(collection);

            //收藏数加一
            cookbook.setCollectCount(cookbook.getCollectCount()+1);
            this.updateById(cookbook);

        }catch (Exception e){
            log.error("菜谱修改收藏状态失败");
            return CommonResult.fail("菜谱修改收藏状态失败");
        }
        return CommonResult.success("成功收藏菜谱");
    }



    @Override
    public CommonResult<CookbookCollectRes> getCollect(Integer cookbookId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        CookbookCollectRes cookbookCollectRes=new CookbookCollectRes();
        try{
            cookbookCollectRes.setIsCollect(checkCollect(userId,cookbookId));
        }catch (Exception e){
            log.error("查找收藏状态失败");
            return CommonResult.fail("查找收藏状态失败");
        }
        return CommonResult.success("查找收藏状态成功",cookbookCollectRes);
    }

    @Override
    public CommonResult<BlankRes> clickCookbook(Integer cookbookId) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        try{
            QueryWrapper<CookbookScore> cookbookScoreQueryWrapper=new QueryWrapper<>();
            cookbookScoreQueryWrapper.eq("user_id",userId);
            cookbookScoreQueryWrapper.eq("cookbook_id",cookbookId);
            CookbookScore cookbookScore=cookbookScoreDao.selectOne(cookbookScoreQueryWrapper);
            if(cookbookScore==null){
                cookbookScore=new CookbookScore();
                cookbookScore.setUserId(userId);
                cookbookScore.setCookbookId(cookbookId);
                cookbookScore.setScore(1);
                cookbookScoreDao.insert(cookbookScore);
                return CommonResult.success("点击成功");
            }
            cookbookScore.setScore(cookbookScore.getScore()+1);
            cookbookScoreDao.updateById(cookbookScore);
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("点击失败");
        }
        return CommonResult.success("点击成功");
    }

    @Override
    public CommonResult<CollectionCountRes> getCollectionCount() {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在");
            return CommonResult.fail("用户不存在");
        }
        CollectionCountRes collectionCountRes=new CollectionCountRes();
        try{
            QueryWrapper<Collection> collectionQueryWrapper=new QueryWrapper<>();
            collectionQueryWrapper.eq("user_id",userId);
            collectionCountRes.setCollectionCount(collectionService.count(collectionQueryWrapper));
        }catch (Exception e){
            e.printStackTrace();
            return CommonResult.fail("获取我的收藏数失败");
        }
        return CommonResult.success("获取我的收藏数成功",collectionCountRes);
    }

    @Override
    public CommonResult<CookbookRes> getCookbookById(Integer cookbookid) {
        //判断是否存在该用户
        Integer userId;
        try {
            userId = JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken());
        } catch (Exception e) {
            log.warn("用户不存在  user:{}",JwtUtils.getUserIdByToken(TokenThreadLocalUtil.getInstance().getToken()));
            return CommonResult.fail("用户不存在");
        }

        CookbookRes cookbookRes;

        try{
            Cookbook cookbook=this.getById(cookbookid);
            cookbook.setImage(minioUtil.downloadFile(cookbook.getImage()));
            cookbookRes=new CookbookRes(cookbook);
        }catch (Exception e){
            log.error("查询菜谱失败");
            return CommonResult.fail("查询菜谱失败");
        }
        return CommonResult.success("查询成功",cookbookRes);
    }

    @Override
    public CommonResult<BlankRes> addCookbook(PostCookbook postCookbook) {
        Cookbook cookbook=new Cookbook();
        try{
            cookbook.setContent(postCookbook.getContent());
            cookbook.setType(postCookbook.getType());
            cookbook.setName(postCookbook.getName());
            cookbook.setIntroduction(postCookbook.getIntroduction());
            cookbook.setCollectCount(0);
            this.save(cookbook);
        }catch (Exception e){
            return CommonResult.fail("上传菜谱失败");
        }
        return CommonResult.success("上传菜谱成功");
    }

    @Override
    public CommonResult<BlankRes> insertImage(Integer cookbookid,MultipartFile file) {
        try {

            Cookbook cookbook=new Cookbook();
            cookbook.setId(cookbookid);

            String fileName = minioUtil.uploadFileByFile(file);
            //判断minio上传是否失败
            if (fileName == null){
                log.error("minio上传失败！");
                return CommonResult.fail("设置失败！");
            }
            cookbook.setImage(fileName);
            this.updateById(cookbook);
        } catch (Exception e) {
            return CommonResult.fail("上传失败");
        }
        return CommonResult.success("成功上传图片");
    }

    //检查菜单是否被收藏
    private Integer checkCollect(Integer userId, Integer cookbookId) {
        try{
            Collection collection;
            QueryWrapper<Collection> collectionQueryWrapper=new QueryWrapper<>();
            collectionQueryWrapper.eq("user_id",userId);
            collectionQueryWrapper.eq("cookbook_id",cookbookId);
            collection=collectionService.getOne(collectionQueryWrapper);
            if(collection==null){
                return 0;
            }
            return 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    @Service
    public static class ClickServiceImpl extends ServiceImpl<ClickDao, Click>{
    }
    @Service
    public static class CollectionServiceImpl extends ServiceImpl<CollectionDao, Collection>{

    }
}
