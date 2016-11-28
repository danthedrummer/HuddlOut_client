package com.teamh.huddleout;

import com.android.volley.RequestQueue;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Paul on 22/11/2016.
 */

public class User {

    final HuddlOutAPI hAPI;
    private static User user;

    private int profileID;

    private String firstName;
    private String lastName;
    private String profilePicture;
    private String token;

    ArrayList<Integer> groupIDs = new ArrayList<Integer>();

    ArrayList<User> friendsList = new ArrayList<>();
    
    public User(int profileID, Context context) {
        this.profileID = profileID;
        hAPI = HuddlOutAPI.getInstance(context);
    }


    public static synchronized User getInstance(int profileId, Context context){
        if(user == null){
            user = new User(profileId, context);
        }
        return user;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void createFriendsLis() {
        RequestQueue reQueue = hAPI.getFriends();
    }

}
