package com.kirisaki.example.consumer;

import com.kirisaki.example.common.model.User;
import com.kirisaki.example.common.service.UserService;
import com.kirisaki.kirisakirpc.proxy.ServiceProxyFactory;

/**
 * 简易服务消费者示例
 */
public class EasyConsumerExample {
    public static void main(String[] args) {
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("kirisaki");
        //通过依赖的接口来实现服务的调用?
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
        int number = userService.getNumber();
        System.out.println(number);
    }
}
