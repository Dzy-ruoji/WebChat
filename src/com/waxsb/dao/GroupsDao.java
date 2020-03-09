package com.waxsb.dao;

import com.waxsb.model.AddGroups;
import com.waxsb.model.User;
import com.waxsb.model.User_Groups;
import com.waxsb.model.User_GroupsToUser;
import java.sql.Connection;
import java.util.List;

public interface GroupsDao {
    void createGroup(Connection conn, User_Groups user_groups);

    void updateGroupName(Connection conn, int ug_id, String ug_name);

    void updateGroupAdmin(Connection conn, int ug_id, int ug_adminID);

    User_Groups getGroupByNum(Connection conn,String ug_number);

    List<User_GroupsToUser> getMyGroups(Connection conn, int id);

    List<User_GroupsToUser> getUserMsg(Connection conn, int ug_id);

    User_Groups getGroupById(Connection conn, int ug_id);

    void insert(Connection conn, AddGroups addGroups);

    void updateAnnouncement(Connection conn, int ug_id, String announcement);

    List<AddGroups> findGroupsRespond(Connection conn, int id);

    List<User_GroupsToUser> getManager(Connection conn, int uid);

    List<AddGroups> findGroupsRequest(Connection conn,int ug_groupID);

    void updateAddGroup(Connection conn, int uid, int ug_id, String isAllow);

    void insertGroupsToUser(Connection conn, User_GroupsToUser groupsToUser);

    void deleteMsg(Connection conn, int uid);

    void deleteGroup(int ug_userID, int ug_groupID);

    List<User> getGroupManagers(Connection conn, int ug_groupID);

    List<User> getMemberName(Connection conn, int ug_groupId);
}
