package com.waxsb.service.Impl;

import com.waxsb.dao.GroupsDao;
import com.waxsb.dao.Impl.GroupsDaoImpl;
import com.waxsb.model.AddGroups;
import com.waxsb.model.User;
import com.waxsb.model.User_Groups;
import com.waxsb.model.User_GroupsToUser;
import com.waxsb.service.GroupsService;
import com.waxsb.util.Database.JDBCUtils;
import com.waxsb.util.Page.PageBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class GroupsServiceImpl implements GroupsService {

    private GroupsDao dao = new GroupsDaoImpl();
    @Override
    public boolean createGroup(User_Groups user_groups) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        boolean flag = false;
        try {
            User_Groups group = dao.getGroupByNum(conn, user_groups.getUG_Number());
            flag = false;
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
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
        return flag;
    }

    @Override
    public void updateGroupName(int ug_id, String ug_name) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.updateGroupName(conn,ug_id,ug_name);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public void updateGroupAdmin(int ug_id, int ug_adminID) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.updateGroupAdmin(conn,ug_id,ug_adminID);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public List<User_GroupsToUser> getMyGroups(int id) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //根据用户id查询得到了群id、入群时间、群昵称
        List<User_GroupsToUser> groups = null;
        try {
            groups = dao.getMyGroups(conn,id);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
        return groups;
    }

    @Override
    public List<User_GroupsToUser> getUserMsg(int ug_id) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //根据群id查询得到了所有用户信息及其群昵称
        List<User_GroupsToUser> Users = null;
        try {
            Users = dao.getUserMsg(conn,ug_id);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
        return Users;
    }

    @Override
    public User_Groups getGroupById(int ug_id) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        User_Groups user_group = null;
        try {
            user_group = dao.getGroupById(conn,ug_id);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
        return user_group;
    }

    @Override
    public User_Groups getGroupByNum(String ug_number) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        User_Groups user_group = null;
        try {
            user_group = dao.getGroupByNum(conn,ug_number);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
        return user_group;
    }

    @Override
    public void addGroup(AddGroups addGroups) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.insert(conn,addGroups);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public void updateAnnouncement(int ug_id, String announcement) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.updateAnnouncement(conn,ug_id,announcement);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public List<AddGroups> responseMessage(int id) {
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<AddGroups> GroupRespond = null;
        try {
            GroupRespond = dao.findGroupsRespond(conn,id);
            if(GroupRespond!=null){
                for(AddGroups group : GroupRespond){
                    //获取群id
                    int ug_id = group.getUG_ID();
                    //根据群id获取群信息
                    User_Groups user_groups = dao.getGroupById(conn, ug_id);
                    group.setUser_groups(user_groups);
                }
            }
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        finally {
            JDBCUtils.closeResource(conn,null);
        }
        return GroupRespond;
    }

    @Override
    public List<User_GroupsToUser> isManager(int id) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<User_GroupsToUser> groupsToUser = null;
        try {
            groupsToUser = dao.getManager(conn,id);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
        return groupsToUser;
    }

    @Override
    public List<AddGroups> findGroupRequest(int ug_groupID) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<AddGroups> groups = null;
        try {
            groups = dao.findGroupsRequest(conn,ug_groupID);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
        return groups;
    }

    @Override
    public void updateReq(int uid, int ug_id, String isAllow) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
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
                conn.commit();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public void deleteMsg(int uid) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            dao.deleteMsg(conn,uid);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public void exitMyGroup(int ug_userID, int ug_groupID) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //删除User_GroupsToUser中的数据
        try {
            dao.deleteGroup(ug_userID,ug_groupID);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
    }

    @Override
    public List<User> getManager(int ug_groupID) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<User> users= null;
        try {
            users = dao.getGroupManagers(conn,ug_groupID);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
        return users;
    }

    @Override
    public List<User> memberName(int ug_groupId) {
        Connection conn=JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<User> usernames= null;
        try {
            usernames = dao.getMemberName(conn,ug_groupId);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn,null);
        }
        return usernames;
    }

    @Override
    public PageBean<User_Groups> getGroupByNameOrNum(String _currentPage, String _row, String groupname) {
        int currentPage=Integer.parseInt(_currentPage);
        int rows=Integer.parseInt(_row);
        Connection conn = JDBCUtils.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(currentPage<=0){
            currentPage=1;
        }
        //1.创建空的PageBean对象
        PageBean<User_Groups> pb=new PageBean<User_Groups>();
        //2.设置参数
        pb.setCurrentPage(currentPage);
        pb.setRows(rows);

        try {
            //3.调用Dao查询总记录数
            int totalCount=dao.getGroupByNameOrNumCount(conn,groupname);
            pb.setTotalCount(totalCount);

            //4.调用Dao查询List集合
            //计算开始的记录索引
            int start=(currentPage-1)*rows;
            List<User_Groups> list=dao.getGroupByNameOrNum(conn,start,rows,groupname);
            pb.setList(list);
            //5.计算总页码
            int totalPage=(totalCount%rows)==0? (totalCount/rows):(totalCount/rows)+1;
            pb.setTotalPage(totalPage);
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }  finally {
            JDBCUtils.closeResource(conn,null);
        }
        return pb;
    }
}
