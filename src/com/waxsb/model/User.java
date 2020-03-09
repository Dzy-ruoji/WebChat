package com.waxsb.model;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class User implements HttpSessionBindingListener {
    private int id;//用户id
    private String username;//用户名，账号
    private String password;//密码
    private String name;//真实姓名
    private Date birthday;//出生日期
    private String gender;//男或女
    private String telephone;//手机号
    private String email;//邮箱
    private String src;//头像

    private List<User_GroupsToUser> user_groupsList;//表示该用户所有的群及对应昵称

    public List<User_GroupsToUser> getUser_groupsList() {
        return user_groupsList;
    }

    public void setUser_groupsList(List<User_GroupsToUser> user_groupsList) {
        this.user_groupsList = user_groupsList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    //将javaBean与session绑定
    //session.setAttribute();
    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        System.out.println(this.getUsername()+"进入了...");
        //通过事件对象获取事件源对象
        HttpSession session = event.getSession();
        //从ServletContext中获得到人员列表的Map集合
        Map<User,HttpSession> userMap= (Map<User, HttpSession>) session.getServletContext().getAttribute("userMap");
        //将用户和对应的的session存入到map集合中
        userMap.put(this,session);
        for (Map.Entry<User, HttpSession> entry : userMap.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            //entry.getKey() ;entry.getValue(); entry.setValue();
            //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
        }
            System.out.println("让我看看里面有多少个userMap="+userMap.size());

    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        System.out.println(this.getUsername()+"退出了..");
        HttpSession session = event.getSession();
        //获得人员列表
        Map<User,HttpSession> userMap= (Map<User, HttpSession>) session.getServletContext().getAttribute("userMap");
        //将用户从Map中移除
        userMap.remove(this);
        for (Map.Entry<User, HttpSession> entry : userMap.entrySet()) {
            //Map.entry<Integer,String> 映射项（键-值对）  有几个方法：用上面的名字entry
            //entry.getKey() ;entry.getValue(); entry.setValue();
            //map.entrySet()  返回此映射中包含的映射关系的 Set视图。
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
        }
    }


}
