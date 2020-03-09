package com.waxsb.dao.Impl;

import com.waxsb.dao.BaseDao;
import com.waxsb.dao.GroupsDao;
import com.waxsb.model.AddGroups;
import com.waxsb.model.User;
import com.waxsb.model.User_Groups;
import com.waxsb.model.User_GroupsToUser;
import java.sql.Connection;
import java.util.List;

public class GroupsDaoImpl implements GroupsDao {
    private BaseDao baseDao=new BaseDao();
    @Override
    public void createGroup(Connection conn, User_Groups user_groups) {
        String sql = "insert into user_groups(UG_ID,UG_Name,UG_CreateTime,UG_AdminID,UG_Number) value(null,?,?,?,?)";
        baseDao.update(conn,sql,user_groups.getUG_Name(),user_groups.getUG_CreateTime(),user_groups.getUG_AdminID(),user_groups.getUG_Number());
    }

    @Override
    public void updateGroupName(Connection conn, int ug_id, String ug_name) {
        String sql = "update user_groups set UG_Name=? where UG_ID=?";
        baseDao.update(conn,sql,ug_name,ug_id);
    }

    @Override
    public void updateGroupAdmin(Connection conn, int ug_id, int ug_adminID) {
        String sql = "update user_groups set UG_AdminID=? where UG_ID=?";
        baseDao.update(conn,sql,ug_adminID,ug_id);
    }

    @Override
    public User_Groups  getGroupByNum(Connection conn, String ug_number) {
        String sql = "select * from user_groups where UG_Number=?";
        User_Groups user_group = baseDao.getInstance(conn, User_Groups.class, ug_number);
        return user_group;
    }

    @Override
    public List<User_GroupsToUser> getMyGroups(Connection conn, int id) {
        String sql="select * from user_groupstouser where UG_UserID = ? ";
        List<User_GroupsToUser> groups = baseDao.getForList(conn, User_GroupsToUser.class, sql, id);
        if(groups==null||groups.size()==0){
            return null;
        }else{
            for(User_GroupsToUser group : groups){
                int ug_groupID = group.getUG_GroupID();
                //根据群id查询到群
                sql = "select* from user_groups where UG_ID = ?";
                User_Groups user_groups = baseDao.getInstance(conn, User_Groups.class, sql, ug_groupID);
                //将查询到的群分别放入User_GroupsToUser对象中
                group.setUser_Groups(user_groups);
            }
            return groups;
        }
    }

    @Override
    public List<User_GroupsToUser> getUserMsg(Connection conn, int ug_id) {
        String sql = "select * from user_groupstouser where UG_GroupID = ?";
        List<User_GroupsToUser> groups = baseDao.getForList(conn, User_GroupsToUser.class, sql, ug_id);
        //根据用户id获取用户信息
        for(User_GroupsToUser group :groups){
            int uid = group.getUG_UserID();
            sql = "select * from User where id = ?";
            User user = baseDao.getInstance(conn, User.class, sql, uid);
            //把User类储存到User_GroupsToUser中
            group.setUser(user);
        }
        return groups;
    }

    @Override
    public User_Groups getGroupById(Connection conn, int ug_id) {
        String sql = "SELECT * FROM user_groups WHERE UG_id = ? ";
        User_Groups group = baseDao.getInstance(conn, User_Groups.class, sql, ug_id);
        return group;
    }

    @Override
    public void insert(Connection conn, AddGroups addGroups) {
        String sql = "insert into t_addgroup(Uid,UG_ID) value(?,?)";
        baseDao.update(conn,sql,addGroups.getUid(),addGroups.getUG_ID());
    }

    @Override
    public void updateAnnouncement(Connection conn, int ug_id, String announcement) {
        String sql = "update user_groups set announcement=? where UG_ID=?";
        baseDao.update(conn,announcement,ug_id);
    }

    @Override
    public List<AddGroups> findGroupsRespond(Connection conn, int id) {
        String sql=" select * from t_addgroup t where t.Uid = ? and t.isAllow is not null";
        List<AddGroups> addGroupsResp=baseDao.getForList(conn,AddGroups.class,sql,id);
        if(addGroupsResp==null||addGroupsResp.size()==0){
            return null;
        }else{
            return addGroupsResp;
        }
    }

    @Override
    public List<User_GroupsToUser> getManager(Connection conn, int id) {
        String sql = "select * from user_groupstouser where UG_UserID = ? and level is not null";
        List<User_GroupsToUser> List = baseDao.getForList(conn, User_GroupsToUser.class, sql, id);
        if(List==null||List.size()==0){
            return null;
        }else{
            return List;
        }

    }

    @Override
    public List<AddGroups> findGroupsRequest(Connection conn, int ug_groupID) {
        String sql = "select * from t_addgroup where UG_ID = ? and isAllow is null";
        List<AddGroups> list = baseDao.getForList(conn, AddGroups.class, sql, ug_groupID);
        if(list==null||list.size()==0){
            return null;
        }else {
            return list;
        }
    }

    @Override
    public void updateAddGroup(Connection conn, int uid, int ug_id, String isAllow) {
        String sql ="update t_addgroup set isAllow = ? where Uid=? and UG_ID=?";
        baseDao.update(conn,sql,isAllow,uid,ug_id);
    }

    @Override
    public void insertGroupsToUser(Connection conn, User_GroupsToUser groupsToUser) {
        String sql ="insert into user_groupstouser(UG_UserId,UG_GroupID,UG_CreateTime) value(?,?,?)";
        baseDao.update(conn,sql,groupsToUser.getUG_ID(),groupsToUser.getUG_GroupID(),groupsToUser.getDatetime());
    }

    @Override
    public void deleteMsg(Connection conn, int uid) {
        String sql = " delete from t_addgroup where Uid =? and isAllow is not null";
        baseDao.update(conn,sql,uid);
    }

    @Override
    public void deleteGroup(int ug_userID, int ug_groupID) {
        String sql ="delete from user_groupstouser where UG_UserID = ? and UG_GroupID = ?";

    }

    @Override
    public List<User> getGroupManagers(Connection conn, int ug_groupID) {
        String sql = "SELECT * FROM USER WHERE id IN\n" +
                "( SELECT UG_UserID FROM user_groupstouser WHERE UG_GroupID = ? AND LEVEL IS NOT NULL);";
        List<User> users = baseDao.getForList(conn, User.class, sql, ug_groupID);
        if(users==null||users.size()==0){
            return null;
        }
        return users;
    }

    @Override
    public List<User> getMemberName(Connection conn, int ug_groupId) {
        String sql = "SELECT * FROM USER WHERE id IN ( SELECT UG_UserId FROM user_groupstouser WHERE UG_GroupID = ?)";
        List<User> userList = baseDao.getForList(conn,User.class,sql,ug_groupId);
        if(userList==null||userList.size()==0){
            return null;
        }
        return userList;
    }


}
