package com.example.yin.service.impl;

import com.example.yin.config.RedisTemplate;
import com.example.yin.common.Constants;
import com.example.yin.mapper.SongMapper;
import com.example.yin.model.domain.Song;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.service.IndexServie;
import com.example.yin.service.UserSongInteractionRecordService;
import com.example.yin.util.AuthUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

@Service
public class IndexServiceImpl implements IndexServie {

    @Resource
    private SongMapper songMapper;

    @Resource
    private UserSongInteractionRecordService userSongInteractionRecordService;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void downloadSong(Integer songId) {
        //1、查询文件信息
        Song song = songMapper.selectById(songId);
        if (ObjectUtils.isEmpty(song)) {
            return;
        }
        String url = song.getUrl();

        try {

            // 2、通过文件输入流读取文件
            String path = Constants.LOCAL_SONG_PATH + url;
            FileInputStream is = new FileInputStream(new File(path));

            HttpServletResponse response = AuthUtil.getResponse();

            // 3. 获取响应输出流
            response.setContentType("application/force-download;charset=UTF-8");

            // 4. 附件下载 attachment 附件 inline 在线打开(默认值)
            response.setHeader("content-disposition", "attachment;fileName=" + song.getName());
            // 5. 处理下载流复制
            ServletOutputStream os = null;
            os = response.getOutputStream();
            int len;
            byte[] b = new byte[1024];
            while (true) {
                len = is.read(b);
                if (len == -1) break;
                os.write(b, 0, len);
            }
            // 释放资源
            os.close();
            is.close();

            // 6、添加歌曲下载数量
            Optional<UserRedisDto> loginUser = AuthUtil.getLoginUser(redisTemplate);
            if (loginUser.isPresent()) {
                boolean flag = userSongInteractionRecordService.updateOrAddDownlocalCount(loginUser.get().getId(),songId);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
