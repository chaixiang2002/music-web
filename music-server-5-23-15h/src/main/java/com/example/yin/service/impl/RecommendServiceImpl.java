package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.yin.config.RedisTemplate;
import com.example.yin.mapper.CollectMapper;
import com.example.yin.mapper.SongMapper;
import com.example.yin.mapper.UserSongInteractionRecordMapper;
import com.example.yin.model.domain.Collect;
import com.example.yin.model.domain.Song;
import com.example.yin.model.domain.UserSongInteractionRecord;
import com.example.yin.model.dto.*;
import com.example.yin.service.RecommendService;
import com.example.yin.util.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserSongInteractionRecordMapper userSongInteractionRecordMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private SongMapper songMapper;

    //播放分数表
    public Integer getPlayScore(Long count) {
        if (count < 0) return null;

        if (count >= 4) {
            if (count <= 7) return 20;
            if (count <= 15) return 25;
            return 30;
        } else {
            if (count == 0) return 0;
            if (count == 1) return 8;
            return 16;
        }
    }

    //下载分数表
    public Integer getDownloadScore(Long count) {
        if (count == 0) return 0;
        if (count == 1) return 15;
        if (count == 2) return 20;
        if (count == 3) return 23;
        if (count == 4) return 27;
        else return 30;
    }

    //收藏分数
    public Integer getCollectionScore() {
        return 40;
    }


    public HashMap<Integer, HashMap<Integer, Integer>> getScoreTable() {
        //1、获取全部用户歌曲记录信息  -- 记录用户对操作过的歌曲的下载和播放次数
        List<UserSongInteractionRecord> userSongInteractionRecords = userSongInteractionRecordMapper.selectList(null);

        //2、获取全部用户的歌曲收藏记录
        LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectLambdaQueryWrapper.eq(Collect::getType, 0);
        List<Collect> collects = collectMapper.selectList(collectLambdaQueryWrapper);


        HashMap<Integer, HashMap<Integer, Integer>> hashMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        System.out.println();
        System.out.println("--------------开始计算用户的歌曲下载收听次数分数--------------");
        System.out.println();

        //遍历每一条记录，但是一个用户对应着某首歌最多是一行记录
        userSongInteractionRecords.stream().forEach(
                o -> {

                    Integer consumerId = o.getConsumerId();
                    Integer songId = o.getSongId();
                    Long downloadCount = o.getDownloadCount();
                    Long playbackCount = o.getPlaybackCount();

                    if (!ObjectUtils.isEmpty(songId) && !ObjectUtils.isEmpty(consumerId)) {
                        HashMap<Integer, Integer> integerIntegerHashMap = hashMap.get(consumerId);

                        //第一次放，初始化一下
                        if (ObjectUtils.isEmpty(integerIntegerHashMap)) {
                            integerIntegerHashMap = new HashMap<>();
                            hashMap.put(consumerId, integerIntegerHashMap);
                        }

                        Integer nowScore = integerIntegerHashMap.get(songId);

                        //不用判断，直接放就行了，对于一个用户来说，他对一首歌只会有0~1行记录
                        Integer downloadScore = getDownloadScore(downloadCount);
                        Integer playScore = getPlayScore(playbackCount);
                        integerIntegerHashMap.put(songId, downloadScore + playScore);
                        System.out.println("----------用户:" + consumerId + " -- 对歌曲：" + songId + " -- " + "下载次数:" + downloadCount + " -- " + "收听次数：" + playbackCount);
                        System.out.println("----------用户:" + consumerId + " -- 对歌曲：" + songId + " -- " + "下载评分:" + downloadScore + " -- " + "收听评分：" + playScore);
                    }


                }
        );
        System.out.println();
        System.out.println("--------------用户的歌曲下载收听次数分数计算完毕--------------");
        System.out.println();

        System.out.println();
        System.out.println("--------------开始计算用户的歌曲收藏分数--------------");
        System.out.println();
        //遍历每行记录
        collects.stream().forEach(
                o -> {
                    Integer songId = o.getSongId();
                    Integer userId = o.getUserId();
                    if (!ObjectUtils.isEmpty(songId) && !ObjectUtils.isEmpty(userId)) {
                        HashMap<Integer, Integer> integerIntegerHashMap = hashMap.get(userId);
                        //第一次放，初始化一下
                        if (ObjectUtils.isEmpty(integerIntegerHashMap)) {
                            integerIntegerHashMap = new HashMap<>();
                            hashMap.put(userId, integerIntegerHashMap);
                        }

                        Integer nowScore = integerIntegerHashMap.get(songId);
                        Integer score = null;
                        //如果是空，说明该用户没有收听和下载，只是收藏
                        if (ObjectUtils.isEmpty(nowScore)) {
                            score = getCollectionScore();
                        } else {
                            score = nowScore + getCollectionScore();
                        }
                        integerIntegerHashMap.put(songId, score);
                        System.out.println("----------用户:" + userId + " -- 对歌曲：" + songId + " -- " + "收藏了" + " -- 评分为：" + getCollectionScore() + "-- 对歌曲总评分：" + score);
                    }
                }
        );
        System.out.println();
        System.out.println("--------------用户的歌曲收藏分数已计算完毕--------------");
        System.out.println();
        return hashMap;
    }

    /**
     * 方法描述: 皮尔森（pearson）相关系数计算
     * (x1,y1) 理解为 a 用户对 x 歌曲的评分和对 y 歌曲的评分
     *
     * @param xs
     * @param ys
     * @throws
     * @Return {@link Double}
     */
    public Double getRelate(List<Integer> xs, List<Integer> ys) {
        //1、获取长度，由于两个集合里面size相同，获取一个就行了
        int n = xs.size();
        if (n == 0) return 0.0;

        //2、概率统计 皮尔森相关系数 公式：
        double Ex = xs.stream().mapToDouble(x -> x).sum(); //求X合
        double Ey = ys.stream().mapToDouble(y -> y).sum(); //求Y合
        double avgX = Ex / n; //X平均数
        double avgY = Ey / n; //Y平均数
        double up = IntStream.range(0, n).mapToDouble(i -> (xs.get(i) - avgX) * (ys.get(i) - avgY)).sum();

        double downLeft = Math.sqrt(IntStream.range(0, n).mapToDouble(i -> Math.pow(xs.get(i) - avgX, 2)).sum());
        double downRight = Math.sqrt(IntStream.range(0, n).mapToDouble(i -> Math.pow(ys.get(i) - avgY, 2)).sum());

        double down = downLeft * downRight;

        if (down - 0.0 < 1e-5) {
            return 0.0;
        }
        double result = up / down;
        return result;
    }

    /**
     * 计算两个序列间的相关系数
     *
     * @param xList 当前用户的数据集
     * @param yList 其他用户的数据集
     * @return
     */
    public Double pearson_dis(HashMap<Integer, Integer> xList, HashMap<Integer, Integer> yList) {

        //首先目的是先找出这两个用户歌曲评分表中的歌曲交集，即找出那些他们都评分过的歌曲

        //1、转成List
        Set<Map.Entry<Integer, Integer>> xEntries = xList.entrySet();
        Set<Map.Entry<Integer, Integer>> yEntries = yList.entrySet();

        ArrayList<Map.Entry<Integer, Integer>> xEntriesList = new ArrayList<>(xEntries);
        ArrayList<Map.Entry<Integer, Integer>> yEntriesList = new ArrayList<>(yEntries);

        //2、根据歌曲id进行排序  时间复杂度nlogn
        xEntriesList.sort(
                (o1, o2) -> Integer.compare(o1.getKey(), o2.getKey())
        );

        yEntriesList.sort(
                (o1, o2) -> Integer.compare(o1.getKey(), o2.getKey())
        );

        //3、原算法采用双重循环，时间复杂度n*n  这里采用快排+双指针算法
        int i = 0, j = 0;
        //
        List<Integer> xs = new ArrayList<>();
        List<Integer> ys = new ArrayList<>();

        //  key  歌曲id  value  用户对歌曲的评分  双指针
        while (i < xEntriesList.size()) {
            Integer xkey = xEntriesList.get(i).getKey();
            Integer xvalue = xEntriesList.get(i).getValue();

            //只要有一边结束了，就代表已经匹配完了
            if (j >= yEntriesList.size()) {
                break;
            }

            Integer ykey = yEntriesList.get(j).getKey();
            Integer yvalue = yEntriesList.get(j).getValue();


            if (xkey == ykey) {
                //只要这里按顺序一起放就是对应的
                xs.add(xvalue);
                ys.add(yvalue);

                //由于用户对每首歌只会有一行记录，所以说这里不会有重复的，直接都走就行了
                i++;
                j++;
            }
            //x列中歌曲id大于y列歌曲id，那么只能是y跳才能继续匹配
            else if (xkey > ykey) {
                j++;
            }
            //x列中歌曲id小于y列歌曲id，那么只能是x跳才能继续匹配
            else {
                i++;
            }
        }

        Double relate = getRelate(xs, ys);
        return relate;
    }

    public List<UserPearsonScoreDto> computeNearestKNeighbor(Integer nowUserId, HashMap<Integer, HashMap<Integer, Integer>> scoreTable, Integer k) {
        System.out.println();
        System.out.println("--------------------------开始计算最近的k个邻居--------------------------------");
        System.out.println();
        //1、先转成set
        Set<Map.Entry<Integer, HashMap<Integer, Integer>>> entries = scoreTable.entrySet();

        //获取当前用户自己的歌曲评分表
        HashMap<Integer, Integer> integerIntegerHashMap = scoreTable.get(nowUserId);

        //2、算当前用户与其他用户的相关系数
        ArrayList<UserPearsonScoreDto> userPearsonScores = new ArrayList<>();

        entries.stream().forEach(
                o -> {
                    Integer userId = o.getKey();
                    HashMap<Integer, Integer> userSongScoreTable = o.getValue();

                    //不是自己就算咯
                    if (userId != nowUserId) {
                        //算出相关系数
                        Double aDouble = pearson_dis(integerIntegerHashMap, userSongScoreTable);
                        System.out.println("当前用户" + nowUserId + "与 用户" + userId + " -- 的相关系数为：" + aDouble);
                        UserPearsonScoreDto userPearsonScore = UserPearsonScoreDto.builder()
                                .userId(userId)
                                .personScore(aDouble).build();
                        userPearsonScores.add(userPearsonScore);
                    }
                }
        );

        //3、开始排序，选出前k个相关用户
        userPearsonScores.sort(
                (o1, o2) -> Double.compare(o2.getPersonScore(), o1.getPersonScore())
        );

        System.out.println();

        //4、取前k个临近用户
        ArrayList<UserPearsonScoreDto> reUserPearsonScores = new ArrayList<>();
        for (int i = 0; i < k && i < userPearsonScores.size(); i++) {
            System.out.println("最近的邻居是：" + userPearsonScores.get(i).getUserId() + " -- 皮尔森系数为：" + userPearsonScores.get(i).getPersonScore());
            reUserPearsonScores.add(userPearsonScores.get(i));
        }
        System.out.println();
        System.out.println("--------------------------最近的k个邻居已计算完毕--------------------------------");
        System.out.println();
        return reUserPearsonScores;
    }

    /**
     * 推荐歌曲列表
     *
     * @return {@link List<SongScoreDto>}
     */
    public List<SongScoreDto> recommendSongList() {

        //1、判断用户是否已登录
        Optional<UserRedisDto> loginUser = AuthUtil.getLoginUser(redisTemplate);
        List<SongScoreDto> songScoreDtos = new ArrayList<>();

        //2、用户已登录  -- 混合推荐算法
        if (loginUser.isPresent()) {
            log.info("当前用户已登录，采用混合推荐算法");

            //获取用户歌曲评分数据表
            HashMap<Integer, HashMap<Integer, Integer>> scoreTable = getScoreTable();

            //如果当前网站还没有用户访问

            HashMap<Integer, Integer> integerIntegerHashMap = scoreTable.get(loginUser.get().getId());


            //如果当前用户一首歌的喜欢都没给出，那么直接用流行度推荐算法
            if (ObjectUtils.isEmpty(integerIntegerHashMap)) {
                log.info("当前用户已登录，但是他没有在网站留下记录，采用随机推荐算法推荐8首，流行度推荐算法推荐2首");
                songScoreDtos = popularityRecommendation(2, null);
                List<Integer> excludeIds = songScoreDtos.stream().map(o -> o.getSong().getId()).collect(Collectors.toList());
                List<SongScoreDto> scoreDtos = randomRecommendation(8, excludeIds);
                songScoreDtos.addAll(scoreDtos);
            }

            //有记录，尝试用一下基于用户的协同推荐算法
            else {

                //计算用户歌曲评分数据库表里面各用户与当前用户的 爱好最相近的  k 个用户
                Integer k = 3;

                //已获取前k个爱好相近的用户
                List<UserPearsonScoreDto> userPearsonScores = computeNearestKNeighbor(loginUser.get().getId(), scoreTable, k);

                System.out.println();
                System.out.println("--------------------------已获取到+" + userPearsonScores.size() + "个爱好相近的用户--------------------------------");
                System.out.println();

                //key 为歌曲id  value为一个类 存的是   用户id  与当前用户的相关系数  用户对该歌曲的评分  即歌曲候选表
                HashMap<Integer, ArrayList<Neigbhor>> songToNeigbhor = new HashMap<>();

                //获取当前用户的歌曲评分表
                HashMap<Integer, Integer> nowUserScoreTable = scoreTable.get(loginUser.get().getId());

                //遍历那k个最近的用户，获取数据填入歌曲候选表
                System.out.println();
                System.out.println("--------------------------开始从获取到" + userPearsonScores.size() + "个爱好相近的用户中选取歌曲--------------------------------");
                System.out.println();

                //遍历这k个用户
                for (UserPearsonScoreDto userPearsonScore : userPearsonScores) {
                    Double personScore = userPearsonScore.getPersonScore();

                    //相关系数小于3的话，正相关度太低了，没意义
                    if (personScore < 0.3) {
                        System.out.println();
                        System.out.println("----------用户" + userPearsonScore.getUserId() + "与当前用户皮尔森系数为:" + personScore + " 低于0.3 参考价值低--------------");
                        System.out.println();
                        break;
                    } else {
                        System.out.println();
                        System.out.println("--------------------------用户" + userPearsonScore.getUserId() + "与当前用户皮尔森系数为:" + personScore + "-------------------------------");
                        System.out.println();

                        //选出该用户歌曲评分表中 当前用户没有的那些歌曲
                        HashMap<Integer, Integer> userScoreTable = scoreTable.get(userPearsonScore.getUserId());//歌曲id->评分
                        Set<Map.Entry<Integer, Integer>> entries = userScoreTable.entrySet();

                        //遍历这个用户的全部评分歌曲
                        for (Map.Entry<Integer, Integer> map : entries) {

                            //获得歌曲id
                            Integer key = map.getKey();

                            //获得用户对歌曲的评分
                            Integer value = map.getValue();

                            //从当前用户的歌曲评分表中获取这首歌的评分
                            Integer integer = nowUserScoreTable.get(key);

                            //如果获取不到，那就不在当前用户的歌曲评分表里面 ,就可以   放入歌曲候选表里面
                            if (ObjectUtils.isEmpty(integer)) {
                                ArrayList<Neigbhor> neigbhors = songToNeigbhor.get(key);

                                //空的话创建一个
                                if (ObjectUtils.isEmpty(neigbhors)) {
                                    neigbhors = new ArrayList<>();
                                }

                                Neigbhor neigbhor = Neigbhor.builder().userId(userPearsonScore.getUserId())
                                        .relate(personScore)
                                        .score(value).build();

                                System.out.println("歌曲：" + key + "已放入，此用户与当前用户的相关系数：" + personScore + " -- 此用户对歌曲的评分：" + value);
                                neigbhors.add(neigbhor);
                                songToNeigbhor.put(key, neigbhors);
                            }
                        }

                    }
                }

                //开始计算在歌曲候选表中的歌曲平均预估值
                List<SongScoreDto> temp = new ArrayList<>();

                Set<Map.Entry<Integer, ArrayList<Neigbhor>>> entries = songToNeigbhor.entrySet();
                entries.stream().forEach(
                        o -> {
                            //歌曲id
                            Integer key = o.getKey();
                            //多个用户与当前用户的相关系数以及那些用户对歌曲的评分
                            ArrayList<Neigbhor> value = o.getValue();

                            //这里不能直接用其他用户的评分，这里采用与当前用户的  相关系数*其他用户对歌曲的评分作为预估值
                            Double score = Double.valueOf(0);

                            //对分数的综合带上 相关系数 以更准确的预测
                            for (Neigbhor neigbhor : value) {
                                score += neigbhor.getScore() * neigbhor.getRelate();
                            }

                            //再取一个平均值，避免这首歌因为人数多而加起来就多
                            score = score / value.size();

                            SongScoreDto songScoreDto = new SongScoreDto();
                            Song song = new Song();
                            song.setId(key);
                            songScoreDto.setSong(song);
                            songScoreDto.setScore(score);
                            temp.add(songScoreDto);
                        }
                );

                //再从歌曲候选表中选出前7首，
                temp.sort(
                        (o1, o2) -> Double.compare(o2.getScore(), o1.getScore())
                );

                int x = 0;

                for (int i = 0; x < 7 && i < temp.size(); i++) {
                    SongScoreDto songScoreDto = temp.get(i);
                    Integer id = songScoreDto.getSong().getId();
                    Song song = songMapper.selectById(id); //这里如果数据库  数据完整性没保证可以会出错
                    if (!ObjectUtils.isEmpty(song)) {
                        x++;
                        songScoreDto.setSong(song);
                        songScoreDtos.add(songScoreDto);
                        System.out.println();
                        System.out.println("歌曲：" + id + "已推荐成功，歌曲评分是" + songScoreDto.getScore());
                        System.out.println();
                    }
                }

                //10-掉协同推荐出的歌曲数，剩下的用流行度算法推荐
                int size = ObjectUtils.isEmpty(songScoreDtos) ? 0 : songScoreDtos.size();
                int sub = 10 - size;
                System.out.println();
                System.out.println("基于用户的协同推荐算法计算完毕，已推荐歌曲数：" + size);
                System.out.println();

                //取出已出歌曲中的那些id
                List<Integer> excludeIds = null;
                if (!ObjectUtils.isEmpty(songScoreDtos))
                    excludeIds = songScoreDtos.stream().map(o -> o.getSong().getId()).collect(Collectors.toList());

                //如果基于用户的协同推荐不出来，那只能是8--2 流行度推荐+随机推荐
                if (size == 0) {
                    System.out.println();
                    System.out.println("基于用户的协同推荐算法推荐歌曲为0，转成 2--8 流行度推荐+随机推荐");
                    System.out.println();
                    songScoreDtos = popularityRecommendation(2, null);
                    List<Integer> excludeIds1 = songScoreDtos.stream().map(o -> o.getSong().getId()).collect(Collectors.toList());
                    List<SongScoreDto> scoreDtos1 = randomRecommendation(8, excludeIds);

                    songScoreDtos.addAll(scoreDtos1);

                } else {
                    System.out.println();
                    System.out.println("基于用户的协同推荐算法计算完毕，已推荐歌曲数：" + size + " -- 需从流行度推荐算法中推荐1首歌" + "从流行度算法中推荐" + 1 + "首歌");
                    System.out.println();
                    List<SongScoreDto> scoreDtos = popularityRecommendation(1, excludeIds);
                    excludeIds.add(scoreDtos.get(0).getSong().getId());
                    System.out.println();
                    System.out.println("基于用户的协同推荐算法计算完毕，已推荐歌曲数：" + size + " -- 需从流行度推荐算法中推荐1首歌" + "从随机算法中推荐" + (sub - 1) + "首歌");
                    System.out.println();
                    List<SongScoreDto> scoreDtos1 = randomRecommendation(sub - 1, excludeIds);

                    songScoreDtos.addAll(scoreDtos);
                    songScoreDtos.addAll(scoreDtos1);
                }
            }
        }


        //3、用户未登录 --  流行度推荐算法 + 随机推荐算法
        else {
            log.info("当前用户未登录，采用随机推荐算法推荐8首，流行度推荐算法推荐2首");
            songScoreDtos = popularityRecommendation(2, null);
            List<Integer> excludeIds = songScoreDtos.stream().map(o -> o.getSong().getId()).collect(Collectors.toList());
            List<SongScoreDto> scoreDtos = randomRecommendation(8, excludeIds);
            songScoreDtos.addAll(scoreDtos);
        }

        return songScoreDtos;
    }

    /**
     * 随机推荐歌曲列表
     *
     * @return {@link List<SongScoreDto>}
     */
    public List<SongScoreDto> randomRecommendation(int n, List<Integer> excludeSongIds) {
        if (n == 0) return null;

        //获取所有歌曲
        List<Song> songs = songMapper.selectList(null);

        List<SongScoreDto> temp = new ArrayList<>();
        System.out.println();
        System.out.println("--------------------------随机推荐算法开始计算--------------------------------");
        System.out.println();
        songs.stream().forEach(
                o -> {
                    //随机生成一个1~100之间的分数
                    Double score = Math.random() * 100;
                    SongScoreDto songScoreDto = new SongScoreDto();
                    songScoreDto.setSong(o);
                    songScoreDto.setScore(score);
                    System.out.println("随机为歌曲：" + o.getId() + " -- 评分：" + score);
                    temp.add(songScoreDto);
                }
        );
        System.out.println();
        System.out.println("--------------------------随机推荐算法计算完毕--------------------------------");
        System.out.println();
        //按照分数逆序排序
        temp.sort(
                (o1, o2) -> Double.compare(o2.getScore(), o1.getScore())
        );

        List<SongScoreDto> scoreDtos = new ArrayList<>();

        boolean flag = ObjectUtils.isEmpty(excludeSongIds);
        int g = 0;

        //取前n首歌曲进行推荐
        for (int i = 0; g < n && i < temp.size(); i++) {
            Integer id = temp.get(i).getSong().getId();

            //排除ids不为空，且当前歌曲id不在里面，就可以推荐 或者排除ids为空
            if (flag || (!flag && !excludeSongIds.contains(id))) {
                scoreDtos.add(temp.get(i));
                System.out.println("歌曲" + id + "已成功推荐");
                g++;
            }
        }

        return scoreDtos;
    }

    /**
     * 流行度推荐歌曲列表
     *
     * @return {@link List<SongScoreDto>}
     */
    public List<SongScoreDto> popularityRecommendation(int n, List<Integer> excludeSongIds) {
        if (n == 0) return null;

        Double downloadRatio = 0.4;
        Double playRatio = 0.3;
        Double collectionRatio = 0.4;
        System.out.println();
        System.out.println("--------------------------流行度推荐算法开始计算--------------------------------");
        System.out.println();
        //先获取歌曲 下载总次数  收听平总次数
        List<SongMulScoreDto> songScore = userSongInteractionRecordMapper.getSongScore();

        //获取歌曲收藏次数
        List<SongCollectionDto> songCollectCount = collectMapper.getSongCollectCount();

        //开始算分
        HashMap<Integer, Double> songScoreTable = new HashMap<>();

        //第一部分
        songScore.stream().forEach(
                o -> {
                    Integer songId = o.getSongId();
                    Double downloadScore = o.getDownloadScore();
                    Double playbackScore = o.getPlaybackScore();

                    //由于sql里面是对songid分组，所有不会存在重复的问题
                    Double score = downloadScore * downloadRatio + playbackScore * playRatio;
                    System.out.println("歌曲：" + songId + " -- " + "下载次数：" + downloadScore + " -- 播放次数：" + playbackScore + " -- 评分：" + score);
                    songScoreTable.put(songId, score);
                }
        );

        //第二部分
        songCollectCount.stream().forEach(
                o -> {
                    Integer songId = o.getSongId();
                    Integer collectionCount = o.getCollectionCount();

                    //这里有可能是第一次放进去，有可能不是，需要判断
                    Double aDouble = songScoreTable.get(songId);

                    //如果是空的，说明第一次放进去，否则加上前面的累计值
                    Double score = ObjectUtils.isEmpty(aDouble) ? collectionCount * collectionRatio : aDouble + collectionCount * collectionRatio;

                    System.out.println("歌曲：" + songId + " -- " + "收藏次数：" + collectionCount + " -- 总评分：" + score);
                    songScoreTable.put(songId, score);
                }
        );

        //转成set
        Set<Map.Entry<Integer, Double>> entries = songScoreTable.entrySet();
        ArrayList<Map.Entry<Integer, Double>> scoreTable = new ArrayList<>(entries);

        //按分数逆序排序
        scoreTable.sort(
                (o1, o2) -> Double.compare(o2.getValue(), o1.getValue())
        );

        ArrayList<SongScoreDto> songs = new ArrayList<>();
        boolean flag = ObjectUtils.isEmpty(excludeSongIds);
        int g = 0;

        for (int i = 0; i < scoreTable.size() && g < n; i++) {
            Map.Entry<Integer, Double> integerDoubleEntry = scoreTable.get(i);
            Integer key = integerDoubleEntry.getKey(); //歌曲id
            Song song = songMapper.selectById(key);
            if (!ObjectUtils.isEmpty(song)) {
                //如果排除ids非空，且当前这首歌又不在里面了，才推荐了   或者不需要排除
                if (flag || (!flag && !excludeSongIds.contains(key))) {
                    SongScoreDto songScoreDto = new SongScoreDto();
                    songScoreDto.setSong(song);
                    songScoreDto.setScore(integerDoubleEntry.getValue());
                    System.out.println("歌曲：" + key + " -- 已推荐");
                    songs.add(songScoreDto);
                    g++;
                }
            }
        }

        return songs;
    }

}
