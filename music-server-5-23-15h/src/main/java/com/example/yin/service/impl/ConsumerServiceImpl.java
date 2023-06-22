package com.example.yin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.yin.common.R;
import com.example.yin.config.RedisTemplate;
import com.example.yin.common.Constants;
import com.example.yin.mapper.*;
import com.example.yin.model.domain.*;
import com.example.yin.model.dto.*;
import com.example.yin.service.ConsumerService;
import com.example.yin.util.AuthUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.yin.common.Constants.SALT;

@Service
public class ConsumerServiceImpl extends ServiceImpl<ConsumerMapper, Consumer>
        implements ConsumerService {

    @Autowired
    private ConsumerMapper consumerMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CommentMapper commentMapper;

    @Resource
    private UserSupportMapper userSupportMapper;

    @Autowired
    private RankListMapper rankListMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private UserSongInteractionRecordMapper userSongInteractionRecordMapper;

    /**
     * 新增用户
     */
    @Override
    public R addUser(ConsumerRegistryDto registryRequest) {
        //1、先判断用户是否已存在
        if (this.existUserName(registryRequest.getUsername())) {
            return R.error(701, "用户名已存在");
        }

        if (!ObjectUtils.isEmpty(registryRequest.getEmail()) && this.existUserEmail(registryRequest.getEmail())) {
            return R.error(702, "该邮箱已注册账号，请勿重复注册");
        }

        if (!ObjectUtils.isEmpty(registryRequest.getPhoneNum()) && this.existUserPhone(registryRequest.getPhoneNum())) {
            return R.error(703, "该手机号码已注册账号，请勿重复注册");
        }

        //注册的信息
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(registryRequest, consumer);

        //2、设置用户信息
        String password = DigestUtils.md5DigestAsHex((SALT + registryRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
        consumer.setPassword(password);
        //
        if (ObjectUtils.isEmpty(consumer.getPhoneNum())) {
            consumer.setPhoneNum(null);
        }
        if (ObjectUtils.isEmpty(consumer.getEmail())) {
            consumer.setEmail(null);
        }

        //默认用户头像
        consumer.setAvator(Constants.USER_BASE_IMAGE);

        try {
            if (consumerMapper.insert(consumer) > 0) {
                return R.success("注册成功");
            } else {
                return R.error("注册失败");
            }
        } catch (DuplicateKeyException e) {
            return R.fatal(e.getMessage());
        }
    }

    @Override
    public R updateUserMsg(UserUpdateDto updateRequest) {
        Consumer consumer = new Consumer();
        BeanUtils.copyProperties(updateRequest, consumer);
        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败");
        }
    }

    @Override
    public R updatePassword(UpdatePasswordDto updatePasswordRequest) {

        //1、先验证当前用户密码是否正确
        if (!this.verityPasswd(updatePasswordRequest.getUsername(), updatePasswordRequest.getOldPassword())) {
            return R.error("密码输入错误");
        }

        //2、验证完毕，修改密码即可

        //如果新密码和旧密码相同，就不改了
        if (updatePasswordRequest.getPassword().equals(updatePasswordRequest.getOldPassword())) {
            return R.error("新密码不能与旧密码相同");
        }


        Consumer consumer = new Consumer();
        consumer.setId(updatePasswordRequest.getId());
        String secretPassword = DigestUtils.md5DigestAsHex((SALT + updatePasswordRequest.getPassword()).getBytes(StandardCharsets.UTF_8));
        consumer.setPassword(secretPassword);

        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("密码修改成功");
        } else {
            return R.error("密码修改失败");
        }
    }

    @Override
    public R updateUserAvator(MultipartFile avatorFile, int id) {
        //1、找出文件
        String suffix = null;
        if (ObjectUtils.isEmpty(avatorFile) || avatorFile.getSize() <= 0) {
            return R.error("文件不能为空");
        } else {
            String originalFilename = avatorFile.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            if (lastIndexOf != -1) {
                suffix = originalFilename.substring(lastIndexOf + 1, originalFilename.length());
                if (ObjectUtils.isEmpty(suffix)) return R.error("文件类型不符合,只能上传png、jpg、jpeg、gif文件");
                else {
                    if (!(suffix.equals("png") || suffix.equals("jpg")) || suffix.equals("jpeg") || suffix.equals("gif"))
                        return R.error("文件类型不符合,只能上传png、jpg、jpeg、gif文件");
                }
            } else {
                return R.error("文件类型不符合,只能上传png、jpg、jpeg、gif文件");
            }
        }
        String fileName = UUID.randomUUID() + "." + suffix;//加个时间在前面，防止重复
        //路径 他这个会根据你的系统获取对应的文件分隔符
        String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + "img" + System.getProperty("file.separator") + "avatorImages";
        File file = new File(filePath);
        if (!file.exists() && !file.mkdir()) {
            return R.fatal("创建文件失败");
        }
        File dest = new File(filePath + System.getProperty("file.separator") + fileName);
        String imgPath = "/img/avatorImages/" + fileName;

        //2、上传
        try {
            avatorFile.transferTo(dest);
        } catch (IOException e) {
            return R.fatal("上传失败" + e.getMessage());
        }

        //3、更新用户资料
        Consumer consumer = new Consumer();
        consumer.setId(id);
        consumer.setAvator(imgPath);
        if (consumerMapper.updateById(consumer) > 0) {
            return R.success("上传成功", imgPath);
        } else {
            return R.error("上传失败");
        }
    }

    @Override
    public boolean existUserName(String userName) {
        //根据用户名、邮箱、电话号码 查询用户是否存在
        LambdaQueryWrapper<Consumer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Consumer::getUsername, userName);
        return consumerMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existUserEmail(String email) {
        //根据用户名、邮箱、电话号码 查询用户是否存在
        LambdaQueryWrapper<Consumer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Consumer::getEmail, email);

        return consumerMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existUserPhone(String phone) {
        //根据用户名、邮箱、电话号码 查询用户是否存在
        LambdaQueryWrapper<Consumer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Consumer::getPhoneNum, phone);
        return consumerMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean verityPasswd(String username, String password) {
        LambdaQueryWrapper<Consumer> consumerLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //对比md5摘要后的密码就行了
        String secretPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));

        consumerLambdaQueryWrapper.eq(Consumer::getUsername, username)
                .eq(Consumer::getPassword, secretPassword);
        return consumerMapper.selectCount(consumerLambdaQueryWrapper) > 0;
    }

    // 删除用户
    @Override
    @Transactional
    public R deleteUser(Integer id) {
        //1、删掉用户的点赞信息
        //1.1、查询当前用户全部点赞信息
        LambdaQueryWrapper<UserSupport> userSupportLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userSupportLambdaQueryWrapper.eq(UserSupport::getUserId, id);
        List<UserSupport> userSupports = userSupportMapper.selectList(userSupportLambdaQueryWrapper);

        //1.2、调整那些用户点赞过的评论的赞数
        for (UserSupport userSupport : userSupports) {
            Integer commentId = userSupport.getCommentId();
            Comment comment = new Comment();
            comment.setId(commentId);

            //1.3、查询评论所有赞记录
            LambdaQueryWrapper<UserSupport> userSupportLambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            userSupportLambdaQueryWrapper1.eq(UserSupport::getUserId, id);
            userSupportLambdaQueryWrapper1.eq(UserSupport::getCommentId, commentId);
            Long aLong = userSupportMapper.selectCount(userSupportLambdaQueryWrapper1);
            int count = Math.toIntExact((aLong));
            comment.setUp(count > 0 ? count - 1 : 0);

            //1.4、调整
            commentMapper.updateById(comment);

        }

        //2、删除用户点赞记录
        LambdaUpdateWrapper<UserSupport> userSupportLambdaUpdateWrapper1 = new LambdaUpdateWrapper<>();
        userSupportLambdaUpdateWrapper1.eq(UserSupport::getUserId, id);
        userSupportMapper.delete(userSupportLambdaUpdateWrapper1);


        //3、删掉用户自己的发出的评论信息
        //3.1、查询出全部该用户的评论id
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getUserId, id);
        List<Comment> comments = commentMapper.selectList(commentLambdaQueryWrapper);

        //3.2、评论对应的点赞信息
        if (!ObjectUtils.isEmpty(comments)) {
            List<Integer> collect = comments.stream().map(o -> o.getId()).collect(Collectors.toList());
            LambdaUpdateWrapper<UserSupport> userSupportLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            userSupportLambdaUpdateWrapper.in(!ObjectUtils.isEmpty(collect), UserSupport::getCommentId, collect);
            //2.3、删除评论对应的点赞信息
            userSupportMapper.delete(userSupportLambdaUpdateWrapper);
            //2.4、删除评论
            commentMapper.deleteBatchIds(collect);
        }


        //4、删除用户的歌单评分记录
        LambdaUpdateWrapper<RankList> rankListLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        rankListLambdaUpdateWrapper.eq(RankList::getConsumerId, id);
        rankListMapper.delete(rankListLambdaUpdateWrapper);

        //5、删掉用户的收藏信息
        LambdaUpdateWrapper<Collect> collectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        collectLambdaUpdateWrapper.eq(Collect::getUserId, id);
        collectMapper.delete(collectLambdaUpdateWrapper);

        //6、删掉用户的歌曲交互信息
        LambdaUpdateWrapper<UserSongInteractionRecord> userSongInteractionRecordLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userSongInteractionRecordLambdaUpdateWrapper.eq(UserSongInteractionRecord::getConsumerId, id);
        userSongInteractionRecordMapper.delete(userSongInteractionRecordLambdaUpdateWrapper);


        //7、删除用户头像文件
        Consumer consumer = consumerMapper.selectById(id);
        if (!ObjectUtils.isEmpty(consumer)) {
            if (!consumer.getAvator().equals(Constants.USER_BASE_IMAGE)) {
                String filePath = Constants.ASSETS_PATH + System.getProperty("file.separator") + consumer.getAvator();
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                }
            }

            //8、删除用户
            if (consumerMapper.deleteById(id) > 0) {
                //删掉用户在redis里面的信息代表退出

                redisTemplate.remove(
                        Constants.ID_PREFIX + "1:" + consumer.getId());

                return R.success("删除成功");
            } else {
                return R.error("删除失败");
            }
        }
        return R.success("删除成功");
    }

    @Override
    public R allUser() {
        return R.success(null, consumerMapper.selectList(null));
    }

    @Override
    public R userOfId(Integer id) {
        LambdaQueryWrapper<Consumer> consumerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        consumerLambdaQueryWrapper.eq(Consumer::getId, id);
        return R.success(null, consumerMapper.selectList(consumerLambdaQueryWrapper));
    }

    @Override
    public R loginStatus(UserLoginDto loginRequest) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        //验证账号密码
        if (this.verityPasswd(username, password)) {
            //验证通过
            Consumer consumer = new Consumer();
            consumer.setUsername(username);

            //查询用户信息
            Consumer consumer1 = consumerMapper.selectOne(new QueryWrapper<>(consumer));
            HashMap<String, Object> hashMap = new HashMap<>();

            //生成token
            String token = UUID.randomUUID().toString();

            hashMap.put("token", token);

            UserRedisDto userRedisDto = new UserRedisDto();
            BeanUtils.copyProperties(consumer1, userRedisDto);
            userRedisDto.setType(1);


            hashMap.put("user", userRedisDto);

            // 把新的数据加入redis，存一个token->user
            redisTemplate.setObject(Constants.TOKEN_PREFIX + token, userRedisDto, Constants.TOKEN_TIME);

            //再存一个id->token
            redisTemplate.set(Constants.ID_PREFIX + "1:" + consumer1.getId(), token, Constants.TOKEN_TIME);


            return R.success("登录成功", hashMap);

//            return R.success("登录成功", consumerMapper.selectList(new QueryWrapper<>(consumer)));
        } else {
            return R.error("用户名或密码错误");
        }
    }
}
