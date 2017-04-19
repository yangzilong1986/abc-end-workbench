package com.abc.dubbo.demo.api.user;

public interface UserService {
    User getUser(Long id);

    Long registerUser(User user);
}
