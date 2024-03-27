package com.kirisaki.example.provider;

import com.kirisaki.example.common.model.User;
import com.kirisaki.example.common.service.UserService;

/**
 * 用户服务实现类
 */
public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户用" + user.getName());
        return user;
    }
}
