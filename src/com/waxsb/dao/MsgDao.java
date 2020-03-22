package com.waxsb.dao;

import com.waxsb.model.SocketMsg;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public interface MsgDao {

    void insertContext(Connection conn, SocketMsg socketMsg) throws SQLException;

    int findPublicTotalCount(Connection conn, int user_groupID, Date date) throws SQLException;

    List<SocketMsg> findPublicMsg(Connection conn, int start, int rows, int user_groupID, Date date) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    int findPrivateTotalCount(Connection conn, String fromUser, String toUser, Date date) throws SQLException;

    List<SocketMsg> findPrivateMsg(Connection conn, int start, int rows, String fromUser, String toUser, Date date) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    void deleteMsg(Connection conn);

    List<SocketMsg> findContacts(Connection conn, String username) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;
}
