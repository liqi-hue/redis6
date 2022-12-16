package com.atguigu.service;

import com.atguigu.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author: liqi
 * @create 2022-12-15 6:30 PM
 */

public interface UserService extends IService<User> {

    void addUser(User user);

    void deleteUser(Integer id);

    void updateUser(User user);

    User findUserById(Integer id);
}
