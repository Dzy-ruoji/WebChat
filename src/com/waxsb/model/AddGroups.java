package com.waxsb.model;

public class AddGroups {
    private int Uid;//申请入群用户id
    private int UG_ID;//申请入群的群id
    private String isAllow;//群主是否同意  0不同意，null未受理，1同意
    private User_Groups user_groups;

    public User_Groups getUser_groups() {
        return user_groups;
    }

    public void setUser_groups(User_Groups user_groups) {
        this.user_groups = user_groups;
    }

    public int getUid() {
        return Uid;
    }

    public void setUid(int uid) {
        Uid = uid;
    }

    public int getUG_ID() {
        return UG_ID;
    }

    public void setUG_ID(int UG_ID) {
        this.UG_ID = UG_ID;
    }

    public String getIsAllow() {
        return isAllow;
    }

    public void setIsAllow(String isAllow) {
        this.isAllow = isAllow;
    }
}
