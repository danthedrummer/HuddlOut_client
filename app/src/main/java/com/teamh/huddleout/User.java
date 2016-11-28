package com.teamh.huddleout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
    private String token;

    ArrayList<Integer> groupIDs;
    ArrayList<Integer> friendsList;
    
    public User(int profileID, Context context) {
        this.profileID = profileID;
        hAPI = HuddlOutAPI.getInstance(context);
        getProfileInformation();
//        getGroupList();
        getFriendsList();
    }


    public static synchronized User getInstance(int profileId, Context context){
        if(user == null){
            user = new User(profileId, context);
        }
        return user;
    }

    private void getProfileInformation() {
        RequestQueue reQueue = hAPI.getProfile(profileID);
        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if(hAPI.getAuth()) {
                    try {
                        Log.i(TAG, "Start of try");

                        JSONObject profileJSON = new JSONObject(hAPI.getResponse());
                        firstName = (String) profileJSON.get("first_name");
                        lastName = (String) profileJSON.get("last_name");
                        profilePicture = (String) profileJSON.get("profile_picture");
                        age = (Integer) profileJSON.get("age");
                        description = (String) profileJSON.get("description");
                        privacy = (String) profileJSON.get("privacy");
                        Log.i(TAG, "first name: " + firstName);
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

    // creates an arraylist of the profileIds of the user's friends
    private void getFriendsList() {
        RequestQueue reQueue = hAPI.getFriends();
        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                if(hAPI.getAuth()) {
                    try {
                        JSONObject profileJSON = new JSONObject(hAPI.getResponse());
                        friendsList.add((Integer) profileJSON.get("profile"));
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

}
