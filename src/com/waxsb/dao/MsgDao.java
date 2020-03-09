package com.waxsb.dao;

import com.waxsb.model.SocketMsg;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public interface MsgDao {

    void insertContext(Connection conn, SocketMsg socketMsg);

    int findPublicTotalCount(Connection conn, int user_groupID, Date date);

    List<SocketMsg> findPublicMsg(Connection conn, int start, int rows, int user_groupID, Date date);

    int findPrivateTotalCount(Connection conn, String fromUser, String toUser, Date date);

    List<SocketMsg> findPrivateMsg(Connection conn, int start, int rows, String fromUser, String toUser, Date date);

    void deleteMsg(Connection conn);
}
