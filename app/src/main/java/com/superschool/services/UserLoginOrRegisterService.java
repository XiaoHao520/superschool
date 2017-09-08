package com.superschool.services;

import com.superschool.entity.User;

import java.io.IOException;

/**
 * Created by XIAOHAO on 2017/5/26.
 */

public interface UserLoginOrRegisterService{
    public int userLoginOrRegisterService(User user) throws IOException;
    public User checkHasUserexisted() throws IOException;
}
