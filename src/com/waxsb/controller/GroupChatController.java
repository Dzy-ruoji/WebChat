package com.waxsb.controller;

import com.waxsb.model.AddGroups;
import com.waxsb.model.User;
import com.waxsb.model.User_Groups;
import com.waxsb.model.User_GroupsToUser;
import com.waxsb.service.GroupsService;
import com.waxsb.service.Impl.GroupsServiceImpl;
import com.waxsb.util.Json.MyJson;
import com.waxsb.util.Json.ResultInfo;
import com.waxsb.util.Page.PageBean;
import com.waxsb.util.Page.PageMsg;
import net.sf.json.JSONObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

//获取消息类需要改为websocket
@WebServlet("/groupChat/*")
public class GroupChatController extends BaseServlet {
    private static ResultInfo resInfo=new ResultInfo();
    private static GroupsService groupsSer= new GroupsServiceImpl();
    private static ThreadLocal<ResultInfo> resultInfo=new ThreadLocal<ResultInfo>();
    private static ThreadLocal<GroupsService> groupsService=new ThreadLocal<GroupsService>();
    public static ResultInfo getResultInfo() {
        resultInfo.set(resInfo);
        return resultInfo.get();
    }
    public static GroupsService getGroupsService(){
        groupsService.set(groupsSer);
        return groupsService.get();
    }

    //先验证再修改
    public void createGroup(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("createGroup..");
        JSONObject jsonObject = MyJson.getJson(req);
        ResultInfo resultInfo = getResultInfo();
        GroupsService groupsService = getGroupsService();
        if(jsonObject==null){
            System.out.println("没有获得json数据");
            ResultInfo.ResponseFail("创建失败");
        }else{
            User_Groups user_groups = new User_Groups();
            String UG_Number = (String) jsonObject.get("UG_Number");//群号码
            String UG_Name = (String) jsonObject.get("UG_Name");//群名称
            int  UG_AdminID = (int) jsonObject.get("UG_AdminID");//群主id
            java.util.Date date = new java.util.Date();          // 获取一个Date对象
            Timestamp timeStamp = new Timestamp(date.getTime());
            user_groups.setUG_AdminID(UG_AdminID);
            user_groups.setUG_Name(UG_Name);
            user_groups.setUG_Number(UG_Number);
            user_groups.setUG_CreateTime(timeStamp);
            boolean flag=groupsService.createGroup(user_groups);
            if(flag){
                resultInfo = ResultInfo.ResponseSuccess("创建成功");
            }else{
                resultInfo = ResultInfo.ResponseSuccess("创建失败，群号已被使用");
            }
        }
        MyJson.returnJson(resultInfo,resp);
    }

