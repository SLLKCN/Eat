package com.example.eat;

import com.example.eat.util.RecommendUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EatApplicationTests {

    @Test
    void contextLoads() {
        RecommendUtil recommendUtil=new RecommendUtil();
        System.out.println(recommendUtil.getCoobookRecommend(2));
        System.out.println(recommendUtil.getMusicRecommend(1));
        System.out.println(recommendUtil.getMusicListRecommend(2));
    }

}
