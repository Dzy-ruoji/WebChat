package com.waxsb.service.Impl;

import com.waxsb.dao.GroupsDao;
import com.waxsb.dao.Impl.GroupsDaoImpl;
import com.waxsb.model.AddGroups;
import com.waxsb.model.User;
import com.waxsb.model.User_Groups;
import com.waxsb.model.User_GroupsToUser;
import com.waxsb.service.GroupsService;
import com.waxsb.util.Database.JDBCUtils;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

public class GroupsServiceImpl implements GroupsService {

    private GroupsDao dao = new GroupsDaoImpl();
    @Override
    public boolean createGroup(User_Groups user_groups) {
        Connection conn=JDBCUtils.getConnection();
        User_Groups group = dao.getGroupByNum(conn, user_groups.getUG_Number());
        boolean flag = false;
        if(group==null){
            dao.createGroup(conn,user_groups);
            //将群主添加到关联表，并且设置等级为2
            User_GroupsToUser groupsToUser = new User_GroupsToUser();
            groupsToUser.setUG_GroupID(user_groups.getUG_ID());
            groupsToUser.setUG_UserID(user_groups.getUG_AdminID());
            groupsToUser.setDatetime(user_groups.getUG_CreateTime());
            groupsToUser.setLevel("2");
            dao.insertGroupsToUser(conn,groupsToUser);
            flag = true;
        }else{
            flag = false;
        }
        JDBCUtils.closeResource(conn,null);
        return flag;
    }

    @Override
    public void updateGroupName(int ug_id, String ug_name) {
        Connection conn=JDBCUtils.getConnection();
        dao.updateGroupName(conn,ug_id,ug_name);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void updateGroupAdmin(int ug_id, int ug_adminID) {
        Connection conn=JDBCUtils.getConnection();
        dao.updateGroupAdmin(conn,ug_id,ug_adminID);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public List<User_GroupsToUser> getMyGroups(int id) {
        Connection conn = JDBCUtils.getConnection();
        //根据用户id查询得到了群id、入群时间、群昵称
        List<User_GroupsToUser> groups = dao.getMyGroups(conn,id);
        JDBCUtils.closeResource(conn,null);
        return groups;
    }

    @Override
    public List<User_GroupsToUser> getUserMsg(int ug_id) {
        Connection conn = JDBCUtils.getConnection();
        //根据群id查询得到了所有用户信息及其群昵称
        List<User_GroupsToUser> Users = dao.getUserMsg(conn,ug_id);
        JDBCUtils.closeResource(conn,null);
        return Users;
    }

    @Override
    public User_Groups getGroupById(int ug_id) {
        Connection conn = JDBCUtils.getConnection();
        User_Groups user_group = dao.getGroupById(conn,ug_id);
        JDBCUtils.closeResource(conn,null);
        return user_group;
    }

    @Override
    public User_Groups getGroupByNum(String ug_number) {
        Connection conn = JDBCUtils.getConnection();
        User_Groups user_group = dao.getGroupByNum(conn,ug_number);
        JDBCUtils.closeResource(conn,null);
        return user_group;
    }

    @Override
    public void addGroup(AddGroups addGroups) {
        Connection conn = JDBCUtils.getConnection();
        dao.insert(conn,addGroups);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void updateAnnouncement(int ug_id, String announcement) {
        Connection conn = JDBCUtils.getConnection();
        dao.updateAnnouncement(conn,ug_id,announcement);
    }

    @Override
    public List<AddGroups> responseMessage(int id) {
        Connection conn = JDBCUtils.getConnection();
        List<AddGroups> GroupRespond = dao.findGroupsRespond(conn,id);
        if(GroupRespond!=null){
            for(AddGroups group : GroupRespond){
                //获取群id
                int ug_id = group.getUG_ID();
                //根据群id获取群信息
                User_Groups user_groups = dao.getGroupById(conn, ug_id);
                group.setUser_groups(user_groups);
            }
        }
        JDBCUtils.closeResource(conn,null);
        return GroupRespond;
    }

    @Override
    public List<User_GroupsToUser> isManager(int id) {
        Connection conn=JDBCUtils.getConnection();
        List<User_GroupsToUser> groupsToUser = dao.getManager(conn,id);
        JDBCUtils.closeResource(conn,null);
        return groupsToUser;
    }

    @Override
    public List<AddGroups> findGroupRequest(int ug_groupID) {
        Connection conn=JDBCUtils.getConnection();
        List<AddGroups> groups = dao.findGroupsRequest(conn,ug_groupID);
        JDBCUtils.closeResource(conn,null);
        return groups;
    }

    @Override
    public void updateReq(int uid, int ug_id, String isAllow) {
        Connection conn=JDBCUtils.getConnection();
        dao.updateAddGroup(conn,uid,ug_id,isAllow);
        if(isAllow=="1"){
            //同意，则更新user_groupstouser表
            User_GroupsToUser groupsToUser = new User_GroupsToUser();
            groupsToUser.setUG_GroupID(ug_id);
            groupsToUser.setUG_UserID(uid);
            java.util.Date date = new java.util.Date();          // 获取一个Date对象
            Timestamp timeStamp = new Timestamp(date.getTime());
            groupsToUser.setDatetime(timeStamp);
            dao.insertGroupsToUser(conn,groupsToUser);
        }
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void deleteMsg(int uid) {
        Connection conn=JDBCUtils.getConnection();
        dao.deleteMsg(conn,uid);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public void exitMyGroup(int ug_userID, int ug_groupID) {
        Connection conn=JDBCUtils.getConnection();
        //删除User_GroupsToUser中的数据
        dao.deleteGroup(ug_userID,ug_groupID);
        JDBCUtils.closeResource(conn,null);
    }

    @Override
    public List<User> getManager(int ug_groupID) {
        Connection conn=JDBCUtils.getConnection();
        List<User> users= dao.getGroupManagers(conn,ug_groupID);
        return users;
    }

    @Override
    public List<User> memberName(int ug_groupId) {
        Connection conn=JDBCUtils.getConnection();
        List<User> usernames= dao.getMemberName(conn,ug_groupId);
        return usernames;
    }
}
