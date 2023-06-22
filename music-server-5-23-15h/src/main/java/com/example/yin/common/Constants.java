package com.example.yin.common;

public class Constants {
    /* 歌曲图片，歌手图片，歌曲文件，歌单图片等文件的存放路径 */
    public static String DIR_PATH = System.getProperty("user.dir");
    public static String ASSETS_PATH = DIR_PATH + (System.getProperty("os.name").toLowerCase().startsWith("win") ? "" : "");
    public static String AVATOR_IMAGES_PATH = "file:" + ASSETS_PATH + "/img/avatorImages/";
    public static String SONGLIST_PIC_PATH = "file:" + ASSETS_PATH + "/img/songListPic/";
    public static String SONG_PIC_PATH = "file:" + ASSETS_PATH + "/img/songPic/";
    public static String SONG_PATH = "file:" + ASSETS_PATH + "/song/";

    public static String LOCAL_SONG_PATH = ASSETS_PATH;
    public static String SINGER_PIC_PATH = "file:" + ASSETS_PATH + "/img/singerPic/";
    public static String BANNER_PIC_PATH = "file:" + ASSETS_PATH + "/img/swiper/";

    public static String HEAD_AUTHORIZATION = "token";

    public static String TOKEN_PREFIX = "token:";
    public static Long TOKEN_TIME = 60L * 60 * 6;

    public static String ID_PREFIX = "user:";

    /* 盐值加密 */
    public static String SALT = "zyt";

    public static String USER_BASE_IMAGE = "/img/avatorImages/user.jpg";

    public static String BASE_SONG_LIST_IMAGE = "/img/songListPic/123.jpg";

    public static String SONG_IMAGE="/img/songPic/tubiao.jpg";

    public static String SENSITIVE_FILE_NAME = "sensitive_file";
}
