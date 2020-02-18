package com.waxsb.util;

import com.waxsb.model.User;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class LockHelper {
    static Map<String, HttpSession> map=new HashMap<String, HttpSession>();
    public static void putSession(HttpSession session){
        User user=(User)session.getAttribute("user");
        map.put(user.getUsername(), session);
    }
    public static void moveSession(HttpSession session){
        User user=(User)session.getAttribute("user");
        map.remove(user.getUsername());
    }
    public static void destroyedSession(User user){
        HttpSession session=map.get(user.getUsername());
        session.invalidate();
    }
}

