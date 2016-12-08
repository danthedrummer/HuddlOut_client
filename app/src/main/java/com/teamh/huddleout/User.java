package com.teamh.huddleout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Paul on 22/11/2016.
 */

public class User {

    private static final String TAG = "DevMsg";

    final HuddlOutAPI hAPI;
    private static User user;

    private int profileID;
    private String firstName;
    private String lastName;
    private int age;
    private String description;

    private String profilePicture;
    private String privacy;
    private Context context;

    ArrayList<JSONObject> groupsList;
    ArrayList<JSONObject> friendsList;
    ArrayList<JSONObject> friendRequests;

    public User(int profileID, Context context) {
        this.profileID = profileID;
        hAPI = HuddlOutAPI.getInstance(context);
        this.context = context;
        friendsList = new ArrayList<JSONObject>();
        groupsList = new ArrayList<JSONObject>();
        friendRequests = new ArrayList<JSONObject>();
        hAPI.getProfile(profileID);
        hAPI.getGroups();
        hAPI.getFriends();
        hAPI.getFriendRequests();
    }

    public static synchronized User getInstance(int profileId, Context context){
        user = new User(profileId, context);
        return user;
    }


    public static synchronized User getInstance(Context context) {
        if (user == null) {
            return null;
        }
        return user;
    }

    public void setProfileInformation(final String profileInfo) {
        if (hAPI.getAuth()) {
            try {
                JSONObject profileJSON = new JSONObject(profileInfo);
                Log.i(TAG, "profile info: " + profileJSON.toString());
                firstName = (String) profileJSON.get("first_name");
                lastName = (String) profileJSON.get("last_name");
                profilePicture = (String) profileJSON.get("profile_picture");
                age = (Integer) profileJSON.get("age");
                description = (String) profileJSON.get("description");
                privacy = (String) profileJSON.get("privacy");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "profile not authorised");
        }
    }


    // instantiates the arraylist of the profileIds of the user's friends
    public void setFriendsList(String friends) {
        if (!friendsList.isEmpty()) {
            friendsList.clear();
        }

        try {
            JSONArray profileJSON = new JSONArray(friends);
            for (int i = 0; i < profileJSON.length(); i++) {
                friendsList.add(profileJSON.getJSONObject(i));
            }
        } catch (JSONException e) {
            Log.i(TAG, "setFriendsList failure: " + e);
            e.printStackTrace();
        }
    }


    // insantiates the arraylist of groupIds
    public void setGroupList(final String groups) {
        if (hAPI.getAuth()) {
            try {
                JSONArray groupJSON = new JSONArray(groups);
                for (int i = 0; i < groupJSON.length(); i++) {
                    groupsList.add(groupJSON.getJSONObject(i));
                }
                Log.i(TAG, "in setgroups: " + groupsList.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "groups not authorised");
        }
    }


    public void setFriendRequests(String requests){
        if (!friendRequests.isEmpty()) {
            friendRequests.clear();
        }

        try {
            JSONArray friendRequestsJSON = new JSONArray(requests);
            for (int i = 0; i < friendRequestsJSON.length(); i++) {
//                JSONObject curRequest = friendRequestsJSON.getJSONObject(i);
//                String profile = hAPI.getProfile(curRequest.getInt("profile_a"));
//                friendRequests.add(friendRequestsJSON.getJSONObject(i));
            }
        } catch (JSONException e) {
            Log.i(TAG, "setFriendsList failure: " + e);
            e.printStackTrace();
        }
        Log.i(TAG, "Friend Requests: " + friendRequests.toString());
    }




    // GETTERS
    public ArrayList<JSONObject> getFriends(){
        return friendsList;
    }


    public ArrayList<JSONObject> getGroupsList(){
        Log.i(TAG, "getgroups: " + groupsList.toString());
        return groupsList;
    }


    public ArrayList<JSONObject> getFriendRequests() {
        return friendRequests;
    }


    public String getDescription() {
        return description;
    }

    public int getAge() {
        return age;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getPrivacy() {
        return privacy;
    }

    public String getName(){
        return firstName + " " + lastName;
    }


}
