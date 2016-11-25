package com.teamh.huddleout;

import java.util.ArrayList;

/**
 * Created by Paul on 22/11/2016.
 */

public class User {

    private int profileID;

    private String firstName;
    private String lastName;

    private String token;

    ArrayList<Integer> groupIDs = new ArrayList<Integer>();

    ArrayList<User> friendsList = new ArrayList<>();
    
    public User(int profileID) {

    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void createFriendsLis() {

    }

}
