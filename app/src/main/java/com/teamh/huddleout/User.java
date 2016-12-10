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

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Paul on 22/11/2016.
 */

public class User {

    private static final String TAG = "DevMsg";

    final HuddlOutAPI hAPI;

    private static User user;

    private int profileID;
    private int age;
    private int groupInFocus;

    private String firstName;
    private String lastName;
    private String description;
    private String profilePicture;
    private String privacy;

    private Context context;

    ArrayList<JSONObject> groupsList;
    ArrayList<JSONObject> groupInvites;

    ArrayList<JSONObject> friendsList;
    ArrayList<JSONObject> friendRequests;



    public User(int profileID, Context context) {
        this.profileID = profileID;

        hAPI = HuddlOutAPI.getInstance(context);
        this.context = context;
        friendsList = new ArrayList<JSONObject>();
        groupsList = new ArrayList<JSONObject>();
        groupInvites = new ArrayList<JSONObject>();
        friendRequests = new ArrayList<JSONObject>();
        hAPI.getProfile(profileID);
        hAPI.getGroups();
        hAPI.getFriends();
        hAPI.getFriendRequests();
        hAPI.checkInvites();
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

//                Log.i(TAG, "profile info: " + profileJSON.toString());

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
        try {
            friendsList.clear();
        }catch (IndexOutOfBoundsException e){
            Log.i(TAG, "setFriendsList clearance fail: " + e);
        }

        if(!friends.equalsIgnoreCase("no friends")){
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

    }


    // Instantiates the ArrayList of groupIds
    public void setGroupList(final String groups) {
        try {
            groupsList.clear();
        }catch (IndexOutOfBoundsException e){
            Log.i(TAG, "setFriendsList clearance fail: " + e);
        }

        if (!groups.equalsIgnoreCase("no groups")) {
            try {
                JSONArray groupJSON = new JSONArray(groups);
                for (int i = 0; i < groupJSON.length(); i++) {
                    if(!groupJSON.getJSONObject(i).getString("group_role").equalsIgnoreCase("invited")){
                        groupsList.add(groupJSON.getJSONObject(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setGroupInvites(final String invites){
        try{
            groupInvites.clear();
        }catch (IndexOutOfBoundsException e){
            Log.i(TAG, "setGroupInvites clearance fail: " + e);
        }

        if(!invites.equalsIgnoreCase("no invites")){
            try {
                JSONArray inviteJSON = new JSONArray(invites);
                for(int i = 0; i < inviteJSON.length(); i++){
                    groupInvites.add(inviteJSON.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setFriendRequests(String requests){
        try {
            friendRequests.clear();
        }catch (IndexOutOfBoundsException e){
            Log.i(TAG, "friendRequests clearance fail: " + e);
        }

        if(!requests.equalsIgnoreCase("no requests found")){
            try {
                JSONArray friendRequestsJSON = new JSONArray(requests);
                for (int i = 0; i < friendRequestsJSON.length(); i++) {
                    friendRequests.add(friendRequestsJSON.getJSONObject(i));
                }
            } catch (JSONException e) {
                Log.i(TAG, "setFriendsRequest failure: " + e);
                e.printStackTrace();
            }
        }
    }



    // GETTERS
    public ArrayList<JSONObject> getFriends(){
        return friendsList;
    }


    public ArrayList<JSONObject> getGroupsList(){
        return groupsList;
    }


    public ArrayList<JSONObject> getGroupInvites(){
        return groupInvites;
    }


    public ArrayList<JSONObject> getFriendRequests() {
        return friendRequests;
    }


    public JSONObject getGroupInFocus(){
        return groupsList.get(groupInFocus);
    }

    public void setGroupInFocus(int groupId) {
        this.groupInFocus = groupId;
    }


    public int getProfileID() {
        return profileID;
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
