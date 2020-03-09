package com.waxsb.service;

import com.waxsb.model.AddGroups;
import com.waxsb.model.User;
import com.waxsb.model.User_Groups;
import com.waxsb.model.User_GroupsToUser;

import java.util.List;

public interface GroupsService {

    boolean createGroup(User_Groups user_groups);

    void updateGroupName(int ug_id, String ug_name);

    void updateGroupAdmin(int ug_id, int ug_adminID);

    List<User_GroupsToUser> getMyGroups(int id);

    List<User_GroupsToUser> getUserMsg(int ug_id);

    User_Groups getGroupById(int ug_id);

    User_Groups getGroupByNum(String ug_number);

    void addGroup(AddGroups addGroups);

    void updateAnnouncement(int ug_id, String announcement);

    List<AddGroups> responseMessage(int id);

    List<User_GroupsToUser> isManager(int id);

    List<AddGroups> findGroupRequest(int ug_groupID);

    void updateReq(int uid, int ug_id, String isAllow);

    void deleteMsg(int uid);

    void exitMyGroup(int ug_userID, int ug_groupID);

    List<User> getManager(int ug_groupID);

    List<User> memberName(int ug_groupId);
}