    public void updateGroupName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("updateGroupName..");
        JSONObject jsonObject = MyJson.getJson(req);
        ResultInfo resultInfo = getResultInfo();
        GroupsService groupsService = getGroupsService();
        if(jsonObject==null){
            System.out.println("没有获得json数据");
            ResultInfo.ResponseFail("创建失败");
        }else{
            User_Groups user_groups = new User_Groups();
            String UG_Name = (String) jsonObject.get("UG_Name");//新群名称
            int  UG_ID = (int) jsonObject.get("UG_ID");//群id
            groupsService.updateGroupName(UG_ID,UG_Name);
            resultInfo = ResultInfo.ResponseSuccess("创建成功");
        }
        MyJson.returnJson(resultInfo,resp);
    }

    public void updateGroupAdmin(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("updateGroupAdmin..");
        JSONObject jsonObject = MyJson.getJson(req);
        ResultInfo resultInfo = getResultInfo();
        GroupsService groupsService = getGroupsService();
        if(jsonObject==null){
            System.out.println("没有获得json数据");
            ResultInfo.ResponseFail("创建失败");
        }else{
            User_Groups user_groups = new User_Groups();
            int  UG_AdminID  = (int) jsonObject.get("UG_AdminID");//新群主id
            int  UG_ID = (int) jsonObject.get("UG_ID");//群id
            groupsService.updateGroupAdmin(UG_ID,UG_AdminID);
            resultInfo = ResultInfo.ResponseSuccess("创建成功");
        }
        MyJson.returnJson(resultInfo,resp);
    }

    public void getMyGroups(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("getMyGroups...");
        User user = (User) req.getSession().getAttribute("user");
        ResultInfo resultInfo = getResultInfo();
        GroupsService groupsService = getGroupsService();
        //根据用户id返回用户所有群
        List<User_GroupsToUser> MyGroups = groupsService.getMyGroups(user.getId());
        //将用户所在的所有群及群昵称赋值给User类
        user.setUser_groupsList(MyGroups);
        req.getSession().removeAttribute("user");
        req.getSession().setAttribute("user",user);
        resultInfo = ResultInfo.ResponseSuccess(user);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("getMyGroupsSuccess");
    }

    public void getAllNickname(HttpServletRequest req,HttpServletResponse resp) throws IOException {
         System.out.println("getAllNickname");
        JSONObject jsonObject = MyJson.getJson(req);
        //根据群id获得群的所有用户信息及昵称
        int UG_ID = (int) jsonObject.get("groupID");
        GroupsService groupsService = getGroupsService();
        ResultInfo resultInfo = getResultInfo();
        //根据群id查询群信息
        User_Groups user_groups = groupsService.getGroupById(UG_ID);
        //根据群id返回群中所有用户信息
        List<User_GroupsToUser> UserMsg= groupsService.getUserMsg(UG_ID);
        //将群中的用户及昵称赋值给User_Groups类
        user_groups.setUser_groupsList(UserMsg);
        resultInfo = ResultInfo.ResponseSuccess(user_groups);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("getAllNicknameSuccess");
    }

    public void findGroup(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("findGroup");
        JSONObject jsonObject = MyJson.getJson(req);
        GroupsService groupsService = getGroupsService();
        ResultInfo resultInfo = getResultInfo();
        //根据群号查找群
        String UG_Number =(String)jsonObject.get("UG_Number");
        User_Groups user_group = groupsService.getGroupByNum(UG_Number);
         resultInfo = ResultInfo.ResponseSuccess(user_group);
         MyJson.returnJson(resultInfo,resp);
         System.out.println("findGroupSuccess");
    }

    public void updateAnnouncement(HttpServletRequest req,HttpServletResponse resp){
        System.out.println("update");
        //前校验此人是群主（有一定权限）允许修改后，所传回修改的群公告信息
        JSONObject jsonObject = MyJson.getJson(req);
        GroupsService groupsService = getGroupsService();
        int ug_id = (int) jsonObject.get("UG_ID");
        String announcement = (String) jsonObject.get("Announcement");
        groupsService.updateAnnouncement(ug_id,announcement);
    }

    public void addGroup(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("addGroup");
        JSONObject jsonObject = MyJson.getJson(req);
        GroupsService groupsService = getGroupsService();
        ResultInfo resultInfo = getResultInfo();
        User user = (User) req.getSession().getAttribute("user");
        //获取前端用户输入的群号（实际上是id）
        int UG_ID =(int)jsonObject.get("UG_ID");
        AddGroups addGroups = new AddGroups();
        addGroups.setUG_ID(UG_ID);
        addGroups.setUid(user.getId());
        //添加群操作
        groupsService.addGroup(addGroups);
        resultInfo = ResultInfo.ResponseSuccess("请求已经发出，请耐心等待");
        MyJson.returnJson(resultInfo,resp);
        System.out.println("addGroupSuccess");
    }

    public void findGroupRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("findFriendRequest...");
        //获取当前登录的用户,如果是群主或管理员则会收到他人添加的群请求
        User user = (User) req.getSession().getAttribute("user");
        int id = user.getId();
        GroupsService groupsService = getGroupsService();
        ResultInfo resultInfo = getResultInfo();
        List<List<AddGroups>>list = null;
        //判断请求中的群主或管理员和此用户id是否对应
        //1.先查询该用户是否是群的管理员或群主，如果是，则查询该群的请求信息
        List<User_GroupsToUser> groupsToUser = groupsService.isManager(id);
        if(groupsToUser!=null){
            for(User_GroupsToUser group : groupsToUser){
                //获取当前用户所管理的群id
                int ug_groupID = group.getUG_GroupID();
                //根据群id获取他人申请的群信息
                List<AddGroups> addGroups=groupsService.findGroupRequest(ug_groupID);
                if(addGroups==null){
                    System.out.println(ug_groupID+"暂无消息");
                }else {
                     list.add(addGroups);
                }
            }
            if(list.size()>0){
                System.out.println("真的一个消息都没");
            }else{
                resultInfo = ResultInfo.ResponseSuccess(list);
                MyJson.returnJson(resultInfo,resp);
            }
        }
        System.out.println("findGroupRequestSuccess");
    }

    public void findGroupResponse(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("findGroupResp...");
        //获取当前登录的用户
        User user = (User) req.getSession().getAttribute("user");
        GroupsService groupsService = getGroupsService();
        ResultInfo resultInfo = getResultInfo();
        //返回了添加群后群的回应（1同意、0拒绝）
        List<AddGroups> respMessageList = groupsService.responseMessage(user.getId());
        resultInfo = ResultInfo.ResponseSuccess("这是respMessageList",respMessageList);
        MyJson.returnJson(resultInfo,resp);
        //将respMessageList返回给前端
        System.out.println("respMessageSuccess");
    }

    public void isAllowReq(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        System.out.println("群主或管理员同意或拒绝请求用户入群请求");
        GroupsService groupsService = getGroupsService();
        ResultInfo resultInfo = getResultInfo();
        //前端接收到消息通知后再确定是否同意，所以前端能得知申请入群的用户id和群id
        JSONObject jsonObject = MyJson.getJson(req);
        int uid = (int) jsonObject.get("Uid");
        int ug_id = (int) jsonObject.get("UG_ID");
        String isAllosw = (String) jsonObject.get("isAllosw");
        //改变AddGroups表中为0/1
        groupsService.updateReq(uid,ug_id,isAllosw);
        resultInfo = ResultInfo.ResponseSuccess("操作完成");
        MyJson.returnJson(resultInfo,resp);
        System.out.println("isAllowSuccess");
    }

    public void deleteReqRespMsg(HttpServletRequest req,HttpServletResponse resp){
        System.out.println("deleteReqResp...");
        //主动发出邀请后的用户得到回答后才有资格删掉(一件全删)
        User user = (User) req.getSession().getAttribute("user");
        GroupsService groupsService = getGroupsService();
        groupsService.deleteMsg(user.getId());

    }

    public void exitMyGroup(HttpServletRequest req,HttpServletResponse resp){
        //退出群操作、退出后通知群主和管理员
        System.out.println("exit..");
        JSONObject jsonObject = MyJson.getJson(req);
        int ug_groupID = (int)jsonObject.get("UG_GroupID");
        User user = (User) req.getSession().getAttribute("user");
        int UG_UserID = user.getId();
        GroupsService groupsService = getGroupsService();
        groupsService.exitMyGroup(UG_UserID,ug_groupID);
        //发送信息给管理员或群主,根据群id
        List<User> userlist = groupsService.getManager(ug_groupID);
        //还没写完，
    }

    public void findGroupByNameOrNum(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        PageBean<User_Groups> groupList = PageMsg.GetGroupByNameOrNum(req,resp);
        ResultInfo resultInfo=getResultInfo();
        resultInfo= ResultInfo.ResponseSuccess(groupList);
        MyJson.returnJson(resultInfo,resp);
        System.out.println("查询群流程结束");
    }


}
