- 项目背景
- 安装
- 使用
- Badge
- 相关项目（可选）
- 主要项目负责人
- 参与贡献方式
- 开源协议

## 项目背景
      学习ssm框架后，想利用自己的所学的知识与朋友一起制作一个云音乐网站，主要实现以下功能：  

- 用token用户身份验证结合redis对当前用户登录状态记录，实现在线挤退
- 文件的上传和下载
- 根据用户收藏的歌单和歌手以及听歌次数，对用户进行协同推荐算法
- 对用户对歌曲、歌单的恶意评论进行拦截，如脏话等
- 歌曲歌词页面实现随歌曲的播放进行同步，对当前歌词进行高亮

技术栈  
前端：
vue3+TS+vuex+vue-router
后端：
springboot+mysql+redis
## 运行
1. 后端运行  
> $ java -jar .\music-web\music-server-5-23-15h\target\yin-0.0.1-SNAPSHOT.jar
2. 前端（用户端）
>  cd .\music-web\music-client\
>  npm run serve
3. 前端（管理员后台）
>  cd .\music-web\music-manage\
>  npm run serve


## 项目展示
1. 首页
      ![a](../music-web/img/首页.png)
2. 歌曲播放页面
   ![a](../music-web/img/歌曲播放页面.png)
3. 歌手

   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/歌手.png)
4. 歌单


   ![a]([../music-web/img](https://github.com/chaixiang2002/music-web/tree/master/img)/歌单.png)

5. 歌单详情

   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/%E6%AD%8C%E5%8D%95%E8%AF%A6%E6%83%85.png)
6. 推荐
   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/推荐.png)
7. 用户个人资料
   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/用户个人资料.png)

## 后台

8. 后台首页
   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/后台首页.png)
9. 后台歌手歌曲

   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/后台歌手歌曲.png)
10. 后台歌手

   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/后台歌手.png)
   11. 后台歌单的删选
   ![a](https://github.com/chaixiang2002/music-web/tree/master/img/后台歌单的删选.png)

