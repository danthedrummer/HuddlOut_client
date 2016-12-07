package com.teamh.huddleout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
    
    public User(int profileID, Context context) {
        this.profileID = profileID;
        hAPI = HuddlOutAPI.getInstance(context);
        this.context = context;
        friendsList = new ArrayList<JSONObject>();
        groupsList = new ArrayList<JSONObject>();
        getProfileInformation();
        setGroupList();
        setFriendsList();
        test();
    }


    public static synchronized User getInstance(int profileId, Context context){
        user = new User(profileId, context);
        return user;
    }

    public static synchronized User getInstance(Context context){
        if(user == null){
            return null;
        }
        return user;
    }

    private void getProfileInformation() {
        RequestQueue reQueue = hAPI.getProfile(profileID);
        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if(hAPI.getMessage().equalsIgnoreCase("invalid params") || hAPI.getMessage().equalsIgnoreCase("not found")){
                    Log.i(TAG, "getprofileinformation error: " + hAPI.getMessage());
                }else if(hAPI.getAuth()) {
                    try {
                        JSONObject profileJSON = new JSONObject(hAPI.getResponse());
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
                }else{
                    Log.i(TAG, "profile not authorised");
                }
            }
        };
        reQueue.addRequestFinishedListener(finishedListener);
    }


    // instantiates the arraylist of the profileIds of the user's friends
    public void setFriendsList() {
        if(!friendsList.isEmpty()){
            friendsList.clear();
        }

        RequestQueue reQueue = hAPI.getFriends();
        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if(hAPI.getMessage().equalsIgnoreCase("invalid params") || hAPI.getMessage().equalsIgnoreCase("no friends")){
                    Log.i(TAG, "getfriendslist error: " + hAPI.getMessage());
                }else if(hAPI.getAuth()) {
                    try {
                        JSONArray profileJSON = new JSONArray(hAPI.getResponse());
                        for(int i = 0; i < profileJSON.length(); i++){
                            friendsList.add(profileJSON.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG, "profile not authorised");
                }
            }
        };
        reQueue.addRequestFinishedListener(finishedListener);
    }


    // insantiates the arraylist of groupIds
    private void setGroupList(){
        RequestQueue reQueue = hAPI.getGroups();
        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if(hAPI.getMessage().equalsIgnoreCase("invalid params") || hAPI.getMessage().equalsIgnoreCase("no groups")){
                    Log.i(TAG, "getgroupslist error: " + hAPI.getMessage());
                }else if(hAPI.getAuth()) {
                    try {
                        JSONArray groupJSON = new JSONArray(hAPI.getResponse());
                        for(int i = 0; i < groupJSON.length(); i++){
                            groupsList.add(groupJSON.getJSONObject(i));
                        }
                        Log.i(TAG, "in setgroups: " + groupsList.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.i(TAG, "groups not authorised");
                }
            }
        };
        reQueue.addRequestFinishedListener(finishedListener);
    }


    public ArrayList<JSONObject> getFriends(){
        return friendsList;
    }


    public ArrayList<JSONObject> getGroupsList(){
        Log.i(TAG, "getgroups: " + groupsList.toString());
        return groupsList;
    }


    public void test(){

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

//    public void showFriend(String name, String description){
//
//        AlertDialog.Builder alert = new AlertDialog.Builder(context);
//
////            alert.setIcon(this.getActivity().getApplicationContext().getResources().getIdentifier("chess", "drawable", "com.teamh.huddleout"));
//        alert.setTitle(name);
//        alert.setMessage(description);
//
//        alert.show();
//
//    }

}
