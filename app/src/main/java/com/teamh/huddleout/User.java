package com.teamh.huddleout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

    ArrayList<Integer> groupsList;
    ArrayList<Integer> friendsList;
    
    public User(int profileID, Context context) {
        this.profileID = profileID;
        hAPI = HuddlOutAPI.getInstance(context);
        this.context = context;
        friendsList = new ArrayList<Integer>();
        groupsList = new ArrayList<Integer>();
        getProfileInformation();
        getGroupList();
        getFriendsList();
        test();
    }


    public static synchronized User getInstance(int profileId, Context context){
        if(user == null){
            user = new User(profileId, context);
        }
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
                Log.i(TAG, "getProfileInformation response: " + hAPI.getResponse());
                if(hAPI.getMessage().equalsIgnoreCase("invalid params") || hAPI.getMessage().equalsIgnoreCase("not found")){
                    Log.i(TAG, "getprofileinformation error: " + hAPI.getMessage());
                }else if(hAPI.getAuth()) {
                    try {
                        JSONObject profileJSON = new JSONObject(hAPI.getResponse());
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
    private void getFriendsList() {
        RequestQueue reQueue = hAPI.getFriends();
        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                Log.i(TAG, "getFriendsList response: " + hAPI.getResponse());
                if(hAPI.getMessage().equalsIgnoreCase("invalid params") || hAPI.getMessage().equalsIgnoreCase("no friends")){
                    Log.i(TAG, "getfriendslist error: " + hAPI.getMessage());
                }else if(hAPI.getAuth()) {
                    try {
                        JSONArray profileJSON = new JSONArray(hAPI.getResponse());
                        for(int i = 0; i < profileJSON.length(); i++){
                            JSONObject j = profileJSON.getJSONObject(i);
                            friendsList.add((Integer) j.get("profile"));
                        }
                        Log.i(TAG, "friendslist: " + friendsList.toString());
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
    private void getGroupList(){
        RequestQueue reQueue = hAPI.getGroups();
        RequestQueue.RequestFinishedListener finishedListener = new RequestQueue.RequestFinishedListener() {
            @Override
            public void onRequestFinished(Request request) {
                Log.i(TAG, "getGroupsList response: " + hAPI.getResponse());
                if(hAPI.getMessage().equalsIgnoreCase("invalid params") || hAPI.getMessage().equalsIgnoreCase("no groups")){
                    Log.i(TAG, "getgroupslist error: " + hAPI.getMessage());
                }else if(hAPI.getAuth()) {

                        String output = hAPI.getResponse();

                        for(int i = 0; i < output.length(); i++){
                            if(Character.isDigit(output.charAt(i))){
                                Log.i(TAG, "true");
                            }else{
                                Log.i(TAG, "false");
                            }
                        }

                        Log.i(TAG, "groupslist: " + groupsList.toString());

                }else{
                    Log.i(TAG, "groups not authorised");
                }
            }
        };
        reQueue.addRequestFinishedListener(finishedListener);
    }



    public void test(){
        Log.i(TAG, "TEST start");
        CharSequence message = "ERROR!!!!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();

    }

    public String getName(){
        return firstName + " " + lastName;
    }

}
