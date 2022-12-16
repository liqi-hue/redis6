package com.atguigu.controller;

import com.atguigu.entity.User;
import com.atguigu.entity.UserDTO;
import com.atguigu.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Random;
import java.util.UUID;

/**
 * @author: liqi
 * @create 2022-12-16 3:02 PM
 */
@RestController
@RequestMapping("/user")
public class UserController {


    @Resource
    private UserService userService;

    @GetMapping("/addUser")
    public void addUser() {
        for (int i = 1; i < 6; i++) {
            User user = new User();
            user.setId(i);
            user.setUsername("zzyy" + i);
            user.setPassword(UUID.randomUUID().toString().substring(0,6));
            user.setSex((byte) new Random().nextInt(2));
            userService.addUser(user);
        }
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @PutMapping("/update")
    public void update(@RequestBody UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        userService.updateUser(user);
    }
    //若使用redis-cli客户端查中文 请使用 redis-cli --raw 命令连接redis-server
    @GetMapping("/findById/{id}")
    public User findById(@PathVariable Integer id) {
        return userService.findUserById(id);
    }
}
