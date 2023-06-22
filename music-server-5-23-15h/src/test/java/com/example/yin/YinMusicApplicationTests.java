package com.example.yin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.yin.config.RedisTemplate;
import com.example.yin.mapper.CollectMapper;
import com.example.yin.mapper.SongMapper;
import com.example.yin.mapper.UserSongInteractionRecordMapper;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.domain.UserSongInteractionRecord;
import com.example.yin.model.dto.SongScoreDto;
import com.example.yin.service.RecommendService;
import com.example.yin.service.impl.ConsumerServiceImpl;
import com.example.yin.util.SensitiveUtil;

import com.example.yin.util.SensitiveWordCheckUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YinMusicApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserSongInteractionRecordMapper userSongInteractionRecordMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private RecommendService recommendService;

    @Test
    public void t1(){

    }


    @Test
    public void consumerTest2() {
//        HashMap<Integer, HashMap<Integer, Integer>> scoreTable = recommendService.getScoreTable();
//        System.out.println(1);

        List<SongScoreDto> scoreDtos = recommendService.recommendSongList();

        System.out.println();
        System.out.println("----------------");
        System.out.println();
        scoreDtos.forEach(
                o -> {
                    Integer id = o.getSong().getId();
                    String name = o.getSong().getName();
                    double score = o.getScore();
                    System.out.println(id + " -- " + name + " -- " + score);
                }
        );
    }

}
