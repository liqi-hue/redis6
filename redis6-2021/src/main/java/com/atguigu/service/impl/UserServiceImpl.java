package com.atguigu.service.impl;

import com.atguigu.entity.User;
import com.atguigu.mapper.UserMapper;
import com.atguigu.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: liqi
 * @create 2022-12-15 6:30 PM
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    public static final String CACHE_KEY_USER = "user:";

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public void addUser(User user) {
        // 先成功插入mysql
        int i = baseMapper.insert(user);
        if (i > 0) {
            //保证mysql与redis数据一致
            user = baseMapper.selectById(user.getId());
            String key = CACHE_KEY_USER + user.getId();
            redisTemplate.opsForValue().set(key, user);
        }
    }

    @Override
    public void deleteUser(Integer id) {
        boolean b = removeById(id);
        String key = CACHE_KEY_USER + id;
        if (b) {
            redisTemplate.delete(key);
        }
    }

    @Override
    public void updateUser(User user) {
        int i = baseMapper.updateById(user);
        if (i > 0) {
            user = baseMapper.selectById(user.getId());
            String key = CACHE_KEY_USER + user.getId();
            redisTemplate.opsForValue().set(key, user);
        }
    }

    //高并发存在缓存击穿
    @Override
    public User findUserById(Integer id) {
        String key = CACHE_KEY_USER + id;
        //先查redis,无数据查mysql,此处如果redis 的key过期了大流量下会导致缓存击穿，mysql将承受很大压力
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user == null) {
            //mysql无数据直接返回
            user = baseMapper.selectById(id);
            if (user == null) {
                return user;
            } else {
                //mysql有数据写入redis
                redisTemplate.opsForValue().set(key, user);
            }
        }
        return user;
    }
    //解决高并发存在缓存击穿的问题
    public User findUserById2(Integer id) {
        String key = CACHE_KEY_USER + id;
        //先查redis,无数据查mysql
        User user = (User) redisTemplate.opsForValue().get(key);
        if (user == null) {
            //使用双重检查解决缓存击穿问题
            synchronized (User.class) {
                user = (User) redisTemplate.opsForValue().get(key);
                if (user == null) {
                    user = baseMapper.selectById(id);
                    if (user == null) {
                        return user;
                    } else {
                        //mysql有数据写入redis
                        redisTemplate.opsForValue().setIfAbsent(key, user,7L, TimeUnit.DAYS);
                    }
                }
            }
        }
        return user;
    }
}
