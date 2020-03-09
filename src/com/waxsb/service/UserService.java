package com.waxsb.service;

import com.waxsb.model.User;
import com.waxsb.util.Page.PageBean;

public interface UserService {
    boolean register(User user) ;

    User login(User user) ;

    PageBean<User> findUserByPage(String currentPage, String row);

    User updatePassword(User user, String newPassword);

    User updateMessage(User user);

    void updateImg(int id, String image_src);

    User findUserByUsername(String username);

    PageBean<User> findUserBySearchName(String currentPage, String row, String username);
}
