package com.waxsb.service;
import com.waxsb.model.SocketMsg;
import com.waxsb.util.Page.PageBean;

import java.sql.Date;

public interface MsgContextService {

    void insertContext(SocketMsg socketMsg);

    PageBean<SocketMsg> findPublicMsg(int currentPage, int row, int user_groupID, Date date);

    PageBean<SocketMsg> findPrivateMsg(int currentPage, int row, String fromUser, String toUser, Date date);

    void deleteMsg();
}
