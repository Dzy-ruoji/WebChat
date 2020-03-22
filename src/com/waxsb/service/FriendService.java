package com.waxsb.service;

import com.waxsb.model.AddFriend;
import com.waxsb.model.Friend;

import java.util.List;

public interface FriendService {
    List<Friend> findFriendByName(String username);

    void addFriend(AddFriend addFriend);

    List<AddFriend> findFriendRequest(String username);

    List<AddFriend> responseMessage(String username);

    void friendReqResp(AddFriend addFriend);

    void allowFriendReq(Friend friend);

    void deleteMsg(String friend_1);

    void deleteMyFriend(String myName,String friendName);

    void updateNickname(String myName, String friendName, String nickname);

    String findNickname(String myName, String friendName);
}
