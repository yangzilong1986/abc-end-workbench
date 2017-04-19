package com.abc.dubbo.provider.demo.user;

import com.abc.dubbo.demo.api.user.User;
import com.abc.dubbo.demo.api.user.UserService;

import java.util.concurrent.atomic.AtomicLong;

public class UserServiceImpl implements UserService {

    private final AtomicLong idGen = new AtomicLong();

    public User getUser(Long id) {
        return new User(id, "username" + id);
    }


    public Long registerUser(User user) {
//        System.out.println("Username is " + user.getName());
        return idGen.incrementAndGet();
    }
}
