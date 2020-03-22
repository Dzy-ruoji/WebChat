package com.waxsb.dao;

import com.waxsb.model.AddGroups;
import com.waxsb.model.User;
import com.waxsb.model.User_Groups;
import com.waxsb.model.User_GroupsToUser;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface GroupsDao {
    void createGroup(Connection conn, User_Groups user_groups) throws SQLException;

    void updateGroupName(Connection conn, int ug_id, String ug_name) throws SQLException;

    void updateGroupAdmin(Connection conn, int ug_id, int ug_adminID) throws SQLException;

    User_Groups getGroupByNum(Connection conn,String ug_number) throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException;

    List<User_GroupsToUser> getMyGroups(Connection conn, int id) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    List<User_GroupsToUser> getUserMsg(Connection conn, int ug_id) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    User_Groups getGroupById(Connection conn, int ug_id) throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException;

    void insert(Connection conn, AddGroups addGroups) throws SQLException;

    void updateAnnouncement(Connection conn, int ug_id, String announcement) throws SQLException;

    List<AddGroups> findGroupsRespond(Connection conn, int id) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    List<User_GroupsToUser> getManager(Connection conn, int uid) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    List<AddGroups> findGroupsRequest(Connection conn,int ug_groupID) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    void updateAddGroup(Connection conn, int uid, int ug_id, String isAllow) throws SQLException;

    void insertGroupsToUser(Connection conn, User_GroupsToUser groupsToUser) throws SQLException;

    void deleteMsg(Connection conn, int uid) throws SQLException;

    void deleteGroup(int ug_userID, int ug_groupID);

    List<User> getGroupManagers(Connection conn, int ug_groupID) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    List<User> getMemberName(Connection conn, int ug_groupId) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;

    int getGroupByNameOrNumCount(Connection conn, String groupname) throws SQLException;

    List<User_Groups> getGroupByNameOrNum(Connection conn, int start, int rows, String groupname) throws SQLException, InstantiationException, IllegalAccessException, NoSuchFieldException;
}
