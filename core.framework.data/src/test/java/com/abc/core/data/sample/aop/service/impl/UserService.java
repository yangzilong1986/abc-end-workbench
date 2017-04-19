package com.abc.core.data.sample.aop.service.impl;

import org.springframework.stereotype.Service;

import com.abc.core.data.sample.aop.service.IUserService;

@Service
public class UserService implements IUserService{
    
    public void add(){
        System.out.println("UserService add()");
    }
    
    public boolean delete(){
        System.out.println("UserService delete()");
        return true;
    }
    
    public void edit(){
        System.out.println("UserService edit()");
    }
 
    
}
