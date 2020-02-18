package com.waxsb.service;

import com.waxsb.model.User;
import com.waxsb.util.Page.PageBean;

public interface UserService {
    boolean register(User user) ;

    User login(User user) ;

    PageBean<User> findUserByPage(String currentPage, String row);
}
