package com.example.yin.util;


import com.example.yin.common.Constants;
import com.example.yin.config.RedisTemplate;

import com.example.yin.model.dto.UserRedisDto;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.Optional;

public class AuthUtil {

    public static final String[] whiteList = new String[]
            {
                    "swagger", "api-docs", "doc.html", "webjars,favicon.ico",
                    "/admin/login/status",
                    "/banner/getAllBanner",
                    "/collection/status",
                    "/comment/song/detail",
                    "/comment/songList/detail",
                    "/user/add", "/user/login/status",
                    "/listSong/detail",
                    "/rankList", "/rankList/user",
                    "/singer", "/singer/name/detail", "/singer/sex/detail", "/song/detail", "/song/singer/detail", "/song/singerName/detail", "/song/recommentSong",
                    "/songList", "/songList/likeTitle/detail", "/songList/style/detail"
            };


    public static boolean agreeRequest(String requestURI) {
        if (!ObjectUtils.isEmpty(whiteList)) {
            for (int i = 0; i < whiteList.length; i++) {
                //在白名单里面之间返回true
                if (whiteList[i].equals(requestURI)) return true;
            }
        }
        return false;
    }

    public static Optional<UserRedisDto> getLoginUser(RedisTemplate redisTemplate) {
        String token = getToken();
        Optional<UserRedisDto> object = redisTemplate.getObject(Constants.TOKEN_PREFIX + token, UserRedisDto.class);
        return object;
    }

    public static String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader(Constants.HEAD_AUTHORIZATION);
        return token;
    }

    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request;
    }

    public static HttpServletResponse getResponse() {
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getResponse();
        return response;
    }

    public static final String[] media = new String[]{
            ".mp3", ".m4a", ".jpg", ".jpeg", ".gif", ".png", ".jfif"
    };

    public static boolean mediaRequest(String requestURI) {
        if (requestURI.equals("/admin/login/status")
                || requestURI.equals("/user/add")
                || requestURI.equals("/user/login/status")
                || requestURI.equals("/error")
        )
            return true;

        int lastIndexOf = requestURI.lastIndexOf(".");
        if (lastIndexOf != -1) {
            String substring = requestURI.substring(lastIndexOf);
            if (!ObjectUtils.isEmpty(media)) {
                for (int i = 0; i < media.length; i++) {
                    //在白名单里面之间返回true
                    if (media[i].equals(substring)) return true;
                }
            }
        }

        return false;
    }
}
